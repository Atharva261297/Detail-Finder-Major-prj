package com.destro.linkcalculator.dao;

import com.destro.linkcalculator.config.PostgresConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

@Repository
public class UpdateLogDao {

    @Autowired
    private PostgresConfig postgresConfig;

    private final Logger logger = LoggerFactory.getLogger(UpdateLogDao.class);

    public boolean addNewLog(final String userName,
                             final String oIdX, final String xName,
                             final String oIdY, final String yName) {

        final String query = DaoConstants.addNewUpdateLog.replaceFirst("[?]", userName)
                                                         .replaceFirst("[?]", new Date().toString())
                                                         .replaceFirst("[?]", oIdX)
                                                         .replaceFirst("[?]", xName)
                                                         .replaceFirst("[?]", oIdY)
                                                         .replaceFirst("[?]", yName);
        try (final Statement statement = postgresConfig.getConnection().createStatement()) {
            return statement.executeUpdate(query) == 1;
        } catch (final SQLException e) {
            logger.error("Cannot insert new log for name update into database", e);
        }
        return false;
    }
}
