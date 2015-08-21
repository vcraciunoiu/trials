/**
 * 
 */
package de.schlund.rtstat.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.apache.log4j.Logger;

/**
 * This class takes care of connection to database. Classes/processors which
 * need to work with db have a property of type DbConnection.
 * 
 * @author Vladcrc
 * 
 */
public class DbConnection {
    private static Logger LOG = Logger.getLogger(DbConnection.class);

    private Connection connection;
    private Statement statement;

    public DbConnection(String serverName, Integer port, String database, String username, String password, Integer socketTimeout) {
        try {
            String driverName = "com.mysql.jdbc.Driver";
            Class.forName(driverName);
            String url = "jdbc:mysql://" + serverName + 
                         ":" + port +
            		 "/" + database + 
                         "?autoReconnect=true" + 
                         "&innodb_locks_unsafe_for_binlog=true" + 
                         "&socketTimeout=" + socketTimeout
//                         + "&useTimezone=true&serverTimezone=GMT"
                         ;
            connection = DriverManager.getConnection(url, username, password);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            statement = connection.createStatement();
//            statement.executeUpdate("set time_zone='+00:00'");
        } catch (Exception e) {
            LOG.error("Error trying to connect to Mysql server " + serverName, e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public Statement getStatement() {
        return statement;
    }
}
