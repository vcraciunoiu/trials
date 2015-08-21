package de.schlund.rtstat.monitoring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.schlund.rtstat.statistics.model.ASRCallData;
import de.schlund.rtstat.statistics.model.ASRCallDataOthers;
import de.schlund.rtstat.statistics.model.ASRTreeNode;
import de.schlund.rtstat.statistics.processor.ASR;
import de.schlund.rtstat.statistics.processor.XRTP;
import de.schlund.rtstat.util.Constants;

public class StatsalyzerServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(StatsalyzerServlet.class);

    private static final long serialVersionUID = -8232391773812773468L;

    static List<XRTP> _xrtp = null;

    static ASR _asr = null;

    public static void setXRTPs(List<XRTP> xrtp) {
        _xrtp = xrtp;
    }

    public static void setASR(ASR asr) {
        _asr = asr;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            handle(request, response);
        } catch (Exception e) {
            LOG.fatal("Exception in StatsalyzerServlet#doGet", e);
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            handle(request, response);
        } catch (Exception e) {
            LOG.fatal("Exception in StatsalyzerServlet#doPost", e);
            throw new ServletException(e);
        }
    }

    private void handle(@SuppressWarnings("unused")
    HttpServletRequest request, HttpServletResponse response) throws IOException {
        final ServletOutputStream os = response.getOutputStream();

        for (Iterator<XRTP> it = _xrtp.iterator(); it.hasNext();) {
            final XRTP curXRTP = it.next();
            final String curProvider = curXRTP.getProvider();
            final Map<String, String> curHashMap = curXRTP.getXRTPToStatsalyzerData();
            os.print("xrtp_plMin_" + curProvider + ".count=" + curHashMap.get("plMin") + "\n");
            os.print("xrtp_plPromille_" + curProvider + ".count=" + curHashMap.get("plPromille") + "\n");
            os.print("xrtp_blAvg_" + curProvider + ".count=" + curHashMap.get("blAvg") + "\n");
            os.print("xrtp_minPerMin_" + curProvider + ".count=" + curHashMap.get("minPerMin") + "\n");
            os.print("xrtp_jitterSamples_" + curProvider + ".count=" + curHashMap.get("jitterSamples") + "\n");
            os.print("xrtp_jitter2_" + curProvider + ".count=" + curHashMap.get("jitter") + "\n");

        }

        insertProviderASR(os, Constants.PROVIDER_QSC);
        insertProviderASR(os, Constants.PROVIDER_TELEFONICA);
        insertProviderASR(os, Constants.PROVIDER_VODAFONE);
        insertProviderASR(os, Constants.PROVIDER_VERIZON);
        insertProviderASR(os, Constants.PROVIDER_COLT);
        
        insertOtherASR(os, Constants.OTHERS);

        os.flush();
    }

    private static SimpleStatsdata collectASR(String provider) {
        SimpleStatsdata d = new SimpleStatsdata(provider);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                final String prefix = "49" + i + j;
                final long ratio = _asr.getASRTree().getTreeNode(provider).collectASRCallData(prefix).getRatio();
                final long goodc = _asr.getASRTree().getTreeNode(provider).collectASRCallData(prefix).getGoodCalls();
                final long allc = _asr.getASRTree().getTreeNode(provider).collectASRCallData(prefix).getAllCalls();
                List<Long> l = new ArrayList<Long>();
                l.add(0, Long.valueOf(ratio));
                l.add(1, Long.valueOf(goodc));
                l.add(2, Long.valueOf(allc));
                d.getValues().put(prefix, l);
            }
        }
        return d;
    }

    private void insertProviderASR(final ServletOutputStream os, String p) throws IOException {
        final ASRTreeNode asr = _asr.getASRTree().getTreeNode(p);
        try {
            final ASRCallData data = asr.collectASRCallData();
            os.println("asr_1xx_" + p + ".count=" + data.getBlockRatio(1));
            os.println("asr_" + p + ".count=" + data.getRatio());
            os.println("asr_3xx_" + p + ".count=" + data.getBlockRatio(3));
            // defined 4xx as the Ration without 408/486/487
            long asr_4xx = data.getBlockRatio(4);
            long asr_4xx_subset = asr_4xx - data.get408Ratio() - data.get486Ratio() - data.get487Ratio();
            os.println("asr_4xx_" + p + ".count=" + asr_4xx);
            os.println("asr_4xx_rest_" + p + ".count=" + asr_4xx_subset);
            os.println("asr_408_" + p + ".count=" + data.get408Ratio());
            os.println("asr_480_" + p + ".count=" + data.get480Ratio());
            os.println("asr_486_" + p + ".count=" + data.get486Ratio());
            os.println("asr_487_" + p + ".count=" + data.get487Ratio());
            os.println("asr_5xx_" + p + ".count=" + data.getBlockRatio(5));
            os.println("asr_6xx_" + p + ".count=" + data.getBlockRatio(6));
            os.println("asr_7xx_" + p + ".count=" + data.getBlockRatio(7));
            os.println("asr_calls_" + p + ".count=" + data.getAllCalls());

            for (short sipcode : ASRCallData.SIP_CODES) {
                os.println("asr_" + sipcode + "_" + p + "_counter.count=" + data.getCount(sipcode));
            }

            os.println("asr_calls_once_routed_" + p + ".count=" + data.getCallsOnceRouted());
            os.println("asr_calls_without_ack_" + p + ".count=" + data.getCallsWithoutAck());
            os.println("asr_without_bye_" + p + ".count=" + data.getCallsWithoutBye());
            os.println("asr_new_successful_" + p + ".count=" + data.getCallsNewSuccesfull());
            os.println("asr_ack_discarded_" + p + ".count=" + data.getCallsAckDiscarded());
            os.println("asr_bye_discarded_" + p + ".count=" + data.getCallsByeDiscarded());

        } catch (IllegalStateException ise) {
            LOG.error("exception in collectASRCallData", ise);
        }
    }

    private void insertOtherASR(final ServletOutputStream os, String p) throws IOException {
        final ASRTreeNode asr = _asr.getASRTree().getTreeNode(p);
        try {
            final ASRCallDataOthers data = asr.collectASRCallDataOthers();
            os.println("asr_abroad.count=" + data.getAbroadCalls());
        } catch (IllegalStateException ise) {
            LOG.error("exception in collectASRCallData", ise);
        }
    }

    static class SimpleStatsdata {
        private Map<String, List<Long>> values;
        private String provider;

        SimpleStatsdata(String provider) {
            this.provider = provider;
            values = new HashMap<String, List<Long>>();
        }

        public double[] getRatioAsDoubleArr() {
            double[] d = new double[values.keySet().size()];
            int i = 0;
            for (String s : values.keySet()) {
                List<Long> l = values.get(s);
                Long ratio = l.get(0);
                d[i++] = ratio.doubleValue();
            }
            return d;
        }

        public String getProvider() {
            return provider;
        }

        public Map<String, List<Long>> getValues() {
            return values;
        }
    }
}
