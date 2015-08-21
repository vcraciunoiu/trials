package de.schlund.rtstat.model;

import java.util.Date;
import java.util.Properties;

/**
 * @author mic
 * @author Frank Spychalski (<a
 *         href="mailto:spychalski@schlund.de">spychalski@schlund.de</a>)
 */
public class SERLogEvent {
    private long _timestamp;
    private Method _method;
    private String _fromTag, _toTag;
    private String _call_id;
    private short _code;
    private UserDomain _iURI, _oURI;
    private UserDomain _from, _to;
    private String _identity;
    private String _fromProvider;
    private String _flags;
    private String _lastDst;
    private String _ua1, _ua2;
    private XRTPValue _xrtp1, _xrtp2;
    private String _target;
    private String _contactDomain;
    private Properties _properties;
    private boolean _valid;
    private String migrated;

    public SERLogEvent() {
        _valid = false;
    }

    public void setCallId(String ci) {
        this._call_id = ci;
    }

    public String getCall_id() {
        return _call_id;
    }

    public String getCDRCall_id(boolean reverse) {
        if (!reverse) {
            return _call_id + ":" + _fromTag + ":" + _toTag;
        }

        return _call_id + ":" + _toTag + ":" + _fromTag;
    }

    public void setCode(short co) {
        this._code = co;
    }

    public short getCode() {
        return _code;
    }

    public void setCode(Object object) {
        this._code = (Short) object;
    }

    public long getTimestamp() {
        return _timestamp;
    }

    public void setTimestamp(long newTimestamp) {
        _timestamp = newTimestamp;
    }

    public void setMethod(Method m) {
        this._method = m;
    }

    public Method getMethod() {
        return _method;
    }

    public String getTarget() {
        return _target;
    }

    public void setLastDestination(String ld) {
        this._lastDst = ld;
    }

    public String getLastDestination() {
        return _lastDst;
    }

    public void setIURI(UserDomain ud) {
        this._iURI = ud;
    }

    public UserDomain getIURI() {
        return _iURI;
    }

    public void setOURI(UserDomain ud) {
        this._oURI = ud;
    }

    public UserDomain getOURI() {
        return _oURI;
    }

    public void setFrom(UserDomain ud) {
        this._from = ud;
    }

    public UserDomain getFrom() {
        return _from;
    }

    public void setTo(UserDomain ud) {
        this._to = ud;
    }

    public UserDomain getTo() {
        return _to;
    }

    public String getFromTag() {
        return _fromTag;
    }

    public void setFromTag(String tag) {
        _fromTag = tag;
    }

    public void setToTag(String tag) {
        _toTag = tag;
    }

    public String getToTag() {
        return _toTag;
    }

    private Properties getProperties() {
        if (_properties == null) {
            _properties = new Properties();
        }
        return _properties;
    }

    public void setProperty(String name, String value) {
        if (name == null) {
            throw new IllegalArgumentException("name may not be null");
        }
        if (value == null) {
            // ignore
        } else {
            getProperties().put(name, value);
        }
    }

    public String getProperty(String key) {
        if (_properties == null) {
            return null;
        }
        return _properties.getProperty(key);
    }

    public void setValid() {
        _valid = true;
    }

    public boolean isValid() {
        return _valid;
    }

    public void setUserAgent(String s) {
        this._ua1 = s;
        this._ua2 = s;
    }

    public void setTarget(String s) {
        this._target = s;
    }

    public String getIdentity() {
        return _identity;
    }

    public void setIdentity(String identity) {
        this._identity = identity;
    }

    public String getFlags() {
        return _flags;
    }

    public void setFlags(String flags) {
        this._flags = flags;
    }

    public String getUa1() {
        return _ua1;
    }

    public void setUa1(String ua1) {
        this._ua1 = ua1;
    }

    public String getUa2() {
        return _ua2;
    }

    public void setUa2(String ua2) {
        this._ua2 = ua2;
    }

    public XRTPValue getXrtp1() {
        return _xrtp1;
    }

    public void setXrtp1(XRTPValue xrtp1) {
        this._xrtp1 = xrtp1;
    }

    public XRTPValue getXrtp2() {
        return _xrtp2;
    }

    public void setXrtp2(XRTPValue xrtp2) {
        this._xrtp2 = xrtp2;
    }

    public String getFromProvider() {
        return _fromProvider;
    }

    public void setFromProvider(String provider) {
        _fromProvider = provider;
    }

    public void setContactDomain(String ct) {
        _contactDomain = ct;
    }

    public String getContactDomain() {
        return _contactDomain;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CallID[" + _call_id + "]").append("\n");
        sb.append("Valid[" + _valid + "]").append("\n");
        sb.append("Timestamp[" + new Date(_timestamp) + "]").append("\n");
        sb.append("Method[" + _method + "]").append("\n");
        sb.append("I-URI(User@Domain)[" + _iURI + "]").append("\n");
        sb.append("O-URI(User@Domain)[" + _oURI + "]").append("\n");
        sb.append("From(User@Domain)[" + _from + "]").append("\n");
        sb.append("To(User@Domain)[" + _to + "]").append("\n");
        sb.append("Code[" + _code + "]").append("\n");
        sb.append("UA1[" + _ua1 + "]").append("\n");
        sb.append("TARGET[" + _target + "]").append("\n");
        sb.append("Properties[" + _properties + "]").append("\n");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_call_id == null) ? 0 : _call_id.hashCode());
        result = prime * result + _code;
        result = prime * result + ((_from == null) ? 0 : _from.hashCode());
        result = prime * result + ((_fromTag == null) ? 0 : _fromTag.hashCode());
        result = prime * result + ((_iURI == null) ? 0 : _iURI.hashCode());
        result = prime * result + ((_lastDst == null) ? 0 : _lastDst.hashCode());
        result = prime * result + ((_method == null) ? 0 : _method.hashCode());
        result = prime * result + ((_oURI == null) ? 0 : _oURI.hashCode());
        result = prime * result + ((_properties == null) ? 0 : _properties.hashCode());
        result = prime * result + ((_target == null) ? 0 : _target.hashCode());
        result = prime * result + (int) (_timestamp ^ (_timestamp >>> 32));
        result = prime * result + ((_to == null) ? 0 : _to.hashCode());
        result = prime * result + ((_toTag == null) ? 0 : _toTag.hashCode());
        result = prime * result + ((_ua1 == null) ? 0 : _ua1.hashCode());
        result = prime * result + ((_identity == null) ? 0 : _identity.hashCode());
        result = prime * result + (_valid ? 1231 : 1237);
        result = prime * result + ((_xrtp1 == null) ? 0 : _xrtp1.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof SERLogEvent))
            return false;
        final SERLogEvent other = (SERLogEvent) obj;
        if (_call_id == null) {
            if (other._call_id != null)
                return false;
        } else if (!_call_id.equals(other._call_id))
            return false;
        if (_code != other._code)
            return false;
        if (_from == null) {
            if (other._from != null)
                return false;
        } else if (!_from.equals(other._from))
            return false;
        if (_fromTag == null) {
            if (other._fromTag != null)
                return false;
        } else if (!_fromTag.equals(other._fromTag))
            return false;
        if (_iURI == null) {
            if (other._iURI != null)
                return false;
        } else if (!_iURI.equals(other._iURI))
            return false;
        if (_lastDst == null) {
            if (other._lastDst != null)
                return false;
        } else if (!_lastDst.equals(other._lastDst))
            return false;
        if (_method == null) {
            if (other._method != null)
                return false;
        } else if (!_method.equals(other._method))
            return false;
        if (_oURI == null) {
            if (other._oURI != null)
                return false;
        } else if (!_oURI.equals(other._oURI))
            return false;
        if (_properties == null) {
            if (other._properties != null)
                return false;
        } else if (!_properties.equals(other._properties))
            return false;
        if (_target == null) {
            if (other._target != null)
                return false;
        } else if (!_target.equals(other._target))
            return false;
        if (_timestamp != other._timestamp)
            return false;
        if (_to == null) {
            if (other._to != null)
                return false;
        } else if (!_to.equals(other._to))
            return false;
        if (_toTag == null) {
            if (other._toTag != null)
                return false;
        } else if (!_toTag.equals(other._toTag))
            return false;
        if (_ua1 == null) {
            if (other._ua1 != null)
                return false;
        } else if (!_ua1.equals(other._ua1))
            return false;
        if (_identity == null) {
            if (other._identity != null)
                return false;
        } else if (!_identity.equals(other._identity))
            return false;
        if (_valid != other._valid)
            return false;
        if (_xrtp1 == null) {
            if (other._xrtp1 != null)
                return false;
        } else if (!_xrtp1.equals(other._xrtp1))
            return false;
        return true;
    }

    public String getMigrated() {
        return migrated;
    }

    public void setMigrated(String migrated) {
        this.migrated = migrated;
    }

}
