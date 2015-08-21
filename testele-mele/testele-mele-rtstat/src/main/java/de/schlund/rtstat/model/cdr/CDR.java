package de.schlund.rtstat.model.cdr;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import de.schlund.rtstat.model.Method;
import de.schlund.rtstat.model.SERLogEvent;
import de.schlund.rtstat.model.UserDomain;
import de.schlund.rtstat.statistics.model.XRTPCallData;

/**
 * @author mic
 * @author Frank Spychalski (<a
 *         href="mailto:spychalski@schlund.de">spychalski@schlund.de</a>)
 */
public class CDR {

    private static Logger LOG = Logger.getLogger(CDR.class);

    public enum STATE {
        INVITE, ACK, BYE, ACK_DISCARDED, BYE_DISCARDED
    }

    private STATE _state;

    private UserDomain _a, _b;

    private SERLogEvent _ack, _bye;

    /**
     * used to store the invites
     */
    private LinkedList<SERLogEvent> invites;

    private XRTPCallData _xrtpcalldata;

    private ArrayList<Short> _lastDstCodes;

    private ArrayList<String[]> _lastDst;

    private boolean byeSentByCaller;

    public CDR(String a_user, String a_provider, String b_user, String b_provider, SERLogEvent le) {
        _a = new UserDomain(a_user, a_provider);
        _b = new UserDomain(b_user, b_provider);
        invites = new LinkedList<SERLogEvent>();
        if (le.getMethod().equals(Method.INVITE)) {
            _state = STATE.INVITE;
            invites.add(le);
        }
        _lastDstCodes = new ArrayList<Short>();
        _lastDst = new ArrayList<String[]>();
    }

    public void updateINVITE(SERLogEvent invite) {
        if (_state != STATE.INVITE) {
            LOG.warn("no longer in state INVITE: " + this.toString() + " SERLogEvent: " + (invite == null ? "null" : invite.toString()));
        }
        invites.add(invite);
        Comparator<SERLogEvent> comp = new InvitesComparator();
        Collections.sort(invites, comp);
    }

    public void setStateACK(SERLogEvent le) {
        if (_state != STATE.INVITE) {
            LOG.error("ack received but was not in state INVITE: " + this.toString() + " SERLogEvent: " + (le == null ? "null" : le.toString()));
        }
        _state = STATE.ACK;
        _ack = le;
    }

    public void setStateBYE(SERLogEvent le) {
        if (_state != STATE.ACK) {
            LOG.warn("bye received but was not in state ACK: " + this.toString() + " SERLogEvent: " + (le == null ? "null" : le.toString()));
        }
        _state = STATE.BYE;
        _bye = le;
        setByeSentByCaller();
    }

    /**
     * @return Returns the a.
     */
    public UserDomain getA() {
        return _a;
    }

    /**
     * @return Returns the b.
     */
    public UserDomain getB() {
        return _b;
    }

    /**
     * @return Returns the call_id.
     */
    public String getCall_id() {
        return getInviteOrAck().getCall_id();
    }

    public String getCDRCall_id() {
        return getInviteOrAck().getCDRCall_id(false);
    }

    /**
     * @return Returns the endtime.
     */
    public long getEndtime() {
        if (_state == STATE.BYE) {
            return _bye.getTimestamp();
        } else {
            return getStarttime();
        }
    }

    /**
     * @return Returns the starttime.
     */
    public long getStarttime() {
        return getInviteOrAck().getTimestamp();
    }

    public String getFromNumber() {
        String fromNumber = null;
        /*
         * tests the flags binary representation if the 15 bit is set and from
         * number is set to mi-log value
         */
        if (getFlagsBinaryValue().testBit(15)) {
            fromNumber = getAckOrInvite().getMigrated();
        } else {
            if (getAckOrInvite().getFrom().getUser().equalsIgnoreCase("Anonymous")) {
                fromNumber = getAckOrInvite().getIdentity();
            } else {
                fromNumber = getAckOrInvite().getFrom().getUser();
            }
        }

        fromNumber = normalizeNumber(fromNumber);
        return fromNumber;
    }

    public String getFromDomain() {
        return getAckOrInvite().getFrom().getDomain();
    }

    public String getToNumber() {
        String user = null;
        try {
            user = getAckOrInvite().getTo().getUser();
        } catch (Exception e) {
            LOG.error("Couldn't get toNumber, setting to null.");
        }
        return normalizeNumber(user);
    }

    public String getToDomain() {
        String domain = null;
        try {
            domain = getAckOrInvite().getTo().getDomain();
        } catch (Exception e) {
            LOG.error("Couldn't get toDomain, setting to null.");
        }
        return domain;
    }

    public String getTarget() {
        StringBuffer completeTarget = new StringBuffer();

        int size = invites.size();
        if (size == 1 && invites.get(0).getTo() != null) {
            completeTarget.append(normalizeNumber(invites.get(0).getTo().getUser())).append(";").append(invites.get(0).getTarget());
        } else if (size > 1) {
            for (int i = 0; i < size; i++) {
                if (i == size - 1) {
                    completeTarget.append(normalizeNumber(invites.get(i).getOURI().getUser())).append(";").append(invites.get(i).getTarget());
                } else {
                    if (invites.get(i).getTo() != null) {
                        completeTarget.append(normalizeNumber(invites.get(i).getTo().getUser())).append(";").append(invites.get(i).getTarget());
                        completeTarget.append(",");
                    }
                }
            }
        }

        return completeTarget.toString();
    }

    public SERLogEvent getAckOrInvite() {
        if (_ack != null) {
            return _ack;
        } else if (invites.size() > 0) {
            return getProperInvite();
        } else {
            throw new IllegalStateException("CDR contains neither INVITE nor ACK event");
        }
    }

    public SERLogEvent getInviteOrAck() {
        if (invites.size() > 0) {
            return getProperInvite();
        } else if (_ack != null) {
            return _ack;
        } else {
            throw new IllegalStateException("CDR contains neither INVITE nor ACK event");
        }
    }

    public SERLogEvent getACK() {
        return _ack;
    }

    public SERLogEvent getLastINVITE() {
        if (invites.size() > 0) {
            return invites.getLast();
        }
        return getInviteOrAck();
    }

    public SERLogEvent getFirstINVITE() {
        if (invites.size() > 0) {
            return invites.getFirst();
        }
        return getInviteOrAck();
    }

    public SERLogEvent getBYE() {
        return _bye;
    }

    /**
     * @return duration of this call in ms
     */
    public long getDuration() {
        if (getCode() != 200) {
            return -1;
        }
        return getEndtime() - getStarttime();
    }

    /**
     * @return Returns the code.
     */
    public short getCode() {
        return getAckOrInvite().getCode();
    }

    /**
     * @return Returns the state.
     */
    public STATE getState() {
        return _state;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("State[" + (_state == null ? "null" : _state.name()) + "]").append("\n");
        sb.append("Endtime[" + new Date(getEndtime()) + "]").append("\n");
        sb.append("A User/Provider[" + (_a == null ? "null" : _a.toString()) + "]").append("\n");
        sb.append("B User/Provider[" + (_b == null ? "null" : _b.toString()) + "]").append("\n");
        sb.append("Callid[" + getCall_id() + "]").append("\n");
        if (LOG.isDebugEnabled()) {
            printDebug(sb);
        }
        return sb.toString();
    }

    public void printDebug(StringBuilder sb) {
        if (invites.size() > 0) {
            sb.append("first INVITE [" + invites.getFirst() + "]").append("\n");
        }
        if (invites.size() > 1) {
            sb.append("last INVITE [" + invites.getLast() + "]").append("\n");
        }
        if (_ack != null) {
            sb.append("ACK [" + _ack + "]").append("\n");
        }
        if (_bye != null) {
            sb.append("BYE [" + _bye + "]").append("\n");
        }
    }

    public XRTPCallData getXRTPCallData() {
        return _xrtpcalldata;
    }

    public void setXRTPCallData(XRTPCallData xrtpcalldata) {
        this._xrtpcalldata = xrtpcalldata;
    }

    public boolean isValid() {
        boolean ret = true;
        if (_ack == null || _ack.getFrom() == null) {
            logInvalid("FROM is null");
            ret = false;
        }
        return ret;
    }

    private void logInvalid(String msg) {
        LOG.warn("CDR[" + this.hashCode() + "] not valid[" + msg + "]: \n" + toString());
    }

    private static class InvitesComparator implements Comparator<SERLogEvent> {
        public int compare(SERLogEvent e1, SERLogEvent e2) {
            if ((e1.getTarget().contains("location") || 
                 e1.getTarget().contains("pstn")) && 
                (!e2.getTarget().contains("location") || 
                 !e2.getTarget().contains("pstn"))) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    public ArrayList<Short> getLastDstCodes() {
        try {
            String finalNumber = getLastINVITE().getOURI().getDomain() + "=" + getCode();
            String[] pairs = getLastINVITE().getLastDestination().split(";");
            for (String pair : pairs) {
                if (!pair.equals(finalNumber)) {
                    String[] values = pair.split("=");
                    _lastDstCodes.add(Short.parseShort(values[1]));
                }
            }
        } catch (Exception e) {
            LOG.debug("Last destination not in expected format.");
        }
        return _lastDstCodes;
    }

    private String normalizeNumber(String input) {
        String result = null;
        try {
            if (input.length() > 2) {
                if (input.charAt(0) == '+') {
                    result = input.substring(1);
                } else if (input.substring(0, 3).equals("000")) {
                    result = "49".concat(input.substring(3));
                } else if (input.substring(0, 2).equals("00")) {
                    result = input.substring(2);
                } else if (input.charAt(0) == '0') {
                    result = "49".concat(input.substring(1));
                } else {
                    result = input;
                }
            } else {
                result = input;
            }
            if (result.contains("\'")) {
                result = result.replace("\'", "");
            }
            if (result.contains("\\")) {
                result = result.replace("\\", "");
            }

        } catch (Exception e) {
            LOG.warn("Error normalizing number: " + input, e);
        }
        return result;
    }

    public String getFinalNumber() {
        String number = null;
        if (getFlagsBinaryValue().testBit(9)) {
            number = getProperInvite().getTo().getUser();
        } else {
            number = getProperInvite().getOURI().getUser();
        }
        return normalizeNumber(number);
    }

    public String getFinalDomain() {
        String number = null;
        number = getProperInvite().getOURI().getDomain();
        return number;
    }

    final static int resp_class_prio[] = { 32000, /* 0-99, special */
    11000, /* 1xx, special, should never be used */
    0, /*
         * 2xx, high priority (not used, 2xx are immediately forwarded and
         * t_pick_branch will never be called if a 2xx was received)
         */
    3000, /* 3xx */
    4000, /* 4xx */
    5000, /* 5xx */
    1000 /* 6xx, highest priority */
    };

    /**
     * This method returns the proper invite for building the CDR, based on the
     * codes priority
     * 
     * @return
     */
    private SERLogEvent getProperInvite() {
        SERLogEvent e = null;
        Iterator<SERLogEvent> it = invites.iterator();

        while (it.hasNext()) {
            SERLogEvent ex = it.next();
            if (e == null) {
                e = ex;
            } else {
                if (get_prio(e.getCode()) > get_prio(ex.getCode())) {
                    e = ex;
                }
            }
        }
        return e;
    }

    public static int get_4xx_prio(int xx) {
        switch (xx) {
        case 1:
        case 7:
        case 15:
        case 20:
        case 84:
            return xx;
        default:
            return 100 + xx;
        }
    }

    public static int get_prio(int resp) {
        int c;
        int xx;

        c = resp / 100;

        if (c < 7) {
            xx = resp % 100;
            return resp_class_prio[c] + ((c == 4) ? get_4xx_prio(xx) : xx);
        }
        return 10000 + resp; /*
                                 * unknown response class => return very low
                                 * prio
                                 */
    }

    private void setByeSentByCaller() {
        // now we check whether the BYE comes from caller or callee
        //
        // They are a small number of cases were a bye can come from
        // both parties, when it happens that they terminate the call
        // on the same time. Then we get two bye results, one 200 OK
        // and also one 487. From RFC3261 15.1.2:
        // The UAS MUST still respond to any pending requests received for that
        // dialog. It is RECOMMENDED that a 487 (Request Terminated) response
        // be generated to those pending requests.
        // We ignore this for now, as its not critical, and just return the
        // first
        // received BYE, i think.
        try {
            if (getBYE() != null && getInviteOrAck().getTo() != null) {
                String inviteFrom, inviteTo, byeFrom, byeTo;
                inviteFrom = getInviteOrAck().getFrom().getUser();
                inviteTo = getInviteOrAck().getTo().getUser();
                byeFrom = getBYE().getFrom().getUser();
                byeTo = getBYE().getTo().getUser();

                if (inviteFrom.equals(byeFrom) && inviteTo.equals(byeTo)) {
                    // that means the BYE was sent bye the caller
                    byeSentByCaller = true;
                } else {
                    byeSentByCaller = false;
                }
            }
        } catch (Exception e) {
            LOG.error("Error setting ByeSentByCaller: ", e);
        }
    }

    public boolean getByeSentByCaller() {
        return byeSentByCaller;
    }

    public String getBillUser() {
        String billUser = null;
        if (getBYE() == null) {
            if (getFlagsBinaryValue().testBit(15)) {
                billUser = getLastINVITE().getMigrated();
            } else {
                billUser = getLastINVITE().getFrom().getUser();
            }
        } else if (getBYE().getFrom() != null && getBYE().getTo() != null) {
            if (getByeSentByCaller() == true) {
                if (getFlagsBinaryValue().testBit(15)) {
                    billUser = getLastINVITE().getMigrated();
                } else {
                    billUser = getBYE().getFrom().getUser();
                }
            } else {
                billUser = getBYE().getTo().getUser();
            }
        }
        if (billUser != null && billUser.equalsIgnoreCase("Anonymous")) {
            if (getFlagsBinaryValue().testBit(15)) {
                billUser = getInviteOrAck().getMigrated();
            } else {
                billUser = getInviteOrAck().getIdentity();
            }
        }
        billUser = normalizeNumber(billUser);
        return billUser;
    }

    public void setStateACKdiscarded() {
        _state = STATE.ACK_DISCARDED;
    }

    public void setStateBYEdiscarded() {
        _state = STATE.BYE_DISCARDED;
    }

    public ArrayList<String[]> getLastDst() {
        try {
            String[] pairs = getLastINVITE().getLastDestination().split(";");
            for (String pair : pairs) {
                String[] values = pair.split("=");
                _lastDst.add(values);
            }
        } catch (Exception e) {
            LOG.debug("Last destination not in expected format.");
        }
        return _lastDst;
    }

    /**
     * This method gives the decimal representation of the flags. Example:
     * "1100100010001000100010001000100010001000100010001000100010001000" is
     * translated into "14449949524405815432".
     * 
     * The meaning of the bits is (1 is lowest, 64 is the highest): - 1-32:
     * reserved for OpenSER usage. - 33: ring - 34: who terminated the call -
     * 35: was it an emergency call - 36: was an ACK log line found - 37: was a
     * BYE log line found - 38-64: reserved for future RtStat usage.
     * 
     * @param cdr
     * @param byeSentByCaller
     * @return
     */
    public BigInteger getFlagsBinaryValue() {
        // first 27 bits; they are not used so far
        StringBuilder flags = new StringBuilder("000000000000000000000000000");
        
        // was a bye log found ?
        flags.append(getBYE() != null ? "1" : "0");
        // was an ACK log line found ?
        flags.append(getACK() != null ? "1" : "0");
        // was it an emergency call ?
        // NOTE: not necessary to set it here, as with new log format it is set in OpenSER flags.
        flags.append("0");
        // who terminated the call
        if (getBYE() != null && getByeSentByCaller() == false) {
            flags.append("1");
        } else {
            flags.append("0");
        }

        // ring
        flags.append("0");

        // now append the least significant part, which belongs to OpenSER
        String flagsOpenSERdecimal = getProperInvite().getFlags();
        String flagsOpenSERbinary = Integer.toBinaryString(Integer.parseInt(flagsOpenSERdecimal == null ? "0" : flagsOpenSERdecimal));
        for (int j = 0; j < 32 - flagsOpenSERbinary.length(); j++) {
            flags.append("0");
        }
        flags.append(flagsOpenSERbinary);

        return new BigInteger(flags.toString(), 2);
    }
}
