package com.destro.linkcalculator.dao;

import com.destro.linkcalculator.config.PostgresConfig;
import com.destro.linkcalculator.exception.DaoException;
import com.destro.linkcalculator.exception.InvalidTrainLinkFormatException;
import com.destro.linkcalculator.model.TrainLinkPair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.destro.linkcalculator.util.Constants.EMPTY_FIELD;
import static com.destro.linkcalculator.util.Constants.ONE_WAY_LINK_PATTERN;
import static com.destro.linkcalculator.util.Constants.TRAIN_NO_PATTERN;

@Repository
public class TrainTimeDao {

    @Autowired
    private PostgresConfig postgresConfig;

    public String getTimeForTrainLink(final TrainLinkPair trainLinkPair) {

        if (trainLinkPair.getTrain().matches(".*" + TRAIN_NO_PATTERN + ".*")) {
            final String trainNo = extractTrainNo(trainLinkPair.getTrain());
            final String link = extractLink(trainLinkPair.getLink());
            final String query = DaoConstants.getTrainTime.replaceFirst("[?]", trainNo)
                                                          .replaceFirst("[?]", link);

            try (final Statement statement = postgresConfig.getConnection().createStatement()) {
                final ResultSet resultSet = statement.executeQuery(query);
                if (resultSet.next()) {
                    return resultSet.getString("time");
                }

            } catch (final SQLException e) {
                throw new DaoException("Exception in getTimeForTrainLink TrainLinkPair-" + trainLinkPair, e);
            }

            return EMPTY_FIELD;
        } else {
            throw new InvalidTrainLinkFormatException("TrainLink-" + trainLinkPair + " not contains train no");
        }
    }

    private String extractLink(final String link) {
        final Matcher matcher = Pattern.compile(ONE_WAY_LINK_PATTERN).matcher(link);

        if (matcher.find()) {
            return matcher.group(0);
        }

        throw new InvalidTrainLinkFormatException("Link-" + link + " not of valid format");
    }

    private String extractTrainNo(final String train) {
        final Matcher matcher = Pattern.compile(TRAIN_NO_PATTERN).matcher(train);

        if (matcher.find()) {
            return matcher.group(0);
        }

        throw new InvalidTrainLinkFormatException("TrainNo-" + train + " not of valid format");
    }
}
