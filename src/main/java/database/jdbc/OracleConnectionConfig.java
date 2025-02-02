package database.jdbc;

import engine.exceptions.DataBaseException;

import java.sql.*;

public class OracleConnectionConfig extends ConnectionConfig {

    public OracleConnectionConfig(String host, String database, String username, String password) {
        super(host, database, username, password);
    }

    @Override
    protected String constructConnectionURL() {
        return "jdbc:oracle:thin:@" + host + ":" + database;
    }

    public static ResultSet getTables(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        String query = "SELECT table_name FROM user_tables";
        return statement.executeQuery(query);
    }
}
