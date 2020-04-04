package com.destro.linkcalculator.dao;

import com.destro.linkcalculator.config.PostgresConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BatchesDao {

    @Autowired
    private PostgresConfig postgresConfig;

    private final Logger logger = LoggerFactory.getLogger(BatchesDao.class);

    public List<String> getAllBatches() {
        try (final Statement statement = postgresConfig.getConnection().createStatement()) {
            final List<String> batches = new ArrayList<>();
            final var resultSet = statement.executeQuery(DaoConstants.getAllBatches);
            while (resultSet.next()) {
                batches.add(resultSet.getString("batch"));
            }
            return batches;
        } catch (final SQLException e) {
            logger.error("Cannot read list of batches from database", e);
        }
        return new ArrayList<>();
    }

    public String getStartDateForBatch(final String name) {
        final String query = DaoConstants.getStartDateForBatch.replaceFirst("[?]", name);
        try (final Statement statement = postgresConfig.getConnection().createStatement()) {
            final ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return resultSet.getString("start_date");
            }
        } catch (final SQLException e) {
            logger.error("Cannot get start_date for batch-{}", name, e);
        }
        return null;
    }

    public Integer getNoOfMembers(final String name) {
        final String query = DaoConstants.getNoOfMembers.replaceFirst("[?]", name);
        try (final Statement statement = postgresConfig.getConnection().createStatement()) {
            final ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return resultSet.getInt("no_of_members");
            }
        } catch (final SQLException e) {
            logger.error("Cannot get noOfMembers for batch-{}", name, e);
        }
        return null;
    }
}
