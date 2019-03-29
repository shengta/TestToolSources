package antmedia.loadtester.scenarios;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import antmedia.loadtester.ConfigManager;
import antmedia.loadtester.InstantStat;
import antmedia.loadtester.RestManager;
import antmedia.loadtester.WebRTCClientStat;
import antmedia.loadtester.protocol.WebRTCPlayer;
import antmedia.loadtester.protocol.WebRTCPublisher;

public class WebRTCPlayScenario extends AbstractScenario{
	public static final String name = "WEBRTC PLAY";
	WebRTCPublisher publisher;
	ConcurrentLinkedQueue<WebRTCPlayer> players = new ConcurrentLinkedQueue<>();
	ArrayList<WebRTCClientStat> webrtcClientStats = new ArrayList<>(); 
	
	@Override
	void startInternal() {
		publisher = new WebRTCPublisher(ConfigManager.conf.ORIGIN_SERVER, ConfigManager.conf.TEST_FILE, "WebRTCAppEE", ConfigManager.conf.STREAM_NAME, resultDir, 0);
		publisher.start();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stepIn() {
		WebRTCPlayer player = new WebRTCPlayer(ConfigManager.conf.ORIGIN_SERVER, "WebRTCAppEE", ConfigManager.conf.STREAM_NAME, resultDir, players.size());
		players.add(player);
		player.start();
		
		if(players.size() == load) {
			finishStepIn();
		}
	}

	@Override
	public void stepOut() {
		players.poll().stop();
		if(players.isEmpty()) {
			publisher.stop();
			finishStepOut();
		}
	}
	
	@Override
	void plotStats() {
		super.plotStats();
		plotWebRTCClientBitrateStats();
		plotWebRTCClientVideoPeriodStats();
		plotWebRTCClientAudioPeriodStats();
	}

	@Override
	public void saveStats() throws IOException {
		FileWriter webrtcFile = new FileWriter(new File(resultDir, "webrtc.csv"));
		
		webrtcFile.write(WebRTCClientStat.headers+"\n");
		for (WebRTCClientStat stat : webrtcClientStats) {
			webrtcFile.write(stat.toCsv()+"\n");
		}
		webrtcFile.close();
		
		super.saveStats();
	}

	
	private void plotWebRTCClientBitrateStats() {
		XYSeries measureBitrate = new XYSeries("Measured");
		XYSeries sendBitrate = new XYSeries("Send");
		
		long t0 = stats.get(0).time;
		for (WebRTCClientStat stat : webrtcClientStats) {
			long time = stat.time-t0;
			measureBitrate.add(time, stat.measuredBitrate);
			sendBitrate.add(time, stat.sendBitrate);
		}
		
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(measureBitrate);
		dataset.addSeries(sendBitrate);
		
		plot("Bitrates vs Time","Time", "Bitrate in Kbps", dataset, "webrtc_bitrates.jpeg");
	}
	
	private void plotWebRTCClientVideoPeriodStats() {
		XYSeries videoAvg = new XYSeries("Avg Video");
		XYSeries videoMax = new XYSeries("Max Video");
		
		long t0 = stats.get(0).time;
		for (WebRTCClientStat stat : webrtcClientStats) {
			long time = stat.time-t0;
			
			videoAvg.add(time, stat.videoFrameSendPeriod);
			videoMax.add(time, stat.videoFrameSendPeriodMax);
		}
		
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(videoAvg);
		dataset.addSeries(videoMax);
		
		plot("Video Send Period vs Time","Time", "Video Send Period in ms", dataset, "webrtc_video_period.jpeg");
	}
	
	private void plotWebRTCClientAudioPeriodStats() {
		XYSeries audioAvg = new XYSeries("Avg Audio");
		XYSeries audioMax = new XYSeries("Max Audio");
		
		long t0 = stats.get(0).time;
		for (WebRTCClientStat stat : webrtcClientStats) {
			long time = stat.time-t0;
			
			audioAvg.add(time, stat.audioFrameSendPeriod);
			audioMax.add(time, stat.audioFrameSendPeriodMax);
		}
		
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(audioAvg);
		dataset.addSeries(audioMax);
		
		plot("Audio Send Period vs Time","Time", "Audio Send Period in ms", dataset, "webrtc_audio_period.jpeg");
	}

	@Override
	public void collectStats() {
		super.collectStats();
		webrtcClientStats.add(RestManager.getWebRTCClientStats(ConfigManager.conf.ORIGIN_SERVER, ConfigManager.conf.STREAM_NAME));
	}
}
