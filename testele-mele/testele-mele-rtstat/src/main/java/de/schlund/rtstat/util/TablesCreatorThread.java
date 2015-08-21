package de.schlund.rtstat.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import de.schlund.rtstat.db.DbConnection;

/**
 * This thread is responsible with daily table creation.
 * Every 12 hours it tries to create a new table for 2 days ahead.
 * 
 * @author vladcrc
 *
 */
public class TablesCreatorThread implements Runnable {
    
    public static final Logger LOG = Logger.getLogger(TablesCreatorThread.class);
    
    private DbConnection dbConnection;
    
    private int oldTableExpiration;
    
    private static final SimpleDateFormat GMT_DATE_FORMATTER = new SimpleDateFormat("yyyyMMddHHmmss");

    public TablesCreatorThread(DbConnection dbConn, int oldTableExpiration) throws SQLException {
        this.dbConnection = dbConn;
        this.oldTableExpiration = oldTableExpiration;
        
        // check if today's table exists and if not then creates it
        LOG.info("Creating new table...");
        long currentTime = System.currentTimeMillis();
        String dateString = GMT_DATE_FORMATTER.format(new Date(currentTime)).substring(0,8);                
        String tableName = Constants.CDR_TABLE + "_"  + dateString;
        String stmtString = "CREATE TABLE IF NOT EXISTS " + tableName + " LIKE cdr_template";
        PreparedStatement preparedStatement = dbConnection.getConnection().prepareStatement(stmtString);
        int result = preparedStatement.executeUpdate();
        LOG.info("Today's table " + tableName + " succesfully created.");
    }

    public void run() {

        String tableName = null;
        PreparedStatement preparedStatement = null;
        String stmtString = null;
        int result;
        
        while (true) {
            try {
                long currentTime = System.currentTimeMillis();
                
                // create the table for two days ahead
                LOG.info("Creating new table...");
                String dateString = GMT_DATE_FORMATTER.format(new Date(currentTime + 2*86400000)).substring(0,8);                
                tableName = Constants.CDR_TABLE + "_"  + dateString;
                stmtString = "CREATE TABLE IF NOT EXISTS " + tableName + " LIKE cdr_template";
                preparedStatement = dbConnection.getConnection().prepareStatement(stmtString);
                result = preparedStatement.executeUpdate();
                LOG.info("Table " + tableName + " succesfully processed for create.");
                
                // delete the tables older than 8 weeks
                LOG.info("Deleting old table...");
                dateString = GMT_DATE_FORMATTER.format(new Date(currentTime - oldTableExpiration*7L*86400000L)).substring(0,8);
                tableName = Constants.CDR_TABLE + "_"  + dateString;
                stmtString = "DROP TABLE IF EXISTS " + tableName;
                preparedStatement = dbConnection.getConnection().prepareStatement(stmtString);
                result = preparedStatement.executeUpdate();
                LOG.info("Table " + tableName + " succesfully processed for drop.");
            } catch (Exception e) {
                LOG.error("Error executing sql statements: ", e);
            } finally {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    LOG.error("Couldn't close the PreparedStatement.");
                }
            }
            
            try {
                Thread.sleep(720 * 60 * 1000);
            } catch (InterruptedException e) {
                LOG.error(e);
            }
        }
    }

}
