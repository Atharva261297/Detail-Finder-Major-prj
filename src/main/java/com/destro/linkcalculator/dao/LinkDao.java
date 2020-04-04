package com.destro.linkcalculator.dao;

import com.destro.linkcalculator.config.PostgresConfig;
import com.destro.linkcalculator.model.TrainLinkPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Repository
public class LinkDao {

    @Autowired
    private PostgresConfig postgresConfig;

    private final Logger logger = LoggerFactory.getLogger(LinkDao.class);

    public TrainLinkPair getTrainAndLinkById(final String day, final String id) {
        final String query = DaoConstants.getTrainAndLinkById.replaceFirst("[?]", day)
                                                             .replaceFirst("[?]", day)
                                                             .replaceFirst("[?]", id);
        try (final Statement statement = postgresConfig.getConnection().createStatement()) {
            final ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return new TrainLinkPair(resultSet.getString(day + "_train"), resultSet.getString(day + "_link"));
            }
        } catch (final SQLException e) {
            logger.error("Cannot get train and link for id-{}, day-{}", id, day, e);
        }
        return null;
    }

    public String getStaffByTrainLink(final String day, final TrainLinkPair trainLinkPair, final String batchAndRole) {
        final String query = DaoConstants.getStaffByTrainLink.replaceFirst("[?]", day)
                                                             .replaceFirst("[?]", trainLinkPair.getTrain())
                                                             .replaceFirst("[?]", day)
                                                             .replaceFirst("[?]", trainLinkPair.getLink())
                                                             .replaceFirst("[?]", batchAndRole);
        try (final Statement statement = postgresConfig.getConnection().createStatement()) {
            final ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return resultSet.getString("id");
            }
        } catch (final SQLException e) {
            logger.error("Cannot get staff id for trainLink-{} day-{}", trainLinkPair, day, e);
        }
        return null;
    }
}
