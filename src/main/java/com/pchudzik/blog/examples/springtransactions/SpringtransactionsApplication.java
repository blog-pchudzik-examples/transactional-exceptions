package com.pchudzik.blog.examples.springtransactions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SpringtransactionsApplication {
    private static final Logger log = LoggerFactory.getLogger(SpringtransactionsApplication.class);

    private final ConfigurableApplicationContext ctx;
    private final ExceptionThrowingService exceptionThrowingService;
    private final TestDataRepository repository;

    public SpringtransactionsApplication(
            ConfigurableApplicationContext ctx,
            ExceptionThrowingService exceptionThrowingService,
            TestDataRepository repository) {
        this.ctx = ctx;
        this.exceptionThrowingService = exceptionThrowingService;
        this.repository = repository;
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(SpringtransactionsApplication.class, args);

        SpringtransactionsApplication app = new SpringtransactionsApplication(
                ctx,
                ctx.getBean(ExceptionThrowingService.class),
                ctx.getBean(TestDataRepository.class));

        app.testInfrastructureWorking();
        app.testRuntimeExceptionRollback();
        app.nothingIsRolledBackOnCheckedException();
        app.rollbackOnAndDeclaredException();
        app.rollbackOnAndRuntimeException();
        app.noRollbackForRuntimeException();
        app.rollbackForAnyException(Exception.class);
        app.rollbackForAnyException(RuntimeException.class);
        app.noRollbackForAnyException(Exception.class);
        app.noRollbackForAnyException(RuntimeException.class);

        app.lombokSneakThrowsIsRolledBackForCglibProxy();
        app.lombokSneakThrowsIsRolledBackForJdkProxy();
        app.lombokRollbackForOnJdkProxy();
        app.lombokRollbackOnCglibProxy();
    }

    private void noRollbackForAnyException(Class<? extends Exception> exceptionClass) {
        execute("DontRollbackOn " + exceptionClass.getName(), () -> {
            try {
                exceptionThrowingService.noRollbackOnAnyException(exceptionClass);
            } catch (Exception ex) {
                log.info("dontRollbackOn {}: {}", exceptionClass.getName(), repository.findAll());
            }
        });
    }

    private void rollbackForAnyException(Class<? extends Exception> exceptionClass) {
        execute("Rollback for " + exceptionClass.getName(), () -> {
            try {
                exceptionThrowingService.rollbackForAnyException(exceptionClass);
            } catch (Exception ex) {
                log.info("Rollback for {}: {}", exceptionClass, repository.findAll());
            }
        });
    }

    private void noRollbackForRuntimeException() {
        execute("DontRollbackOn RuntimeException", ()->{
            try {
                exceptionThrowingService.noRollbackOnRuntimeException();
            } catch (Exception ex) {
                log.info("After dontRollbackOn RuntimeException and throwing runtime exception {}", repository.findAll());
            }
        });
    }

    private void rollbackOnAndRuntimeException() {
        execute("RollbackOn declared and throwing runtime exception", () -> {
            try {
                exceptionThrowingService.withRollbackOnAndRuntimeException();
            } catch (Exception ex) {
                log.info("After rollbackOn and throwing runtime exception {}", repository.findAll());
            }
        });
    }

    private void rollbackOnAndDeclaredException() {
        execute("RollbackOn declared and throwing declared exception", () -> {
            try {
                exceptionThrowingService.withRollbackOnAndDeclaredException();
            } catch (Exception ex) {
                log.info("After rollbackOn and throwing declared exception {}", repository.findAll());
            }
        });
    }

    private void lombokRollbackOnCglibProxy() {
        execute(
                "Rollback on with @SneakyThrows", () -> {
                    try {
                        exceptionThrowingService.withRollbackForAndSneakyThrows();
                    } catch (Exception ex) {
                        log.info("After rollbackOn and sneaky throws on cglib proxy: {}", repository.findAll());
                    }
                });
    }

    private void lombokRollbackForOnJdkProxy() {
        execute(
                "Lombok rollback on jdk proxy", () -> {
                    try {
                        lombokThrowingException().withRollbackFor();
                    } catch (Exception ex) {
                        log.info("After rollbackOn and sneaky throws on jdk proxy: {}", repository.findAll());
                    }
                });
    }

    private void lombokSneakThrowsIsRolledBackForJdkProxy() {
        execute(
                "Lombok on jdk proxy", () -> {
                    try {
                        lombokThrowingException().sneakyThrows();
                    } catch (Exception ex) {
                        log.info("After lombok sneaky throws on jdk proxy: {}", repository.findAll());
                    }
                });
    }

    private LombokThrowingException lombokThrowingException() {
        return ctx.getBean(LombokThrowingException.class);
    }

    private void lombokSneakThrowsIsRolledBackForCglibProxy() {
        execute(
                "Sneaky throws from lombok", () -> {
                    try {
                        exceptionThrowingService.lombokSurprise();
                    } catch (Exception ex) {
                        log.info("After @SneakyThrows: {}", repository.findAll());
                    }
                });
    }

    private void nothingIsRolledBackOnCheckedException() {
        execute(
                "Checked exception and no rollback", () -> {
                    try {
                        exceptionThrowingService.noRollbackOnCheckedException();
                    } catch (Exception ex) {
                        log.info("After catching checked exception: {}", repository.findAll());
                    }
                });
    }

    private void testRuntimeExceptionRollback() {
        execute(
                "RuntimeException rollback", () -> {
                    try {
                        exceptionThrowingService.rollbacksOnRuntimeException();
                    } catch (RuntimeException ex) {
                        log.info("After throwing exception: {}", repository.findAll());
                    }
                });
    }

    public void testInfrastructureWorking() {
        execute(
                "Infrastructure test",
                () -> {
                    exceptionThrowingService.simplySavesTheData();
                    log.info("Found data: {}", repository.findAll());
                });
    }


    public void execute(String name, Action action) {
        log.info("====================");
        log.info("Running: {}", name);
        action.execute();
        repository.clearAll();
        assert repository.findAll().isEmpty();
    }

    public interface Action {
        void execute();
    }

}
