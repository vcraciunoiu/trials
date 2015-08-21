package de.schlund.rtstat.processor.cdr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import de.schlund.rtstat.model.cdr.CDR;

public class CDRSQLFileWriterOffline extends CDRSQLFileWriter {

    private Logger LOG = Logger.getLogger(CDRSQLFileWriterOffline.class);

    SimpleDateFormat GMT_DATE_FORMATTER = new SimpleDateFormat("yyyyMMddHHmmss");

    @Override
    public void write(CDR cdr) {
        try {
            StringBuilder bulkInsertString;
            String table;

            table = "cdr_" + GMT_DATE_FORMATTER.format(new Date(cdr.getStarttime())).substring(0, 8);
            
            if (isAlreadyInTable(cdr.getCDRCall_id(), table)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("callid: " + cdr.getCDRCall_id() + " is already in table");
                }
                stmt.executeUpdate("delete from " + table + " where callid = '" + cdr.getCDRCall_id() + "'");
            }

            bulkInsertString = new StringBuilder();
            initializeInsertString(bulkInsertString, table);
            appendChunk(bulkInsertString, cdr);
            writeToDb(bulkInsertString);
        } catch (Exception e) {
            LOG.error("Error when writing CDR: " + cdr, e);
        }
    }

    private boolean isAlreadyInTable(String callid, String table) throws SQLException {
        boolean result = false;
        ResultSet rs = stmt.executeQuery("select dauer from " + table + " where callid = '" + callid + "'");
        if (rs.last()) {
            result = true;
        }
        rs.close();
        return result;
    }

}
