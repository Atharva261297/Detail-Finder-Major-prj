package com.destro.linkcalculator.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service
public class PostgresConfig {

    private final Logger logger = LoggerFactory.getLogger(PostgresConfig.class);

    @Value("${db-name}")
    private String dbName;

    @Value("${remote-db-uri}")
    private String remoteDbUri;

    @Value("${local-db-uri}")
    private String localDbUri;

    @Value("${db-pass}")
    private String password;

    @Value("${db-user}")
    private String username;

    private Connection connection;

    @PostConstruct
    public void initConnection() {
        try {
            String url = localDbUri;
            if (dbName.equals("remote")) {
                final URI dbUri = new URI(remoteDbUri);
                username = dbUri.getUserInfo().split(":")[0];
                password = dbUri.getUserInfo().split(":")[1];
                url = "jdbc:postgresql://" + dbUri.getHost() + ':'
                        + dbUri.getPort() + dbUri.getPath();
            }
            connection = DriverManager.getConnection(url, username, password);
            logger.info("Database Connection Successful");
        } catch (final SQLException | URISyntaxException e) {
            logger.error("Cannot establish database connection", e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

}