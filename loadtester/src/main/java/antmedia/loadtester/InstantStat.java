package antmedia.loadtester;

public class InstantStat {
	public static final String headers = "time;cpu;memory;live streams;webrtc streams;hls players;webrtc players;rtmp players";

	public long time;
	public int cpu;
	public long memory;
	public int totalLiveStreamSize;
	public int localWebRTCLiveStreams;
	public int localHLSViewers;
	public int localWebRTCViewers;
	public int localRTMPViewers;
	
	public String toCsv() {
		return time+";"+cpu+";"+memory+";"+totalLiveStreamSize+";"+localWebRTCLiveStreams+";"+
				localHLSViewers+";"+localWebRTCViewers+";"+localRTMPViewers;
	}
}
