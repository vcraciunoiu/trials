package de.schlund.rtstat.test.model;

import java.text.ParseException;

import junit.framework.TestCase;
import de.schlund.rtstat.model.XRTPParser;
import de.schlund.rtstat.model.XRTPValue;

public class XRTPParserTest extends TestCase {

	public void testParse01() throws ParseException {
		//String x = "PS=484;OS=116160;SP=0/0;SO=0;PR=376;OR=59224;CR=0;SR=0;PL=0;BL=0;EN=PCMA;DE=PCMA,PCMU,G726-40;JI=0;;;";
		//String x = "CS=0;PS=663;ES=663;OS=159120;SP=0/0;SO=0;QS=-;PR=657;ER=663;OR=157680;CR=0;SR=0;QR=-;PL=0,0;BL=0;LS=0;RB=0/0;SB=0/0;EN=PCMA;DE=PCMA;JI=33,39;DL=86,84,90,IP=77.177.216.57:7078,77.176.226.181:7082";
		
		String x = "CS=1788;PS=566;ES=568;OS=135840;SP=0/0;SO=0;QS=-;PR=568;ER=568;OR=135840;CR=0;SR=0;QR=-;PL=0,0;BL=0;LS=0;RB=0/0;SB=0/0;EN=PCMA;DE=PCMA;JI=23,48;DL=28,27,31;IP=77.185.30.173:7078,87.234.1.202:15612";
		
		XRTPParser p = new XRTPParser();
		try {
			XRTPValue v = p.parse(x);
			// fail();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// XRTPValue v = p.parse(
		// "PS=689;OS=165360;SP=0/0;SO=0;PR=691;OR=165840;CR=0;SR=0;PL=0;BL=0;JI=36"
		// );
		// System.out.println(v);
	}

//	public void testParse02() throws ParseException {
//		XRTPParser p = new XRTPParser();
//		p.parse("PS=1896;OS=410280;SP=0/0;SO=0;PR=2711;OR=393064;CR=0;SR=0;PL=0;BL=0;EN=PCMA,G726-32;DE=PCMA,PCMU,G726-32,G726-40;JI=0");
//	}

	/*
	 * public void testParse03() throws ParseException { XRTPParser p = new
	 * XRTPParser();p.parse(
	 * "PS=7345;OS=1762800;SP=0/0;SO=0;PR=7348;OR=1763520;CR=0;SR=0;PL=0;BL=0;BT=0;BD=0;EN=PCMA;DE=PCMA;JI=23"
	 * ); }
	 */
}
