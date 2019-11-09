package com.pchudzik.blog.examples.springtransactions;

import lombok.SneakyThrows;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class LombokThrowingExceptionJdkProxyImpl implements LombokThrowingExceptionJdkProxy {
    private final JdbcTemplate jdbcTemplate;

    public LombokThrowingExceptionJdkProxyImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @SneakyThrows
    @Override
    public void sneakyThrows() {
        jdbcTemplate.execute("insert into test_table values('lombok!')");
        throw new Exception("Simple exception");
    }

    @SneakyThrows
    @Override
    public void withRollbackFor() {
        jdbcTemplate.execute("insert into test_table values('LombokThrowingExceptionImpl.withRollbackFor')");
        throw new CustomCheckedException("Rollback?");
    }

    @SneakyThrows
    @Override
    public void dontRollbackOn() {
        jdbcTemplate.execute("insert into test_table values('dontRollbackOn')");
        throw new CustomCheckedException("Rollback?");
    }
}
