package com.pchudzik.blog.examples.springtransactions;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TestDataRepository {
    private final JdbcTemplate jdbcTemplate;

    public TestDataRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TestData> findAll() {
        return jdbcTemplate.query(
                "select * from test_table",
                (resultSet, i) -> new TestData(resultSet.getString(1)));
    }

    public void clearAll() {
        jdbcTemplate.execute("delete from test_table");
    }

    public static class TestData {
        public final String value;

        public TestData(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "TestData{value='" + value + '\'' + '}';
        }
    }
}
