package ibd.table.jdbc;

import ibd.table.prototype.Header;

public class OracleTable extends JDBCTable {

    public OracleTable(Header header) {
        super(header);
        this.header.set("table-type", "OracleTable");
    }

    public OracleTable(Header header, String connectionUrl, String connectionUser, String connectionPassword) {
        super(header, connectionUrl, connectionUser, connectionPassword);
    }
}
