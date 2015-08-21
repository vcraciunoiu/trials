package de.schlund.rtstat.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.schlund.rtstat.model.cdr.CDR;
import de.schlund.rtstat.model.cdr.CDRState;

public class CDRFileRecovery {

	private File _oldRecoveryFile, _currentRecoveryFile;

	private FileWriter _fw;

	private long _fileExpireTimestamp;

	private static Logger LOG = Logger.getLogger(CDRFileRecovery.class.getName());

	private CDRState cdrState;

	public CDRFileRecovery(CDRState cdrstate) {
		this.cdrState = cdrstate;
	}

	/**
	 * 
	 * @param recoveryFile
	 * @throws IOException
	 */

	public void recover(String recoveryFile) throws IOException {
		long start = System.currentTimeMillis();
		LOG.info("Starting recovery");

		_oldRecoveryFile = new File(Constants.LOGHOME + "/" + recoveryFile + ".current");
		_currentRecoveryFile = new File(Constants.LOGHOME + "/" + recoveryFile + ".old");

		readRecoveryFile(_oldRecoveryFile);
		readRecoveryFile(_currentRecoveryFile);

		_fw = new FileWriter(_currentRecoveryFile, true);
		long stop = System.currentTimeMillis();
		double time = (stop - start) / 1000.0;
		LOG.info("Recoverytime " + time + "s.");
	}

	public void writeRecoveryFile(CDR cdr) {
		if (_fw == null)
			return;
		try {
			_fw.append(cdr.getCall_id()).append(" ").append(String.valueOf(cdr.getStarttime()));
			_fw.append(" ").append(cdr.getA().getUser()).append(" ").append(cdr.getA().getDomain());
			_fw.append(" ").append(cdr.getB().getUser()).append(" ").append(cdr.getB().getDomain());
			_fw.append("\n").flush();
		} catch (IOException e) {
			Map<String, String> message = new HashMap<String, String>();
			message.put("FATAL ERROR", "FATAL ERROR: Unable to write to recovery " + "file [" + _currentRecoveryFile.getPath() + "]! ");
			LOG.error(e);
		}
		// file rotation!
		if (_fileExpireTimestamp == 0) {
			_fileExpireTimestamp = cdr.getStarttime() + cdrState.getExpireAfter();
		} else if (cdr.getStarttime() > _fileExpireTimestamp) {
			try {
				_fw.close();
			} catch (IOException e) {
				ErrorReporter r = new ErrorReporter();
				r.sendException(e);
			}
			if (_currentRecoveryFile.renameTo(_oldRecoveryFile)) {
				_fileExpireTimestamp = cdr.getStarttime() + cdrState.getExpireAfter();
			} else {
				LOG.error("unable to rotate recovery file. Will not try again!");
				_fileExpireTimestamp = Long.MAX_VALUE;
			}
			try {
				_fw = new FileWriter(_currentRecoveryFile);
			} catch (IOException e) {
				Map<String, String> message = new HashMap<String, String>();
				message.put("FATAL ERROR", "Unable to open recovery " + "file [" + _currentRecoveryFile.getPath() + "]! ");
				LOG.error(e);
			}
		}
	}

	/**Initializes the cdrState and _fileExpireTimestamp from the recovery file
	 * @param recoveryFile
	 * @throws IOException
	 */
	private void readRecoveryFile(File recoveryFile) throws IOException {
		LOG.info("Reading recoveryfile: " + recoveryFile.getAbsolutePath());

		if (recoveryFile.exists()) {
			try {
				final BufferedReader br = new BufferedReader(new FileReader(recoveryFile));
				String line = br.readLine();
				if (line != null) {
					final CDR cdr = createCDRFromRecoveryFileLine(line);
					if (cdr != null) {
						_fileExpireTimestamp = cdr.getStarttime() + cdrState.getExpireAfter();
						cdrState.put(cdr.getCall_id(), cdr);
					}
					line = br.readLine();
				}
				while (line != null) {
					final CDR cdr = createCDRFromRecoveryFileLine(line);
					if (cdr != null) {
						cdrState.put(cdr.getCall_id(), cdr);
					}
					line = br.readLine();
				}
			} catch (FileNotFoundException e) {
				ErrorReporter r = new ErrorReporter();
				r.sendException(e);
			}
		} else {
			LOG.warn("recovery file " + recoveryFile.getName() + " not found.");
		}

	}

	private CDR createCDRFromRecoveryFileLine(final String line) {
		final String[] split = line.split(" ");
		if (split.length == 6) {

			final String call_id = split[0];
			final long timestamp = Long.parseLong(split[1]);
			final String a_user = split[2];
			final String a_provider = split[3];
			final String b_user = split[4];
			final String b_provider = split[5];
			return null;
		} else {
			LOG.error("Recoveryfile contains strange data '" + line + "'");
			return null;
		}

	}

}
