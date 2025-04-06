package com.cherniva.blog.repo;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.sql.DataSource;
import java.util.List;

@SpringJUnitConfig(com.cherniva.blog.config.TestConfig.class)
public abstract class BaseRepositoryTest {

    @Autowired
    protected DataSource dataSource;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @AfterEach
    void tearDown() {
        // Clean up all tables after each test
        List<String> tables = jdbcTemplate.queryForList(
                "SELECT table_name FROM information_schema.tables WHERE table_schema = 'PUBLIC'",
                String.class
        );
        
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        for (String table : tables) {
            jdbcTemplate.execute("TRUNCATE TABLE " + table);
        }
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }
} 