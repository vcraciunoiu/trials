package de.schlund.rtstat.model;

/**
 * @see http://www.avm.de/de/Presse/Informationen/2005/pdf/Iptelorg_QoS-Spec.pdf
 *      1: PS=<audio packets sent> 2: OS=<audio octets sent> 3: SP=<comfort
 *      noise packets sent>,<silence packets sent> 4: SO=<silence octets sent>
 *      5: PR=<audio packets received> 6: OR=<audio octets received> 7:
 *      CR=<comfort noise packets received> 8: SR=<comfort noise octets
 *      received> 9: PL=<receive packets lost> 10:BL=<receive maximum burst
 *      packets lost> 11:EN=<encoder1 used>,<encoder2 used>... 12:DE=<decoder1
 *      used>,<decoder2 used>... - possible coder values:
 *      "G723","G729","PCMA","PCMU","iLBC-20","iLBC-30","G726-16","G726-24",
 *      "G726-32","G726-40","AMR-WB","Linear" 13:JI=<jitter in ms>
 * 
 */

public class XRTPValue {
	private Integer PS_audio_packets_sent;
	private Integer esExpectedPacketsSend;
	private Integer OS_audio_octets_sent;
	private String SP_silence_packets_sent;
	private Integer SO_silence_octets_sent;
	private Integer PR_audio_packets_received;
	private Integer erExpectedPacketsReceived;
	private Integer OR_audio_octets_received;
	private Integer CR_comfort_noise_packets_received;
	private Integer SR_comfort_noise_octets_received;
	private Integer PL_receive_packets_lost_s;
	private Integer PL_receive_packets_lost_r;
	private Integer lsSequenceLoss;
	private Integer BL_receive_maximum_burst_packets_lost;
	private String EN_encoder;
	private String DE_decoder;
	private Integer JI_jitter_s;
	private Integer JI_jitter_r;
	private Long CS_setup_time;
	private Integer RB_receive_burst_duration;
	private Integer RB_receive_burst_density;
	private Integer SB_send_burst_duration;
	private Integer SB_send_burst_density;
	private Integer DL_delay_avg;
	private Integer DL_delay_max;

	public Integer getLsSequenceLoss() {
		return lsSequenceLoss;
	}
	public void setLsSequenceLoss(Integer lsSequenceLoss) {
		this.lsSequenceLoss = lsSequenceLoss;
	}
	public Integer getErExpectedPacketsReceived() {
		return erExpectedPacketsReceived;
	}

	public void setErExpectedPacketsReceived(Integer erExpectedPacketsReceived) {
		this.erExpectedPacketsReceived = erExpectedPacketsReceived;
	}

	public Integer getEsExpectedPacketsSend() {
		return esExpectedPacketsSend;
	}

	public void setEsExpectedPacketsSend(Integer esExpectedPacketsSend) {
		this.esExpectedPacketsSend = esExpectedPacketsSend;
	}

	public Integer getBL_receive_maximum_burst_packets_lost() {
		return BL_receive_maximum_burst_packets_lost;
	}

	public void setBL_receive_maximum_burst_packets_lost(Integer bl_receive_maximum_burst_packets_lost) {
		BL_receive_maximum_burst_packets_lost = bl_receive_maximum_burst_packets_lost;
	}

	public Integer getCR_comfort_noise_packets_received() {
		return CR_comfort_noise_packets_received;
	}

	public void setCR_comfort_noise_packets_received(Integer cr_comfort_noise_packets_received) {
		CR_comfort_noise_packets_received = cr_comfort_noise_packets_received;
	}

	public String getDE_decoder() {
		return DE_decoder;
	}

	public void setDE_decoder(String de_decoder) {
		DE_decoder = de_decoder;
	}

	public String getEN_encoder() {
		return EN_encoder;
	}

	public void setEN_encoder(String en_encoder) {
		EN_encoder = en_encoder;
	}

	public Integer getOR_audio_octets_received() {
		return OR_audio_octets_received;
	}

	public void setOR_audio_octets_received(Integer or_audio_octets_received) {
		OR_audio_octets_received = or_audio_octets_received;
	}

	public Integer getOS_audio_octets_sent() {
		return OS_audio_octets_sent;
	}

	public void setOS_audio_octets_sent(Integer os_audio_octets_sent) {
		OS_audio_octets_sent = os_audio_octets_sent;
	}

	public Integer getPR_audio_packets_received() {
		return PR_audio_packets_received;
	}

	public void setPR_audio_packets_received(Integer pr_audio_packets_received) {
		PR_audio_packets_received = pr_audio_packets_received;
	}

	public Integer getPS_audio_packets_sent() {
		return PS_audio_packets_sent;
	}

	public void setPS_audio_packets_sent(Integer ps_audio_packets_sent) {
		PS_audio_packets_sent = ps_audio_packets_sent;
	}

	public Integer getSO_silence_octets_sent() {
		return SO_silence_octets_sent;
	}

	public void setSO_silence_octets_sent(Integer so_silence_octets_sent) {
		SO_silence_octets_sent = so_silence_octets_sent;
	}

	public String getSP_silence_packets_sent() {
		return SP_silence_packets_sent;
	}

	public void setSP_silence_packets_sent(String sp_silence_packets_sent) {
		SP_silence_packets_sent = sp_silence_packets_sent;
	}

	public Integer getSR_comfort_noise_octets_received() {
		return SR_comfort_noise_octets_received;
	}

	public void setSR_comfort_noise_octets_received(Integer sr_comfort_noise_octets_received) {
		SR_comfort_noise_octets_received = sr_comfort_noise_octets_received;
	}

	public Long getCS_setup_time() {
		return CS_setup_time;
	}

	public void setCS_setup_time(Long cs_setup_time) {
		CS_setup_time = cs_setup_time;
	}

	public Integer getPL_receive_packets_lost_s() {
		return PL_receive_packets_lost_s;
	}

	public void setPL_receive_packets_lost_s(Integer pl_receive_packets_lost_s) {
		PL_receive_packets_lost_s = pl_receive_packets_lost_s;
	}

	public Integer getPL_receive_packets_lost_r() {
		return PL_receive_packets_lost_r;
	}

	public void setPL_receive_packets_lost_r(Integer pl_receive_packets_lost_r) {
		PL_receive_packets_lost_r = pl_receive_packets_lost_r;
	}

	public Integer getJI_jitter_s() {
		return JI_jitter_s;
	}

	public void setJI_jitter_s(Integer ji_jitter_s) {
		JI_jitter_s = ji_jitter_s;
	}

	public Integer getJI_jitter_r() {
		return JI_jitter_r;
	}

	public void setJI_jitter_r(Integer ji_jitter_r) {
		JI_jitter_r = ji_jitter_r;
	}

	public Integer getRB_receive_burst_duration() {
		return RB_receive_burst_duration;
	}

	public void setRB_receive_burst_duration(Integer rb_receive_burst_duration) {
		RB_receive_burst_duration = rb_receive_burst_duration;
	}

	public Integer getRB_receive_burst_density() {
		return RB_receive_burst_density;
	}

	public void setRB_receive_burst_density(Integer rb_receive_burst_density) {
		RB_receive_burst_density = rb_receive_burst_density;
	}

	public Integer getSB_send_burst_duration() {
		return SB_send_burst_duration;
	}

	public void setSB_send_burst_duration(Integer sb_send_burst_duration) {
		SB_send_burst_duration = sb_send_burst_duration;
	}

	public Integer getSB_send_burst_density() {
		return SB_send_burst_density;
	}

	public void setSB_send_burst_density(Integer sb_send_burst_density) {
		SB_send_burst_density = sb_send_burst_density;
	}

	public Integer getDL_delay_avg() {
		return DL_delay_avg;
	}

	public void setDL_delay_avg(Integer dl_delay_avg) {
		DL_delay_avg = dl_delay_avg;
	}

	public Integer getDL_delay_max() {
		return DL_delay_max;
	}

	public void setDL_delay_max(Integer dl_delay_max) {
		DL_delay_max = dl_delay_max;
	}

}
