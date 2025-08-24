package com.hiri.crediya.r2dbc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapter.r2dbc.postgresql")
public record PostgresqlConnectionProperties(
        String host,
        Integer port,
        String database,
        String schema,
        String username,
        String password,
        Pool pool
) {
    public record Pool(Integer initialSize, Integer maxSize, Integer maxIdleMinutes) {}
}