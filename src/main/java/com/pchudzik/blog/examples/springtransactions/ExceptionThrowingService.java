package com.pchudzik.blog.examples.springtransactions;

import lombok.SneakyThrows;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ExceptionThrowingService {
    private final JdbcTemplate jdbcTemplate;

    public ExceptionThrowingService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void simplySavesTheData() {
        jdbcTemplate.execute("insert into test_table values('simplySavesData')");
    }

    @Transactional
    public void rollbacksOnRuntimeException() {
        jdbcTemplate.execute("insert into test_table values('rollbacksOnRuntimeException')");
        throw new RuntimeException("Rollback!");
    }

    @Transactional
    public void noRollbackOnCheckedException() throws Exception {
        jdbcTemplate.execute("insert into test_table values('noRollbackOnCheckedException')");
        throw new Exception("Simple exception");
    }

    @Transactional(rollbackOn = CustomCheckedException.class)
    public void withRollbackOnAndDeclaredException() throws CustomCheckedException {
        jdbcTemplate.execute("insert into test_table values('withRollbackForAndDeclaredException')");
        throw new CustomCheckedException("rollback me");
    }

    @Transactional(rollbackOn = CustomCheckedException.class)
    public void withRollbackOnAndRuntimeException() throws CustomCheckedException {
        jdbcTemplate.execute("insert into test_table values('withRollbackOnAndRuntimeException')");
        throw new RuntimeException("rollback me");
    }

    @Transactional(dontRollbackOn = RuntimeException.class)
    public void noRollbackOnRuntimeException() {
        jdbcTemplate.execute("insert into test_table values('noRollbackOnRuntimeException')");
        throw new IllegalStateException("Exception");
    }

    @SneakyThrows
    @Transactional(rollbackOn = CustomCheckedException.class)
    public void withRollbackForAndSneakyThrows() {
        jdbcTemplate.execute("insert into test_table values('withRollbackForAndDeclaredException!')");
        throw new CustomCheckedException("rollback me");
    }

    @SneakyThrows
    @Transactional
    public void lombokSurprise() {
        jdbcTemplate.execute("insert into test_table values('lombok!')");
        throw new Exception("Simple exception");
    }

    @Transactional(dontRollbackOn = Exception.class)
    public void noRollbackOnAnyException(Class<? extends Exception> exceptionClass) throws Exception {
        jdbcTemplate.execute("insert into test_table values('noRollbackOnAnyException')");
        throw exceptionClass.newInstance();
    }

    @Transactional(rollbackOn = Exception.class)
    public void rollbackForAnyException(Class<? extends Exception> exceptionClass) throws Exception {
        jdbcTemplate.execute("insert into test_table values('rollbackForAnyException')");
        throw exceptionClass.newInstance();
    }
}
