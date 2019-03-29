package antmedia.loadtester;

public class WebRTCClientStat {

	public static final String headers = "time;measuredBitrate;sendBitrate;videoFrameSendPeriod;audioFrameSendPeriod;videoFrameSendPeriodMax;audioFrameSendPeriodMax";
	
	public long time;
	public int measuredBitrate;
	public int sendBitrate;
	public int videoFrameSendPeriod;
	public int audioFrameSendPeriod;
	public int videoFrameSendPeriodMax;
	public int audioFrameSendPeriodMax;
	
	public String toCsv() {
		return time+";"+measuredBitrate+";"+sendBitrate+";"+videoFrameSendPeriod+";"+audioFrameSendPeriod+";"+videoFrameSendPeriodMax+";"+audioFrameSendPeriodMax;
	}
	
}