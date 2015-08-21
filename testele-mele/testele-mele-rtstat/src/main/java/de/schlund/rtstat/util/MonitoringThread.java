package de.schlund.rtstat.util;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import de.schlund.rtstat.statistics.model.ASRCallData;
import de.schlund.rtstat.statistics.model.ASRTreeNode;
import de.schlund.rtstat.statistics.model.XRTPCallData;
import de.schlund.rtstat.statistics.processor.ASR;
import de.schlund.rtstat.statistics.processor.XRTP;

public class MonitoringThread implements Runnable {
    
    public static final Logger LOG = Logger.getLogger(MonitoringThread.class);
    private RandomAccessFile fw;
    
    public static class MinMax {

        private long min, max;

        private boolean set = false;

        public void update(long value) {
            if (set) {
                if (min > value) {
                    min = value;
                }
                if (max < value) {
                    max = value;
                }
            } else {
                // not yet set .. initialize with the first seen value
                min = value;
                max = value;
                set = true;
            }
        }

        public void reset() {
            set = false;
            min = 0;
            max = 0;
        }

        public long getMin() {
            if (!set) {
                throw new IllegalStateException("no minimum was found, never called update(long)?");
            }
            return min;
        }

        public long getMax() {
            if (!set) {
                throw new IllegalStateException("no maximum was found, never called update(long)?");
            }
            return max;
        }

        /**
         * @return $min/$max
         */
        @Override
        public String toString() {
            return getMin() + "/" + getMax();
        }
    }

    /**
     * insert ',' every 3 digits for easier reading
     */
    private static final DecimalFormat DF;

    static {
        DF = new DecimalFormat();
        DF.setGroupingSize(3);
        DF.setGroupingUsed(true);
    }

    private ASR _asr = null;

    private List<XRTP> _xrtp = new ArrayList<XRTP>();

    private MinMax free, total, threads;

    public MonitoringThread(ASR asr, List<XRTP> xrtp, String aliveFile) throws IOException {
        _asr = asr;
        _xrtp = xrtp;
        free = new MinMax();
        total = new MinMax();
        threads = new MinMax();
        fw = new RandomAccessFile(aliveFile, "rw");
    }

    public void run() {

        while (true) {
            final Runtime r = Runtime.getRuntime();

            final long lFree = r.freeMemory();
            final long lTotal = r.totalMemory();
            final long lMax = r.maxMemory();
            final int iThreads = Thread.activeCount();

            free.update(lFree);
            total.update(lTotal);
            threads.update(iThreads);

            LOG.info("maxMemory:\t" + DF.format(lMax));
            LOG.info("freeMemory:\t" + DF.format(lFree) + "\t" + free.toString());
            LOG.info("totalMemory:\t" + DF.format(lTotal) + "\t" + total.toString());
            LOG.info("runningThreads:\t" + iThreads + "\t" + threads.toString());
            
            // write in alive file
            StringBuilder buf = new StringBuilder();
            buf.append("freeMemory:\t" + DF.format(lFree)).append("\n");
            buf.append("totalMemory:\t" + DF.format(lTotal)).append("\n");
            buf.append("runningThreads:\t" + iThreads + "\n");
            try {
                fw.seek(0);
                fw.writeBytes(buf.toString());
            } catch (Exception e) {
                LOG.error("Error writing in alive file", e);
            }
            
            if (_asr != null) {
                for (Entry<String, ASRTreeNode> entry : _asr.getASRTree().entrySet()) {
                    // to force update
                    ASRCallData asr = entry.getValue().getASRCallData();
                    LOG.info("ASR:" + entry.getKey() + "\t" + entry.getValue().getBuffersize());
                    if (asr != null) {
                        LOG.info("TS oldest: " + new Date(asr.getTimestampOldest()));
                    }
                }
            }
            if (_xrtp != null) {
                for (XRTP xrtp : _xrtp) {
                    // to force update
                    try {
                        xrtp.getXRTP();
                    } catch (Exception e) {
                        LOG.error("Couldn't force update.", e);
                    }
                    LOG.info("XRTP:" + xrtp.getProvider() + "\t" + xrtp.getBuffersize());
                    XRTPCallData data = xrtp.getBuffer().peek();
                    if (data != null) {
                        LOG.info("TS oldest: " + new Date(data.getTimestamp()));
                    }
                }
            }
            try {
                Thread.sleep(60 * 1000);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

}
