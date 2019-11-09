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
    private final ExceptionThrowing exceptionThrowing;
    private final LombokThrowingExceptionJdkProxy lombokThrowingExceptionJdkProxy;
    private final TestDataRepository repository;

    public SpringtransactionsApplication(
            ConfigurableApplicationContext ctx,
            ExceptionThrowing exceptionThrowing,
            LombokThrowingExceptionJdkProxy lombokThrowingExceptionJdkProxy,
            TestDataRepository repository) {
        this.ctx = ctx;
        this.exceptionThrowing = exceptionThrowing;
        this.lombokThrowingExceptionJdkProxy = lombokThrowingExceptionJdkProxy;
        this.repository = repository;
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(SpringtransactionsApplication.class, args);

        SpringtransactionsApplication app = new SpringtransactionsApplication(
                ctx,
                ctx.getBean(ExceptionThrowing.class),
                ctx.getBean(LombokThrowingExceptionJdkProxy.class),
                ctx.getBean(TestDataRepository.class));

        app.testInfrastructureWorking();
        app.testRuntimeExceptionRollback();
        app.nothingIsRolledBackOnCheckedException();
        app.rollbackOnAndDeclaredException();
        app.rollbackOnAndRuntimeException();
        app.rollbackOnRuntimeException();
        app.noRollbackForRuntimeException();
        app.rollbackForAnyException(Exception.class);
        app.rollbackForAnyException(RuntimeException.class);
        app.noRollbackForAnyException(Exception.class);
        app.noRollbackForAnyException(RuntimeException.class);

        app.lombokSneakThrowsIsRolledBackForCglibProxy();
        app.lombokSneakThrowsIsRolledBackForJdkProxy();
        app.lombokSneakThrowsIsDontRolledBackForJdkProxy();
        app.lombokRollbackForOnJdkProxy();
        app.lombokRollbackOnCglibProxy();
        app.lombokDontRollbackOnCglibProxy();
    }

    private void lombokSneakThrowsIsDontRolledBackForJdkProxy() {
        execute(
                "LombokThrowingExceptionJdkProxy.dontRollbackOn",
                lombokThrowingExceptionJdkProxy::dontRollbackOn);
    }

    private void lombokDontRollbackOnCglibProxy() {
        execute(
                "ExceptionThrowing.withDontRollbackForAndSneakyThrows",
                exceptionThrowing::withDontRollbackForAndSneakyThrows);
    }

    private void rollbackOnRuntimeException() {
        execute(
                "ExceptionThrowing.doRollbackOnRuntimeException",
                exceptionThrowing::doRollbackOnRuntimeException);
    }

    private void noRollbackForAnyException(Class<? extends Exception> exceptionClass) {
        execute(
                "ExceptionThrowing.noRollbackOnAnyException " + exceptionClass.getName(),
                () -> exceptionThrowing.noRollbackOnAnyException(exceptionClass));
    }

    private void rollbackForAnyException(Class<? extends Exception> exceptionClass) {
        execute(
                "ExceptionThrowing.rollbackForAnyException " + exceptionClass.getName(),
                () -> exceptionThrowing.rollbackForAnyException(exceptionClass));
    }

    private void noRollbackForRuntimeException() {
        execute(
                "ExceptionThrowing.noRollbackOnRuntimeException",
                exceptionThrowing::noRollbackOnRuntimeException);
    }

    private void rollbackOnAndRuntimeException() {
        execute(
                "ExceptionThrowing.withRollbackOnAndRuntimeException",
                exceptionThrowing::withRollbackOnAndRuntimeException);
    }

    private void rollbackOnAndDeclaredException() {
        execute(
                "ExceptionThrowing.withRollbackOnAndDeclaredException",
                exceptionThrowing::withRollbackOnAndDeclaredException);
    }

    private void lombokRollbackOnCglibProxy() {
        execute(
                "ExceptionThrowing.withRollbackForAndSneakyThrows",
                exceptionThrowing::withRollbackForAndSneakyThrows);
    }

    private void lombokRollbackForOnJdkProxy() {
        execute(
                "LombokThrowingExceptionJdkProxy.withRollbackFor",
                lombokThrowingExceptionJdkProxy::withRollbackFor);
    }

    private void lombokSneakThrowsIsRolledBackForJdkProxy() {
        execute(
                "LombokThrowingExceptionJdkProxy.sneakyThrows",
                lombokThrowingExceptionJdkProxy::sneakyThrows);
    }

    private void lombokSneakThrowsIsRolledBackForCglibProxy() {
        execute(
                "ExceptionThrowing.lombokSurprise",
                exceptionThrowing::lombokSurprise);
    }

    private void nothingIsRolledBackOnCheckedException() {
        execute(
                "ExceptionThrowing.noRollbackOnCheckedException",
                exceptionThrowing::noRollbackOnCheckedException);
    }

    private void testRuntimeExceptionRollback() {
        execute(
                "ExceptionThrowing.rollbacksOnRuntimeException",
                exceptionThrowing::rollbacksOnRuntimeException);
    }

    public void testInfrastructureWorking() {
        execute(
                "ExceptionThrowing.simplySavesTheData",
                exceptionThrowing::simplySavesTheData);
    }


    public void execute(String name, Action action) {
        log.info("");
        log.info("Running: {}", name);
        try {
            action.execute();
            log.info("After:   {}: {}", name, repository.findAll());
        } catch (Exception ex) {
            log.info("After:   {}: {}", name, repository.findAll());
        }
        repository.clearAll();
        assert repository.findAll().isEmpty();
    }

    public interface Action {
        void execute() throws Exception;
    }

}
