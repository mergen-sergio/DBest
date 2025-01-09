package enums;

public enum JDBCDriver {
    MYSQL("MySQL"),
    POSTGRESQL("PostgreSQL"),
    ORACLE("Oracle");

    public final String NAME;

    JDBCDriver(String driverClassName) {
        this.NAME = driverClassName;
    }

    public String getDriverClassName() {
        return NAME;
    }
}
