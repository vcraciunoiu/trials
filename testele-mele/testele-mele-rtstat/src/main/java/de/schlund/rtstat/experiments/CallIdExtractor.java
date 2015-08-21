package de.schlund.rtstat.experiments;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts call ids from a logfile for a given date
 * 
 * @author CirstoiuR
 * 
 */
public class CallIdExtractor {

	private String logFileName;
	private Date date;

	public CallIdExtractor(String logFileName, Date date) {

		this.logFileName = logFileName;
		this.date = date;
	}

	public Set<String> getCallIds() {

		Set<String> callIds = new LinkedHashSet<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(logFileName));

			String readLine = "";
			while ((readLine = reader.readLine()) != null) {

				CallIdParser parser = new CallIdParser(readLine);
				if (isTheSameDay(parser.getDate(), date)) {

					callIds.add(parser.getCallID());
				}

			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			throw new IllegalStateException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
		return callIds;
	}

	private boolean isTheSameDay(Date date1, Date date2) {

		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(date1);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(date2);

		if (calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) && calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) && calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH)) {
			/*
			 * System.out.println(date1); System.out.println(date2);
			 * System.out.println("------------------------");
			 */
			return true;

		} else
			return false;
	}

	public class CallIdParser {
		private Date date;
		private String callID;
		private String lineToParse;

		public Date getDate() {
			return date;
		}

		public String getCallID() {
			return callID;
		}

		public CallIdParser(String lineToParse) {
			this.lineToParse = lineToParse;
			parseCallId();
			parseDate();

		}

		private void parseCallId() {

			Pattern callIdPattern = Pattern.compile(" i=(\\w+)\\@");
			Matcher matcher = callIdPattern.matcher(lineToParse);
			if (matcher.find()) {
				callID = matcher.group(1);
			}

		}
		private void parseDate() {
			Pattern datePattern = Pattern.compile("^(\\d{4})-(\\d{2})-(\\d{2})");
			Matcher matcher = datePattern.matcher(lineToParse);
			if (matcher.find()) {
				GregorianCalendar calendar = new GregorianCalendar();
				calendar.set(new Integer(matcher.group(1)), new Integer(matcher.group(2)) - 1, new Integer(matcher.group(3)));
				this.date = calendar.getTime();
			}
		}

	}

	public static void main(String[] args) throws ParseException {
		CallIdExtractor idExtractor = new CallIdExtractor("c://seracc.log", new SimpleDateFormat("MM/dd/yy").parse("08/20/10"));
		Set<String> calllIdSet = idExtractor.getCallIds();
		System.out.println(calllIdSet);
		System.out.println("size=" + calllIdSet.size());
	}
}
