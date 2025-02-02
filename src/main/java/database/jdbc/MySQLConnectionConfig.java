package database.jdbc;

import java.sql.*;

public class MySQLConnectionConfig extends ConnectionConfig {

    public MySQLConnectionConfig(String host, String database, String username, String password) {
        super(host, database, username, password);
    }

    @Override
    protected String constructConnectionURL() {
        return "jdbc:mysql://" + host + "/" + database;
    }

    public static ResultSet getTables(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        String query = "SHOW TABLES";
        return statement.executeQuery(query);
    }
}
