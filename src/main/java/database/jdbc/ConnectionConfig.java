package database.jdbc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import engine.exceptions.DataBaseException;

import java.io.*;
import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class ConnectionConfig {

    public static final String CONFIG_FILES_FOLDER = "conf/jdbc/connections/";

    @SerializedName("id")
    private final String id;

    @SerializedName("host")
    public String host;

    @SerializedName("username")
    public String username;

    @SerializedName("password")
    public String password;

    @SerializedName("database")
    public String database;

    @SerializedName("connectionURL")
    public String connectionURL;

    @SerializedName("type")
    public String type = getClass().getName();

    protected String constructConnectionURL() {
        return null;
    }

    public ConnectionConfig(String host, String database, String username, String password) {
        this.id = String.valueOf(new Date().getTime());
        this.host = host;
        this.username = username;
        this.password = password;
        this.database = database;
        this.connectionURL = constructConnectionURL();

        createConfigFolder();
    }

    public boolean test() {
        try {
            DriverManager.getConnection(connectionURL, username, password);

            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * TODO: Review logic
     */
    public ArrayList<String> getTableNames() {

        try (Connection connection = DriverManager.getConnection(connectionURL, username, password)) {
            if (isMySQL()) {
                ResultSet resultSet = MySQLConnectionConfig.getTables(connection);
                return extractTableNames(resultSet);
            } else if (isPostgreSQL()) {
                ResultSet resultSet = PostgreSQLConnectionConfig.getTables(connection);
                return extractTableNames(resultSet);
            } else if (isOracle()) {
                ResultSet resultSet = OracleConnectionConfig.getTables(connection);
                return extractTableNames(resultSet);
            }
        } catch (SQLException e) {
            throw new DataBaseException("ConnectionConfig", e.getMessage());
        }

        return new ArrayList<>();
    }

    private ArrayList<String> extractTableNames(ResultSet resultSet) {
        ArrayList<String> tableNames = new ArrayList<>();
        try {
            while (resultSet.next()) {
                tableNames.add(resultSet.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tableNames;
    }

    private void createConfigFolder() {
        File folder = new File(CONFIG_FILES_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public void save() {
        String filePath = CONFIG_FILES_FOLDER + id + ".json";

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        String json = gson.toJson(this);

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ConnectionConfig load(String id) {
        String filePath = CONFIG_FILES_FOLDER + id + ".json";

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        try {
            FileReader reader = new FileReader(filePath);
            return gson.fromJson(reader, ConnectionConfig.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void delete() {
        String filePath = CONFIG_FILES_FOLDER + id + ".json";
        File configFile = new File(filePath);

        if (configFile.exists() && configFile.isFile()) {
            configFile.delete();
        }
    }

    public static ArrayList<ConnectionConfig> getAllConfiguredConnections() {
        ArrayList<ConnectionConfig> configurations = new ArrayList<>();
        for (String configFile : getAllConfigurationFiles()) {
            ConnectionConfig config = load(configFile.replaceAll("\\.json$", ""));
            if (config != null) {
                configurations.add(config);
            }
        }

        return configurations;
    }

    public static ArrayList<String> getAllConfigurationFiles() {
        ArrayList<String> configFiles = new ArrayList<>();

        File directory = new File(CONFIG_FILES_FOLDER);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".json")) {
                        configFiles.add(file.getName());
                    }
                }
            }
        }

        return configFiles;
    }

    public boolean isMySQL() {
        return type.equals(MySQLConnectionConfig.class.getName());
    }

    public boolean isPostgreSQL() {
        return type.equals(PostgreSQLConnectionConfig.class.getName());
    }

    public boolean isOracle() {
        return type.equals(OracleConnectionConfig.class.getName());
    }

    public String getTableType() {
        if (isMySQL()) {
            return "MySQLTable";
        } else if (isPostgreSQL()) {
            return "PostgreSQL";
        } else if (isOracle()) {
            return "Oracle";
        } else {
            throw new InvalidParameterException("Connection config passed as parameter isn't mapped!");
        }
    }
}
