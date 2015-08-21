package de.schlund.rtstat.test.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.ParseException;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.schlund.rtstat.model.SERLogEvent;
import de.schlund.rtstat.model.SERLogEventFactory;
import de.schlund.rtstat.model.SERLogEventFactoryV3;
import de.schlund.rtstat.model.XRTPValue;
import de.schlund.rtstat.processor.ser.ProviderUtil;
import de.schlund.rtstat.processor.ser.SimplePropertiesProcessor;
import de.schlund.rtstat.test.RtstatTestSuite;
import de.schlund.rtstat.util.Constants;

/**
 * 
 * <p>
 * checking against ugly real-live logfile entries
 * </p>
 */
public class SERLogEventTest extends TestCase {
	ApplicationContext ctx;
	SimplePropertiesProcessor pp;
	SERLogEventFactoryV3 parser;
	
	static {
		RtstatTestSuite.configureLogging();
	}

	public static final String MILOG = "2009-11-25T17:46:35+01:00 proxy1-bs2 [29679]: md=BYE, fg=1522179711, " +
			"tg=92579BAF361B5432, i=3bdc9bd9-115f099f-55d160d5-cfaf@1und1-3.sip.mgc.voip.telefonica.de, " +
			"sc=200, iu=sip:49335527935@77.185.189.67;uniq=6085BDA7A39267718C9F94A882D31, " +
			"ou=sip:49335527935@77.185.189.67;uniq=6085BDA7A39267718C9F94A882D31, " +
			"f=sip:+493355217600@1und1-3.sip.mgc.voip.telefonica.de:5060;user=phone, " +
			"t=sip:+49335527935@217.188.58.181:5060;user=phone, cq=2, id=, mi=, fp=, fs=14, ld=, " +
			"u1=, u2=AVM FRITZ!Box Fon WLAN 7141 (UI) 40.04.50 TAL (Jan 17 2008), " +
			"x1=, " +
			"x2=PS=22301;OS=5352240;SP=0/0;SO=0;PR=33438;OR=5350080;CR=0;SR=0;PL=0;BL=0;BT=0;BD=0;EN=PCMA;DE=PCMA;JI=200, " +
			"tt=, ct=";
	
	public static final String EVENT_STEUERBUERO = "May 29 08:34:42 sip1 /usr/sbin/openser[1668]: "
			+ "ACC: transaction answered: method=INVITE, i-uri=sip:00493876612544@sip1.schlund.de, "
			+ "o-uri=sip:493876612544@1und1-2.interconnect.sip.voip.telefonica.de, "
			+ "call_id=1021733396@84.188.96.108, from=\"Steuerb�ro Dr.L�ders, Tarcikowski & Partner\" "
			+ "<sip:493876788428@sip.gmx.net>;tag=1448129940, code=486, cseq=21, "
			+ "to=<sip:00493876612544@sip.gmx.net>;tag=947031005, uid=493876788428, xrtp=n/a, "
			+ "ua=DeTeWe Opencom 31/0.1, target=pstn";

	public static final String EVENT_BISTER = "Sep 20 21:50:11 sip2 /usr/sbin/openser[30396]: "
			+ "ACC: transaction answered: method=BYE, i-uri=sip:4974238499403@84.158.130.170;uniq=3C73DC23499DA633DCC86BA7DD8A1, "
			+ "o-uri=sip:4974238499403@84.158.130.170;uniq=3C73DC23499DA633DCC86BA7DD8A1, "
			+ "call_id=808D6691-4E47-DB11-BED8-006073E5F254@84.158.130.170, "
			+ "from=Bister, Gerd <sip:4974238499405@1und1.de>;tag=18806, code=481, cseq=4, "
			+ "to=<sip:074238499403@1und1.de>;tag=872894DBCDABEC40, uid=n/a, ringing=n/a, xrtp=n/a, "
			+ "ua=SIPPER for phoner, target=n/a";

	public final String EVENT_XRTPTEST1 = "Mar 15 15:37:59 sipproxy4 /usr/sbin/openser[9853]: ACC: transaction answered: method=BYE, "
			+ "i-uri=sip:499433899350@subscriber5.interconnect.mgc.voip.telefonica.de:5060, "
			+ "o-uri=sip:499433899350@subscriber5.interconnect.mgc.voip.telefonica.de:5060, "
			+ "call_id=07E6505838383F0C@217.237.36.121, from=<sip:499433372@1und1.de>;tag=8344D44298E7A774, "
			+ "code=200, cseq=46, to=<sip:499433899350@1und1.de>;tag=1429156754, uid=n/a, ringing=n/a, "
			+ "xrtp=PS=484;OS=116160;SP=0/0;SO=0;PR=376;OR=59224;CR=0;SR=0;PL=0;BL=0;EN=PCMA;DE=PCMA,PCMU,G726-40;JI=0, "
			+ "ua=AVM FRITZ!Box Fon WLAN 7170 (UI) 29.04.29 (Dec  8 2006), target=n/a";

	public final String EVENT_XRTPTEST2 = "Jul 25 12:16:43 sipproxy1 /usr/sbin/openser[10946]: ACC: transaction answered: method=BYE, "
			+ "i-uri=sip:498997898691@subscriber3.interconnect.mgc.voip.telefonica.de:5060, "
			+ "o-uri=sip:498997898691@subscriber3.interconnect.mgc.voip.telefonica.de:5060, "
			+ "call_id=149A18613D80CDD1@84.153.31.111, from=<sip:498178541314@sip.1und1.de>;tag=3D4ED6BD13C1C88C, "
			+ "code=200, cseq=2696, to=<sip:498997898691@sip.1und1.de>;tag=1920477583, uid=n/a, last-dst=n/a, ringing=n/a, "
			+ "xrtp=PS=1896;OS=410280;SP=0/0;SO=0;PR=2711;OR=393064;CR=0;SR=0;PL=0;BL=0;EN=PCMA,G726-32;DE=PCMA,PCMU,G726-32,G726-40;JI=0, "
			+ "ua=AVM FRITZ!Box Fon WLAN 7141 (UI) 40.04.30 (Jan 17 2007), target=n/a";

	public final String EVENT_XRTPTEST3 = "Aug 15 18:01:51 sipproxy1 /usr/sbin/openser[21290]: ACC: transaction answered: method=BYE, "
			+ "i-uri=sip:00492064732545@62.206.98.12:5060, "
			+ "o-uri=sip:00492064732545@62.206.98.12:5060, "
			+ "call_id=218F68B900B31CCE@77.183.190.246, from=<sip:492064149841@1und1.de>;tag=62824D041D9937B7, "
			+ "code=200, cseq=11, to=<sip:492064732545@1und1.de>;tag=1814979053, uid=n/a, last-dst=n/a, ringing=n/a, "
			+ "xrtp=PS=299;OS=71760;SP=0/0;SO=0;PR=0;OR=0;CR=0;SR=0;PL=0;BL=0;EN=PCMA;DE=;JI=0, "
			+ "ua=AVM FRITZ!Box Fon WLAN 7141 40.04.37 (Jun 28 2007), target=n/a";

	public final String EVENT_TALRSP_404 = "Jul 20 13:45:30 voiptest1 /usr/sbin/openser[21110]: ACC: transaction answered: method=INVITE, "
			+ "i-uri=sip:07215@voiptest1.schlund.de, o-uri=sip:tal-rsp-484@tal.voiptest.schlund.de:5060, "
			+ "call_id=7B96E88DB8A67A20FCA8C768FDF3@87.177.200.228, from=<sip:495113584120@voiptest.schlund.de>;tag=AFD209691D4AC4D135BAD7F296AE,"
			+ " code=200, cseq=509, to=<sip:07215@voiptest.schlund.de>;tag=as4bd01603, uid=495113584120, "
			+ "last-dst=1und1-1.interconnect.sip.voip.telefonica.de, ringing=n/a, xrtp=n/a, "
			+ "ua=AVM FRITZ!Box Fon 5050 (UI) 12.03.89 (3.01.03 tested by accredited T-Com test lab) (Oct 28 2005), target=pstn";

	public final String EVENT_001 = "Jul 20 09:40:54 sipproxy1 /usr/sbin/openser[1977]: ACC: transaction answered: method=INVITE,"
			+ "i-uri=sip:0@sipproxy1.schlund.de, o-uri=sip:tal-rsp-404@tal.sip.schlund.de:5060, "
			+ "call_id=AC52BA721C1A2A109F01000000000000@62.226.27.91, from=<sip:@sip.1und1.de;user=phone>;tag=ejgpi49boo16434,"
			+ " code=200, cseq=1826, to=<sip:0@sip.1und1.de;user=phone>;tag=as5b05b7c3, uid=n/a, "
			+ "ringing=n/a, xrtp=n/a, ua=FEC SIP Proxy 4.0, target=n/a";

	public final String EVENT_002 = "Jul 23 18:09:39 sipproxy2 /usr/sbin/openser[16340]: ACC: transaction answered: method=REGISTER, "
			+ "i-uri=sip:sip2.1und1.de:5060, o-uri=sip:sip2.1und1.de:5060, "
			+ "call_id=4F97-F3D1-31101125-6BDE57F7669C-0003@62.134.226.192, from=<sip:929462@sip2.1und1.de:5060>;tag=617263616479616E-1594942451-f09bf41f-632470271, "
			+ "code=407, cseq=1232, to=<sip:929462@sip2.1und1.de:5060>;tag=329cfeaa6ded039da25ff8cbb8668bd2.093b, uid=496406929462, last-dst=n/a, "
			+ "ringing=n/a, xrtp=n/a, ua=Speedport/W700V-1.22.000, target=n/a";

	public final String EVENT_003 = "Jul 24 17:20:15 sipproxy3 /usr/sbin/openser[6260]: ACC: transaction answered: method=INVITE, "
			+ "i-uri=sip:49773254008@sipproxy3.schlund.de, o-uri=sip:tal-rsp-404@tal.sip.schlund.de:5060, "
			+ "call_id=D16EBAA840445F62@84.132.157.127, from=\"anonymous\" <sip:495523454570@sip.1und1.de>;tag=F8AC2300BF526345, "
			+ "code=200, cseq=27816, to=<sip:49773254008@sip.1und1.de>;tag=as1a1fd9f5, uid=495523454570, last-dst=1und1-1.interconnect.sip.voip.telefonica.de, "
			+ "ringing=n/a, xrtp=n/a, ua=AVM FRITZ!Box Fon WLAN 7141 (UI) 40.04.30 (Jan 17 2007), target=pstn";

	public final String EVENT_SYSLOG_01 = "<134>Sep 11 06:27:59 sipproxy1 /usr/sbin/openser[25910]: ACC: transaction answered: method=INVITE, "
			+ "i-uri=sip:491379404040@sipproxy1.schlund.de, o-uri=sip:491379404040@1und1-2.interconnect.sip.voip.telefonica.de, "
			+ "call_id=7E63292EE55C28E6@91.7.39.166, from=<sip:496220911323@1und1.de>;tag=9B65E5B7F6111BC7, "
			+ "code=200, cseq=1167, to=<sip:491379404040@1und1.de>;tag=387546421, uid=496220911323, last-dst=1und1-2.interconnect.sip.voip.telefonica.de, "
			+ "ringing=1, xrtp=n/a, ua=AVM FRITZ!Box Fon WLAN 7141 (UI) 40.04.33 (May 29 2007), target=pstn";

    public void testParseMilog() {
        System.out.println("testParseMilog");
        SERLogEvent event = parser.parse(MILOG);
        assertTrue(event.toString(), event.isValid());
        assertTrue(event.getCode() == 486);

        pp.processEvent(event);
        assertEquals(Constants.PROVIDER_TELEFONICA, event
                .getProperty(ProviderUtil.B_PROVIDER));
    }

    public void testParseSteuerbuero() {
		System.out.println("testParseSteuerbuero");
		SERLogEvent event = parser.parse(EVENT_STEUERBUERO);
		assertTrue(event.toString(), event.isValid());
		assertTrue(event.getCode() == 486);

		pp.processEvent(event);
		assertEquals(Constants.PROVIDER_TELEFONICA, event
				.getProperty(ProviderUtil.B_PROVIDER));
	}

	/*
	 * public void testParse001() { SERLogEvent event = new
	 * SERLogEvent(EVENT_001); event.parse(); assertTrue(event.isValid()); }
	 */

	public void testParseBister() {
		System.out.println("testParseBister");
		SERLogEvent event = parser.parse(EVENT_BISTER);
		assertTrue(event.toString(), event.isValid());
		assertTrue(event.getCode() == 481);
	}

	public void testParseTALRSP484() {
		System.out.println("testParseTALRSP484");
		SERLogEvent event = parser.parse(EVENT_TALRSP_404);
		assertTrue(event.toString(), event.isValid());
		pp.processEvent(event);
		assertEquals(Constants.PROVIDER_TELEFONICA, event.getProperty(ProviderUtil.B_PROVIDER));
		assertTrue(event.getCode() == 484);
	}

	public void testParseEVENT_SYSLOG_01() {
		System.out.println("testParseEVENT_SYSLOG_01");
		SERLogEvent event = parser.parse(EVENT_SYSLOG_01);
		assertTrue(EVENT_001.toString(), event.isValid());
		pp.processEvent(event);
		assertEquals(Constants.PROVIDER_TELEFONICA, event.getProperty(ProviderUtil.B_PROVIDER));
		assertTrue(event.getCode() == 200);
	}

	public void testParseEvent002() {
		System.out.println("testParseEvent002");
		SERLogEvent event = parser.parse(EVENT_002);
		assertTrue(event.toString(), event.isValid());

	}

	public void testParseEvent003() {
		System.out.println("testParseEvent003");
		SERLogEvent event = parser.parse(EVENT_003);
//		event.parse();
		assertTrue(event.toString(), event.isValid());

		pp.processEvent(event);
		assertEquals(Constants.PROVIDER_TELEFONICA, event.getProperty(ProviderUtil.B_PROVIDER));
		assertEquals(event.getProperty(ProviderUtil.B_USER), "49773254008");
		assertEquals(event.getProperty(ProviderUtil.A_USER), "495523454570");
		assertTrue(event.getCode() == 404);
	}

	public void testXRTPTest1() throws ParseException {
		System.out.println("testXRTPTest1");
		SERLogEvent event = parser.parse(EVENT_XRTPTEST1);
		assertTrue(event.toString(), event.isValid());
//		XRTPValue xr = event.getXrtp();
//		assertTrue(event.getCall_id(), xr != null);
	}

	public void testXRTPTest2() throws ParseException {
		System.out.println("testXRTPTest2");
		SERLogEvent event = parser.parse(EVENT_XRTPTEST2);
//		event.parse();
		assertTrue(event.toString(), event.isValid());
//		XRTPValue xr = event.getXrtp();
//		assertTrue(event.getCall_id().toString(), xr != null);
	}

	public void testXRTPTest3() throws ParseException {
		System.out.println("testXRTPTest3");
		SERLogEvent event = parser.parse(EVENT_XRTPTEST3);
//		event.parse();
		assertTrue(event.toString(), event.isValid());
//		XRTPValue xr = event.getXrtp();
//		assertTrue(event.getCall_id().toString(), xr != null);
	}

	public void testXRTPTestSteuer() throws ParseException {
		System.out.println("testXRTPTestSteuer");
		SERLogEvent event = parser.parse(EVENT_STEUERBUERO);
//		event.parse();
		assertTrue(event.toString(), event.isValid());
//		XRTPValue xr = event.getXrtp();
//		assertTrue(event.getCall_id().toString(), xr == null);
	}

	public void testTestfile01() throws Exception {
		System.out.println("testTestfile01");
		FileReader i = new FileReader("testdata/testdata01.txt");
		BufferedReader r = new BufferedReader(i);
		String str = r.readLine();
		while (str != null) {
			System.out.println("String==============" + str);
			SERLogEvent ev = parser.parse(str);
//			ev.parse();
			assertTrue(ev.toString(), ev.isValid());

			str = r.readLine();
		}
	}

	public void testTestfile07() throws Exception {
		System.out.println("testTestfile07");
		FileReader i = new FileReader("testdata/testdata07.txt");
		BufferedReader r = new BufferedReader(i);
		String str = r.readLine();
		while (str != null) {
			SERLogEvent ev = parser.parse(str);
			assertTrue(ev.toString(), ev.isValid());
			System.out.println("String==============" + str);
			str = r.readLine();
		}
	}

	public void testTestfile05() throws Exception {
		System.out.println("testTestfile05");
		FileReader i = new FileReader("testdata/testdata05.txt");
		BufferedReader r = new BufferedReader(i);
		String str = r.readLine();
		while (str != null) {
			System.out.println("String==============" + str);
			SERLogEvent ev = parser.parse(str);
			assertTrue(ev.toString(), ev.isValid());
			str = r.readLine();
		}
	}

	public void tesTestfile09() throws Exception {
		System.out.println("testTestfile09");
		FileReader i = new FileReader("testdata/testdata09.txt");
		BufferedReader r = new BufferedReader(i);
		String str = r.readLine();
		while (str != null) {
			SERLogEvent ev = parser.parse(str);
//			ev.parse();
			assertTrue(ev.toString(), ev.isValid());
			str = r.readLine();
		}
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ctx = new ClassPathXmlApplicationContext("bean.xml");
		pp = (SimplePropertiesProcessor) ctx.getBean("simplepp");
		parser = new SERLogEventFactoryV3();
	}
}
