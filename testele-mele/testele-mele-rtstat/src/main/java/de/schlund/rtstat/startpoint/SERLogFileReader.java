package de.schlund.rtstat.startpoint;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;

import de.schlund.rtstat.model.SERLogEvent;
import de.schlund.rtstat.model.SERLogEventFactory;
import de.schlund.rtstat.model.SERLogEventFactoryV2;

/**
 * Reads from a file. Used for unit-tests.
 */
public class SERLogFileReader extends SERLogProcessorFeeder {

	final static Logger LOG = Logger.getLogger(SERLogFileReader.class);

	private final static Charset CHARSET = Charset.forName("ISO-8859-1");

	private FileChannel _fileChannel;
	private String fname;
	private SERLogEventFactory parser;
	
	/**
	 * Creates a new EventReceiver with given parameters.
	 * 
	 * @param address
	 *            Address of server socket
	 * @param backlog
	 *            Backlog of server socket
	 * @throws IOException
	 *             if a network error occurs
	 */
	public SERLogFileReader(String name, String filename) throws IOException {
		super(name);
		fname = filename;
		FileInputStream fis = new FileInputStream(filename);
		_fileChannel = fis.getChannel();
	}

	public SERLogFileReader(String name) throws IOException {
		super(name);
		parser = new SERLogEventFactoryV2();
	}

	public SERLogFileReader() {
		super("bla");
		System.out.println("neu");
	}

	public void setFile(String filename) throws IOException {
		FileInputStream fis = new FileInputStream(filename);
		_fileChannel = fis.getChannel();
		fname = filename;
		System.out.println("Setting file to " + filename + "  "+ this.hashCode());
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
//			System.out.println(CHARSET.decode(slice));
			SERLogEvent event = parser.parse(CHARSET.decode(slice));
			// start event parsing and processing
			process(event);
			// return true because we found a line
			return true;
		}
		// return false because we found no line
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.schlund.rtmed.EventReceiver#run()
	 */
	public void run() {
		System.out.println("Startet ");

		if (_fileChannel == null) {
			throw new IllegalStateException("FileChannel == null");
		}
		ByteBuffer bb;

		bb = ByteBuffer.allocateDirect(1 << 16);
		bb.clear();

		try {
			Thread t = Thread.currentThread();
			while (t == _thread) {
				// read something from the socket into the buffer
				if (_fileChannel.read(bb) == -1)
					break;
				// prepare buffer for reading.
				bb.flip();
				// advance buffer as long as there are any more lines
				while (nextLine(bb)) {
					// do nothing
				}
				// compact buffer
				bb.compact();
				// start over
				t = Thread.currentThread();
			}
		} catch (IOException e) {
			LOG.error(e);
		}
	}

}
