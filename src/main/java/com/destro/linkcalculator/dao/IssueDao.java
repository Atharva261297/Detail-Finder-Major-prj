package com.destro.linkcalculator.dao;

import com.destro.linkcalculator.config.PostgresConfig;
import com.destro.linkcalculator.model.IssueModel;
import com.destro.linkcalculator.model.IssueStatus;
import com.destro.linkcalculator.model.IssueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class IssueDao {

    @Autowired
    private PostgresConfig postgresConfig;

    private final Logger logger = LoggerFactory.getLogger(IssueDao.class);

    public boolean addIssue(final IssueModel issue) {
        final String query = DaoConstants.addIssue.replaceFirst("[?]", issue.getIssueType().name())
                                                  .replaceFirst("[?]", issue.getIssueDescription())
                                                  .replaceFirst("[?]", issue.getContactInfo())
                                                  .replaceFirst("[?]", issue.getReporterName())
                                                  .replaceFirst("[?]", new Date().toString())
                                                  .replaceFirst("[?]", IssueStatus.New.name());
        try (final Statement statement = postgresConfig.getConnection().createStatement()) {
            return statement.executeUpdate(query) == 1;
        } catch (final SQLException e) {
            logger.error("Cannot raise issue-{}", issue, e);
        }
        return false;
    }

    public List<IssueModel> getAllIssuesForName(final String name) {
        final String query = DaoConstants.getAllIssuesForName.replaceFirst("[?]", name);
        final List<IssueModel> issuesList = new ArrayList<>();
        try (final Statement statement = postgresConfig.getConnection().createStatement()) {
            final ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                issuesList.add(
                        new IssueModel(
                                IssueType.valueOf(resultSet.getString("type")),
                                resultSet.getString("description"),
                                resultSet.getString("contact"),
                                resultSet.getString("name"),
                                resultSet.getString("status"),
                                resultSet.getDate("creation_date")));
            }
        } catch (final SQLException e) {
            logger.error("Cannot get list of the issues for name-{}", name, e);
        }
        return issuesList;
    }
}
