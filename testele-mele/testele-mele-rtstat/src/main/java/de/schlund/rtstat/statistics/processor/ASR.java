package de.schlund.rtstat.statistics.processor;

import java.util.ArrayList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.schlund.rtstat.model.cdr.CDR;
import de.schlund.rtstat.processor.Processor;
import de.schlund.rtstat.processor.ser.ProviderUtil;
import de.schlund.rtstat.statistics.model.ASRTree;
import de.schlund.rtstat.statistics.model.ASRTreeNode;
import de.schlund.rtstat.util.Constants;
import de.schlund.rtstat.util.Util;

/**
 * CDR Processor for collecting ASR (Answer Sustain Ratio) statistics.
 * 
 * @author mic
 */
public class ASR extends Processor<CDR> {

  final static Logger LOG = Logger.getLogger(ASR.class);

  private ASRTree _asrTree;

  private int _minCallDuration;

  private String con1251Filter;

  private ProviderUtil providerutil;

  /**
   * Create an ASR statistics process.
   * 
   * @param cruncher
   *          the framework controller object
   * @param qCapacity
   *          the capacity of the incoming events queue
   * @param treenodeCapacity
   *          the capacity of the ASR ring buffer (this is the max. number of
   *          CDRs for which the ASR will be calculated)
   * @param minDuration
   *          the minimal duration of a call to qualify it as successfull
   * @param port
   *          the local tcp port where reports can be requested.
   */

  public ASR(String name, int qCapacity, int treenodeCapacity, int minDuration, int port) {
    super(name, qCapacity);
    init(treenodeCapacity, minDuration, port);
  }

  /**
   * Create an ASR statistics process with a default event queue size.
   * 
   * @param cruncher
   *          the framework controller object
   * @param treenodeDuration
   *          bufferduration in ms for elements in the treenode buffer
   * @param minDuration
   *          the minimal duration of a call to qualify it as successfull
   * @param port
   *          the local tcp port where reports can be requested.
   */
  public ASR(String name, Integer treenodeDuration, Integer minDuration, Integer port) {
    super(name);
    init(treenodeDuration, minDuration, port);
  }

  /**
   * common init stuff for all constructors
   * 
   * @param bufferduration
   * @param minDuration
   * @param port
   */
  private void init(int bufferduration, int minDuration, int port) {
    _asrTree = new ASRTree(bufferduration);
    _minCallDuration = minDuration;
    /*
     * t = new DebugSocketPrinter(this, port); t.start();
     */
  }

  /**
   * Returns a string representation of the currently stored statistics.
   */
  public ASRTree getASRTree() {
    return _asrTree;
  }

  public void setProviderUtil(ProviderUtil p) {
    this.providerutil = p;
  }

  public String getCon1251Filter() {
    return con1251Filter;
  }

  @Override
  protected void processEvent(CDR cdr) {
    ArrayList<String[]> lastDest = null;
    try {
      lastDest = cdr.getLastDst();
      if (lastDest.size() == 1) {
        String b_provider = cdr.getB().getDomain();
        ASRTreeNode treeNode = _asrTree.getTreeNode(b_provider);
        insertCode(cdr, treeNode, (short) 0, b_provider, 1, false);
      } else {
        for (int i = 0; i < lastDest.size(); i++) {
          String[] dstEntry = lastDest.get(i);
          if (dstEntry.length == 2) {
            String bprovTemp = dstEntry[0];
            String b_provider = providerutil.getProviderMap().get(bprovTemp);
            ASRTreeNode treeNode = _asrTree.getTreeNode(b_provider);
            short code = Short.parseShort(dstEntry[1]);

            boolean isLastDst = false;
            if (i == lastDest.size() - 1) {
              isLastDst = true;
            }
            insertCode(cdr, treeNode, code, b_provider, 2, isLastDst);
          }
        }
      }
    } catch (Exception e) {
      LOG.error("Error: ", e);
      // reporter.sendException(e);
    }
  }

  public void setCon1251Filter(String con1251Filter) {
    this.con1251Filter = con1251Filter;
  }

  private boolean printed = false;

  private void insertCode(CDR cdr, ASRTreeNode treeNode, short codeParam, String b_provider, int lastDstCase, boolean isLastDst) {
    final long timestamp;
    final long now;
    final long tooold;

    if (LOG.isDebugEnabled())
      LOG.debug("Inserting callid=" + cdr.getCall_id() + ", code=" + codeParam + ", bprov=" + b_provider);

    // System.out.println("Inserting callid=" + cdr.getCall_id()+
    // ", code=" + code +
    // ", bprov=" + b_provider +
    // ", insertFailRouted=" + insertFailRouted
    // );

    if (treeNode != null) {
      final String prefix = cdr.getB().getUser();
      if (prefix == null) {
        LOG.error("Prefix is null for CDR:" + cdr);
      }

      // we do this IF because "discarded" CDRs don't have INVITEs,
      // so we cannot get the code and other properties.
      if (!(cdr.getState().equals(CDR.STATE.ACK_DISCARDED) || cdr.getState().equals(CDR.STATE.BYE_DISCARDED))) {

        boolean insertFailRouted = false;
        short code = 0;
        if (lastDstCase == 1) {
          code = cdr.getCode();
          insertFailRouted = false;
        } else if (lastDstCase == 2) {
          insertFailRouted = true;
          int scCode = cdr.getCode();
          if (!(scCode == 200) && (isLastDst)) {
            insertFailRouted = false;
          }
          code = codeParam;
        }

        // CDRs older than 120 minutes will not take part in statistic
        // calculation
        timestamp = cdr.getEndtime();
        now = System.currentTimeMillis();
        tooold = now - (180 * 60 * 1000);

        if (timestamp < tooold) {
          if (LOG.isDebugEnabled())
            LOG.debug("ancient (" + ((now - timestamp) / 1000) + "s) event found: " + cdr.getCall_id() + ", code: " + cdr.getCode());
        } else {

          if (code == 200 && cdr.getDuration() < _minCallDuration) {
            // self-defined status code for too short calls
            code = 700;
          }

          int digit = Character.digit(prefix.charAt(0), 10);
          if (digit < 0 || digit > 9) {
            LOG.warn("wrong character found in prefix: '" + prefix + "' " + cdr.toString());
          } else {
            if (LOG.isDebugEnabled())
              LOG.debug("Insert into tree[" + b_provider + "] for call[" + cdr.getCall_id() + "] " + "prefix[" + prefix + "] code[" + code + "] timestamp[" + Util.readableTimestamp(timestamp) + "]");
            treeNode.insert(prefix, code, timestamp);
          }

          // calls having been at least once failure routed
          if (insertFailRouted) {
            treeNode.insertOtherData(Constants.CALLS_ONCE_ROUTED, 0, 0);
          }

          // calls without ack
          if (cdr.getACK() == null && cdr.getBYE() != null) {
            treeNode.insertOtherData(Constants.CALLS_WITHOUT_ACK, 0, 0);
          }

          // calls without bye
          if (cdr.getBYE() == null) {
            treeNode.insertOtherData(Constants.CALLS_WITHOUT_BYE, 0, 0);
          }
        }

      } else { // if is a discarded CDR

        // ACK log lines which are discarded
        if (cdr.getState().equals(CDR.STATE.ACK_DISCARDED)) {
          treeNode.insertOtherData(Constants.CALLS_ACK_DISCARDED, 0, 0);
        }

        // BYE log lines which are discarded
        if (cdr.getState().equals(CDR.STATE.BYE_DISCARDED)) {
          treeNode.insertOtherData(Constants.CALLS_BYE_DISCARDED, 0, 0);
        }

      }

    } else { // if treeNode == null

      if (!(cdr.getState().equals(CDR.STATE.ACK_DISCARDED) || cdr.getState().equals(CDR.STATE.BYE_DISCARDED))) {

        int code = 0;
        if (lastDstCase == 1) {
          code = cdr.getCode();
        } else if (lastDstCase == 2) {
          code = codeParam;
        }

        if (LOG.isEnabledFor(Level.INFO)) {
          LOG.info("no provider '" + b_provider + "' found. [" + cdr + "]");
        }
        ASRTreeNode treeNodeOthers = _asrTree.getTreeNode(Constants.OTHERS);

        String ouri = cdr.getFirstINVITE().getOURI().getDomain();
        if (ouri.equals("ab.sip.schlund.de")) {
          treeNodeOthers.insertOtherData(Constants.CALLS_VOICEBOX, 1, code);
        } else if (ouri.equals("tal.sip.schlund.de")) {
          treeNodeOthers.insertOtherData(Constants.CALLS_ANNOUNCEMENTS, 1, code);
        }

        if (cdr.getToDomain() != null && (cdr.getToDomain().contains("1und1.de") || cdr.getToDomain().contains("freenet.de")) && cdr.getFirstINVITE().getFromProvider().equals("")) {
          treeNodeOthers.insertOtherData(Constants.CALLS_ONNET, 1, code);
        }

        try {
          String toNumberPrefix = cdr.getToNumber().substring(0, 4);
          if (!cdr.getToNumber().substring(0, 2).equals("49")) {
            treeNodeOthers.insertOtherData(Constants.CALLS_ABROAD, 1, code);
          } else if (toNumberPrefix.equals("4915") || toNumberPrefix.equals("4916") || toNumberPrefix.equals("4917")) {
            treeNodeOthers.insertOtherData(Constants.CALLS_MOBILE, 1, code);
          }
        } catch (Exception e) {
          LOG.info("Couldn't get prefix for toNumber: " + cdr.getToNumber(), e);
        }
      }

    }
  }

}
