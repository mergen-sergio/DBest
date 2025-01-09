package database.jdbc;

import java.sql.*;

public class PostgreSQLConnectionConfig extends ConnectionConfig {

    public PostgreSQLConnectionConfig(String host, String database, String username, String password) {
        super(host, database, username, password);
    }

    @Override
    protected String constructConnectionURL() {
        return "jdbc:postgresql://" + host + "/" + database;
    }

    public static ResultSet getTables(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        String query = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'";
        return statement.executeQuery(query);
    }
}
