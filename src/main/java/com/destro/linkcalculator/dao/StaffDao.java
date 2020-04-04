package com.destro.linkcalculator.dao;

import com.destro.linkcalculator.config.PostgresConfig;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class StaffDao {

    @Autowired
    private PostgresConfig postgresConfig;

    private final Logger logger = LoggerFactory.getLogger(StaffDao.class);

    public List<String> getALlStaff(final String batchName) {
        final String query = DaoConstants.getAllStaff.replace("?", batchName);
        try (final Statement statement =
                     postgresConfig.getConnection().createStatement()) {
            final List<String> names = new ArrayList<>();
            final var resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                names.add(resultSet.getString("name"));
            }
            return names;
        } catch (final SQLException e) {
            logger.error("Cannot read list of batches from database", e);
        }
        return null;
    }

    public String getIdForName(final String name) {
        final String query = DaoConstants.getIdForStaff.replace("?", name);
        try (final Statement statement = postgresConfig.getConnection().createStatement()) {
            final ResultSet resultSet = statement.executeQuery(query);
            String id = StringUtils.EMPTY;
            while (resultSet.next()) {
                id = resultSet.getString("id");
                if (!id.contains("substitute") || !id.contains("anonymous")) {
                    return id;
                }
            }
            return id;
        } catch (final SQLException e) {
            logger.error("Cannot get id for name-{}", name, e);
            return null;
        }
    }

    public boolean updateStaffName(final String id, final String newName) {
        final String query = DaoConstants.updateName.replaceFirst("[?]", newName).replaceFirst("[?]", id);
        try (final Statement statement = postgresConfig.getConnection().createStatement()) {
            return statement.executeUpdate(query) == 1;
        } catch (final SQLException e) {
            logger.error("Cannot update name in database", e);
        }
        return false;
    }

    public String getNameForId(final String id) {
        final String query = DaoConstants.getNameForId.replace("?", id);
        try (final Statement statement = postgresConfig.getConnection().createStatement()) {
            final ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next())
                return resultSet.getString("name");
            else
                return StringUtils.EMPTY;
        } catch (final SQLException e) {
            logger.error("Cannot get id for name-{}", id, e);
            return null;
        }
    }

    public boolean insertName(final String id, final String name) {
        final String query = DaoConstants.insertName.replaceFirst("[?]", id).replaceFirst("[?]", name);
        try (final Statement statement = postgresConfig.getConnection().createStatement()) {
            return statement.executeUpdate(query) == 1;
        } catch (final SQLException e) {
            logger.error("Cannot insert name in database", e);
        }
        return false;
    }

    public Map<String, String> getAllStaffWithIds() {
        final Map<String, String> namesCache = new HashMap<>();
        try (final Statement statement = postgresConfig.getConnection().createStatement()) {
            final ResultSet resultSet = statement.executeQuery(DaoConstants.getAllStaffWithIds);
            while (resultSet.next())
                namesCache.put(resultSet.getString("id"), resultSet.getString("name"));
        } catch (final SQLException e) {
            logger.error("Cannot create name cache", e);
        }
        return namesCache;
    }
}
