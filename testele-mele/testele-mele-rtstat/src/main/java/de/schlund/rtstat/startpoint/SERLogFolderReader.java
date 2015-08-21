package de.schlund.rtstat.startpoint;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import de.schlund.rtstat.model.SERLogEvent;
import de.schlund.rtstat.model.SERLogEventFactory;

/**
 * This class is one of the two starting points for event processing. It takes
 * the files from a given folder, parses the input into events and feeds them
 * into an event consumer.
 * 
 * @author mic
 */
public class SERLogFolderReader extends SERLogProcessorFeeder {

    final static Logger LOG = Logger.getLogger(SERLogFolderReader.class);

    private final static Charset CHARSET = Charset.forName("ISO-8859-1");

    private FileChannel _fileChannel;

    private File folder = null;

    private int checkEveryXMinutes;

    private SERLogEventFactory parser;

    public SERLogFolderReader(String name, int checkEveryXMinutes) {
        super(name);
        this.checkEveryXMinutes = checkEveryXMinutes;
    }

    public void setFolder(String folderName) throws IOException {
        if (folderName == null || folderName.length() == 0) {
            LOG.error("No folder is specified in bean.xml. Please correct this.");
            throw new IOException("No folder is specified in bean.xml.");
        } else {
            folder = new File(folderName);
        }
    }

    public void run() {
        TimerTask timerTask = new TimerTask() {
            public void run() {
                try {
                    // always get the first file from folder
                    if (folder != null && folder.listFiles() != null && folder.listFiles().length != 0) {
                        File fileToProcess = folder.listFiles()[0].getAbsoluteFile();
                        LOG.info("File to process is: " + fileToProcess.getName());
                        FileInputStream fis = new FileInputStream(fileToProcess);
                        _fileChannel = fis.getChannel();
                        if (_fileChannel == null) {
                            throw new IllegalStateException("FileChannel == null");
                        }
                        ByteBuffer bb;

                        bb = ByteBuffer.allocateDirect(1 << 16);
                        bb.clear();

                        while (true) {
                            if (_fileChannel.read(bb) == -1) {
                                LOG.info("Finished processing file " + fileToProcess.getName());
                                // after file processing has finished, delete the file
                                fis.close();
                                fileToProcess.delete();
                                break;
                            }
                            // prepare buffer for reading.
                            bb.flip();
                            // advance buffer as long as there are any more
                            // lines
                            while (nextLine(bb)) {
                                // do nothing
                            }
                            // compact buffer
                            bb.compact();
                        }
                    }
                } catch (Exception e) {
                    LOG.error(e);
                }
            }
        };
        new Timer("SERLogFolderReader-timer").schedule(timerTask, 0, checkEveryXMinutes * 60 * 1000L);
    }

    private boolean nextLine(ByteBuffer bb) {
        // new (logical) buffer
        ByteBuffer slice = bb.slice();
        // find end of line
        int p = eol(slice);
        // have we found a line break?
        if (p != -1) {
            // set the buffer slice's limit to the end of line
            slice.limit(p);
            // advance the position of the input buffer by this line's length
            bb.position(bb.position() + p + 1);
            // decode to java characters
            SERLogEvent event = parser.parse(CHARSET.decode(slice));
            // start event parsing and processing
            process(event);
            // return true because we found a line
            return true;
        }
        // return false because we found no line
        return false;
    }

    private int eol(ByteBuffer bb) {
        // mark position in buffer
        bb.mark();
        // as long as there is any character left in this buffer
        while (bb.hasRemaining()) {
            // is the next character in the buffer a 'newline'?
            if (bb.get() == '\n') {
                // store its position
                int p = bb.position() - 1;
                // reset the position to the mark
                bb.reset();
                // return the position of the line break
                return p;
            }
        }
        // no line break found!
        return -1;
    }

    public void setParser(SERLogEventFactory parser) {
        this.parser = parser;
    }

}
