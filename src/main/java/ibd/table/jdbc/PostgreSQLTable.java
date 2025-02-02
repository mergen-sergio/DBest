package ibd.table.jdbc;

import ibd.table.prototype.Header;

public class PostgreSQLTable extends JDBCTable{

    public PostgreSQLTable(Header header) {
        super(header);
        this.header.set("table-type", "PostgreSQLTable");
    }

    public PostgreSQLTable(Header header, String connectionUrl, String connectionUser, String connectionPassword) {
        super(header, connectionUrl, connectionUser, connectionPassword);
    }
}
