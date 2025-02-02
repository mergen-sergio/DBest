package ibd.table.jdbc;

import ibd.table.prototype.Header;

public class MySQLTable extends JDBCTable {

    public MySQLTable(Header header) {
        super(header);
        this.header.set("table-type", "MySQLTable");
    }

    public MySQLTable(Header header, String connectionUrl, String connectionUser, String connectionPassword) {
        super(header, connectionUrl, connectionUser, connectionPassword);
    }
}
