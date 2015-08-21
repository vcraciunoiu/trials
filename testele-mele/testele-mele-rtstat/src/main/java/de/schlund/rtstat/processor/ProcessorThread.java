package de.schlund.rtstat.processor;

import org.apache.log4j.Logger;

public abstract class ProcessorThread implements Runnable {
    private String _name;

    protected volatile Thread _thread;

    protected final Logger LOG;
    

    public ProcessorThread(String name) {
        _name = name;
        LOG = Logger.getLogger(this.getClass());
    }

    public String getName() {
        return _name;
    }

    public void startThread() {
        if (_thread != null) {
            LOG.error("Thread already started");
            throw new IllegalStateException("Thread "+_name+" already started");
        }

        LOG.info("starting " + _name);        
        _thread = new Thread(this);
        _thread.setName(_name);
        _thread.start();
        LOG.info("started " + _name);
    }
    
    public void stopThread() {
        if (_thread == null) {
            LOG.warn("Thread is null");
            return;
        }
        _thread = null;
        
    }
}
