package com.destro.linkcalculator.dao;

class DaoConstants {

    static String getTrainAndLinkById = "select ?_train, ?_link from link_schema.link where id='?'";
    static String getStaffByTrainLink = "select id from link_schema.link " +
            "where ?_train='?' and ?_link='?' and id like '?%'";

    static String addIssue = "INSERT INTO link_schema.issues(type, description, contact, name, creation_date, " +
            "status) VALUES('?', '?', '?', '?', '?', '?');";
    static String getAllIssuesForName = "select * from link_schema.issues where name='?'";

    static String getNoOfMembers = "select no_of_members from link_schema.batches where batch='?'";
    static String getStartDateForBatch = "select start_date from link_schema.batches where batch='?'";
    static String getAllBatches = "select batch from link_schema.batches";

    static String addNewUpdateLog = "INSERT INTO link_schema.update_log (reporter_name, update_date, o_id_x, x_name, " +
            "o_id_y, y_name) VALUES('?', '?', '?', '?', '?', '?')";

    static String getAllStaff = "select name from link_schema.names where id like '?%' order by id";
    static String getIdForStaff = "select id from link_schema.names where name='?'";
    static String getNameForId = "select name from link_schema.names where id='?'";
    static String updateName = "UPDATE link_schema.names SET name='?' WHERE id='?'";
    static String insertName = "insert into link_schema.names (id, name) values('?','?')";
    static String getAllStaffWithIds = "select * from link_schema.names";

    static String getTrainTime = "select time from link_schema.train_time where train='?' and link='?'";

    private DaoConstants() {
    }
}
