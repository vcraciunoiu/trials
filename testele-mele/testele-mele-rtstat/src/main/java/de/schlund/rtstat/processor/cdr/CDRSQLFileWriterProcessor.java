package de.schlund.rtstat.processor.cdr;

import java.io.IOException;

import de.schlund.rtstat.model.cdr.CDR;
import de.schlund.rtstat.processor.Processor;

/**
 * writes SQL statements into a file, uses the Log4j category
 * de.schlund.rtstat.processor.cdr.CDRSQLFileWriter.sql
 * 
 * @author Frank Spychalski (<a
 *         href="mailto:spychalski@schlund.de">spychalski@schlund.de</a>)
 *         
 * UPDATED: executes sql statements into db, not write in file anymore
 * 
 */
public class CDRSQLFileWriterProcessor extends Processor<CDR> {

    private CDRSQLFileWriter writer;

    public CDRSQLFileWriterProcessor(String name, int capacity) throws IOException {
        super(name, capacity);
    }

    public CDRSQLFileWriterProcessor(String name) throws IOException {
        super(name);
    }

    @Override
    protected void processEvent(CDR cdr) {
        if (  !(cdr.getState().equals(CDR.STATE.ACK_DISCARDED) || cdr.getState().equals(CDR.STATE.BYE_DISCARDED))  ) {
            writer.write(cdr);
        }
    }

    public void setWriter(CDRSQLFileWriter writer) {
    	this.writer = writer;
    }
}
