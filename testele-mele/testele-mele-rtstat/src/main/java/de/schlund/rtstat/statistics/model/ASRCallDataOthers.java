package de.schlund.rtstat.statistics.model;

import de.schlund.rtstat.util.Constants;

/**
 * This class holds information about miscellaneous counters which don't depend on a particular provider.
 * For example: 1&1 onNet calls, Voicebox calls, Announcements calls, calls abroad, mobile calls.
 * 
 * @author vladcrc
 */
public class ASRCallDataOthers {
    
    private long voiceboxCalls;
    private long announcementCalls;
    private long onNetCalls;
    private long abroadCalls;
    private long mobileCalls;
    private long voiceboxGoodCalls;
    private long announcementGoodCalls;
    private long onNetGoodCalls;
    private long abroadGoodCalls;
    private long mobileGoodCalls;
    
    public long getVoiceboxCalls() {
        return voiceboxCalls;
    }

    public long getAnnouncementCalls() {
        return announcementCalls;
    }

    public long getOnNetCalls() {
        return onNetCalls;
    }

    public long getAbroadCalls() {
        return abroadCalls;
    }

    public long getMobileCalls() {
        return mobileCalls;
    }

    /*
     * we return these values multiplied by 1000 in order to simplify the graph definitions in Statsalyzer.
     */
    public long getVoiceboxGoodCalls() {
        return 1000 * voiceboxGoodCalls;
    }

    public long getAnnouncementGoodCalls() {
        return 1000 * announcementGoodCalls;
    }

    public long getOnNetGoodCalls() {
        return 1000 * onNetGoodCalls;
    }

    public long getAbroadGoodCalls() {
        return 1000 * abroadGoodCalls;
    }

    public long getMobileGoodCalls() {
        return 1000 * mobileGoodCalls;
    }

    @Override
    public Object clone() {
        final ASRCallDataOthers clone = new ASRCallDataOthers();

        clone.voiceboxCalls = this.voiceboxCalls;
        clone.announcementCalls = this.announcementCalls;
        clone.onNetCalls = this.onNetCalls;
        clone.abroadCalls = this.abroadCalls;
        clone.mobileCalls = this.mobileCalls;
        clone.voiceboxGoodCalls = this.voiceboxGoodCalls;
        clone.announcementGoodCalls = this.announcementGoodCalls;
        clone.onNetGoodCalls = this.onNetGoodCalls;
        clone.abroadGoodCalls = this.abroadGoodCalls;
        clone.mobileGoodCalls = this.mobileGoodCalls;

        return clone;
    }
    
    public void reset() {
        announcementCalls = 0;
        voiceboxCalls = 0;
        onNetCalls = 0;
        abroadCalls = 0;
        mobileCalls = 0;
        announcementGoodCalls = 0;
        voiceboxGoodCalls = 0;
        onNetGoodCalls = 0;
        abroadGoodCalls = 0;
        mobileGoodCalls = 0;
    }
    
    public void insert(String graphType, int code) {
        if (graphType.equals(Constants.CALLS_VOICEBOX)) {
            voiceboxCalls++;
            if (code == 200) {
                voiceboxGoodCalls++;
            }
        } else if (graphType.equals(Constants.CALLS_ANNOUNCEMENTS)) {
            announcementCalls++;
            if (code == 200) {
                announcementGoodCalls++;
            }
        } else if (graphType.equals(Constants.CALLS_ONNET)) {
            onNetCalls++;
            if (code == 200) {
                onNetGoodCalls++;
            }
        } else if (graphType.equals(Constants.CALLS_ABROAD)) {
            abroadCalls++;
            if (code == 200) {
                abroadGoodCalls++;
            }
        } else if (graphType.equals(Constants.CALLS_MOBILE)) {
            mobileCalls++;
            if (code == 200) {
                mobileGoodCalls++;
            }
        }
    }
    
}
