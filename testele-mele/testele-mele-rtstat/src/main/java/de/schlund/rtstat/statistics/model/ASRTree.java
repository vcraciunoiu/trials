package de.schlund.rtstat.statistics.model;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

public class ASRTree {
    private static Logger LOG = Logger.getLogger(ASRTree.class);
    private int   treenodeDuration;
    private HashMap<String, ASRTreeNode> asrTree;
    
    public ASRTree(int tnduration) {
        asrTree = new HashMap<String, ASRTreeNode>();
        treenodeDuration = tnduration;
    }

    public ASRTreeNode getTreeNode(String provider) {
        return asrTree.get(provider);
    }

    public void put(String provider, ASRTreeNode tree) {
        asrTree.put(provider,tree);
    }

    public Set<Entry<String, ASRTreeNode>> entrySet() {
        return asrTree.entrySet();
    }
    
    public void removeProvider(String provider) {
        LOG.info("Removing "+provider);
        ASRTreeNode tree = asrTree.get(provider);
        if(tree == null) {
            LOG.warn("Got no tree for provider :"+provider);
        } else {
            asrTree.remove(provider);
        }
    }
   
    public void addPrefix(String provider, String prefix, boolean freeze) {
        LOG.debug("Adding "+provider+"#"+prefix);
        ASRTreeNode tree = getTreeNode(provider);
        if (tree == null) {
            tree = new ASRTreeNode(treenodeDuration, '+', "+");
            asrTree.put(provider, tree);
        }
        tree.build(prefix, freeze);
    }
    
    public void removePrefix(String provider, String prefix) {
        LOG.info("Removing "+provider+"#"+prefix);
        ASRTreeNode tree = getTreeNode(provider);
        if(tree == null) {
            LOG.warn("Got no tree for provider :"+provider);
        } else {
            tree.remove(prefix);
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String s : asrTree.keySet()) {
            sb.append(s + ":\n------------\n");
            sb.append(asrTree.get(s).toString() + "\n");
        }
        return sb.toString();
    }
}
