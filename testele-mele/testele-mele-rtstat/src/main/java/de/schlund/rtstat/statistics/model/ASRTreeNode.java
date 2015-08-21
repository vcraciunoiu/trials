package de.schlund.rtstat.statistics.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.schlund.rtstat.model.TimebasedRingBuffer;

/**
 * @author mic
 */
public class ASRTreeNode {

    private static final Logger LOG = Logger.getLogger(ASRTreeNode.class);

    private ASRTreeNode[] _children = new ASRTreeNode[10];

    /**
     * store if a child node is frozen -> cannot be removed
     */
    private boolean[] _frozen = { false, false, false, false, false, false, false, false, false, false };

    private TimebasedRingBuffer<ASRData> _buffer;

    private char _digit;

    private String _id;
    
    private ASRCallData _data;

    private int _bufferduration;

    // these are only used in the ASRTreeNode which holds data that do not depend on a particular provider
    private ASRCallDataOthers _dataOthers;

    /**
     * Create a ASR tree node.
     * 
     * @param bufferduration
     *            how long should events be buffered
     * @param d
     *            The digit this node represents
     */

    public ASRTreeNode(int bufferduration, char d, String id) {
        super();
        _bufferduration = bufferduration;
        _buffer = new TimebasedRingBuffer<ASRData>(_bufferduration);
        _digit = d;
        _id = id;
        _data = new ASRCallData(_id);
        _dataOthers = new ASRCallDataOthers();
    }

    /**
     * Build a subtree for the given prefix (This is, in fact, a list) and
     * attach it to this node.
     * 
     * @param prefix
     */

    public void build(String prefix, boolean freeze) {
        if (prefix.length() < 1) {
            return;
        }
        int digit = Character.digit(prefix.charAt(0), 10);
        ASRTreeNode t = getOrCreateSubtree(digit, freeze);
        t.build(prefix.substring(1), freeze);

    }

    /** 
     * Remove a given prefix and maybe all
     * its subtrees if it is not a leaf from tree 
     * */

    public void remove(String prefix) {

        if (prefix.length() < 1) {
            return;
        }
        int digit = Character.digit(prefix.charAt(0), 10);
        ASRTreeNode t = _children[digit];
        if (t == null) {
            LOG.warn("Got no tree at digit[" + digit + "]");
        } else {
            if (prefix.length() == 1) { // the last tree
                LOG.debug("Nulling subtree at :" + digit);
                if (_frozen[digit]) {
                    LOG.info("received remove for :" + digit + ", which is frozen.");
                } else {
                    _children[digit] = null;
                }
            } else {
                t.remove(prefix.substring(1));
            }
        }
    }

    /**
     * insert an event with given status code and timestamp at position prefix
     * in this (sub-)tree
     * 
     * @param prefix
     * @param code
     * @param timestamp
     */

    public void insert(String prefix, short code, long timestamp) {
        if (prefix.length() == 0) {
            insert(code, timestamp);
        } else {
            int digit = Character.digit(prefix.charAt(0), 10);
            if (digit < 0 || digit > 9) {
                LOG.warn("wrong character found: '" + prefix + "'");
            } else {
                if (_children[digit] != null) {
                    final String p = prefix.substring(1);
                    _children[digit].insert(p, code, timestamp);
                } else {
                    insert(code, timestamp);
                }
            }
        }
    }

    /**
     * insert an event with given status code and timestamp at this tree node.
     * 
     * @param code
     * @param timestamp
     */

    private void insert(short code, long timestamp) {
        final ASRData asrdata = new ASRData(code, timestamp);

        _data.insert(asrdata);
        
        final List<ASRData> d = _buffer.add(asrdata, timestamp);
        // correction for elements which were removed from buffer
        updateState(d);
        
        // correction for inserted entry
    }

    private void updateState(final List<ASRData> data) {
        if (data != null) {
            for (ASRData asr : data) {
                // remove a good call -> decrease denormalized ASR
                _data.remove(asr);
            }
        }
    }

    /**
     * Return the subtree for the given digit. Create one if it does not exist
     * yet.
     * 
     * @param digit
     * @return
     */

    private ASRTreeNode getOrCreateSubtree(int digit, boolean freeze) {
        if (_children[digit] == null) {
            _children[digit] = new ASRTreeNode(_bufferduration, Character.forDigit(digit, 10), _id + digit);
            if (freeze) {
                _frozen[digit] = true;
            }
        }
        return _children[digit];
    }

    /**
     * Aggregate the ASR data for this node and all subtrees.
     */

    public ASRCallData collectASRCallData() {
        final ASRCallData result = getASRCallData();
        for (int i = 0; i < 10; i++) {
            if (_children[i] != null) {
                final ASRCallData tmp = _children[i].collectASRCallData();
                result.add(tmp);
            }
        }
        return result;
    }

    /**
     * needed for monitoring output
     */
    public long getBuffersize() {
        long buffersize = _buffer.size();
        for (int i = 0; i < 10; i++) {
            if (_children[i] != null) {
                buffersize += _children[i].getBuffersize();
            }
        }
        return buffersize;
    }

    public ASRCallData getASRCallData() {
        // check for out-dated  buffer elements
        final List<ASRData> d = _buffer.update();
        updateState(d);

        final ASRCallData ret = (ASRCallData) _data.clone();
        ret.setTimestampOldest(_buffer.size() == 0 ? System.currentTimeMillis() : _buffer.peek().getTimestamp());
        
        return ret;
    }

    /**
     * Get ASR data for node at position prefix in this tree
     * 
     * @param prefix
     * @return
     */

    public ASRCallData getASRCallData(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException(
                    "prefix may not be null. If you want the information for this node use an empty string as prefix.");
        }
        ASRCallData ret = null;
        if (prefix.length() == 0) {
            ret = getASRCallData();
        } else {
            int digit = Character.digit(prefix.charAt(0), 10);
            if (_children[digit] == null) {
                ret = new ASRCallData(_id + prefix);
            } else {
                ret = _children[digit].getASRCallData(prefix.substring(1));
            }
        }
        return ret;
    }

    /**
     * Aggregate ASR data starting with node at position prefix in this tree
     * 
     * @param prefix
     * @return
     */

    public ASRCallData collectASRCallData(String prefix) {
        ASRCallData ret = null;
        if (prefix.length() == 0) {
            ret = collectASRCallData();
        } else {
            int digit = Character.digit(prefix.charAt(0), 10);
            if (_children[digit] == null) {
                ret = new ASRCallData(_id + prefix);
            } else {
                ret = _children[digit].collectASRCallData(prefix.substring(1));
            }
        }
        return ret;
    }

    public StringBuilder subtreeToString(StringBuilder sb, String prefix) {
        StringBuilder ret = null;
        if (prefix.length() == 0) {
            ret = toString(sb, "");
        } else {
            int digit = Character.digit(prefix.charAt(0), 10);
            if (_children[digit] == null) {
                ret = sb;
            } else {
                ret = _children[digit].toString(sb, prefix.substring(1));
            }
        }
        return ret;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        return toString(sb, "").toString();
    }

    /**
     * Done because we won't the view to display children as one string 
     * but each child as a String Array (@see ASRStatusServlet). This has to 
     * be done in order to checkout whether the ASR is already in a a critical 
     * area for the current Child.  
     */
    public List<String[]> toViewableString(int critical) {
        List<String[]> curList = new ArrayList<String[]>();
        return collectChildren(curList, critical, "");
    }

    private StringBuilder toString(StringBuilder sb, String prefix) {

        ASRCallData asrArray = getASRCallData();
        String d = asrArray.getTimestampOldestFormatted();
        long asrPromille = asrArray.getRatio();

        String myPrefix = prefix + _digit;

        sb.append(d).append(' ').append(asrPromille).append(' ').append(asrArray.getAllCalls()).append(' ').append(
                myPrefix).append('\n');
        for (int i = 0; i < 10; i++) {
            if (_children[i] != null) {
                _children[i].toString(sb, myPrefix);
            }
        }
        return sb;
    }

    private List<String[]> collectChildren(List<String[]> list, int critical, String prefix) {
        String[] curChild = new String[2];
        ASRCallData asrArray = collectASRCallData();
        String d = asrArray.getTimestampOldestFormatted();
        String myPrefix = prefix + _digit;
        long asr = asrArray.getRatio();

        curChild[0] = asr < critical ? "critical" : "ok";
        curChild[1] = d + " " + asr + "=" + asrArray.getGoodCalls() + "/" + asrArray.getAllCalls() + " " + myPrefix;
        list.add(curChild);

        for (int i = 0; i < 10; i++) {
            if (_children[i] != null) {
                _children[i].collectChildren(list, critical, myPrefix);
            }
        }

        return list;
    }

    public void insertOtherData(String graphType, int flag, int code) {
        if (flag == 0) {
            _data.insertOtherData(graphType);
        } else { // this is for inserting data that do not depend on a particular provider
            _dataOthers.insert(graphType, code);
        }
    }
    
    public ASRCallDataOthers collectASRCallDataOthers() {
        final ASRCallDataOthers ret = (ASRCallDataOthers) _dataOthers.clone();
        _dataOthers.reset();
        return ret;
    }

}
