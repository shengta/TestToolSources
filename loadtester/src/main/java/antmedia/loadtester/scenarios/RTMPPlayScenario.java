package antmedia.loadtester.scenarios;


import java.util.concurrent.ConcurrentLinkedQueue;

import antmedia.loadtester.ConfigManager;
import antmedia.loadtester.protocol.RTMPPlayer;
import antmedia.loadtester.protocol.RTMPPublisher;

public class RTMPPlayScenario extends AbstractScenario{
	public static final String name = "RTMP PLAY";
	RTMPPublisher publisher;
	ConcurrentLinkedQueue<RTMPPlayer> players = new ConcurrentLinkedQueue<>();

	@Override
	void startInternal() {
		publisher = new RTMPPublisher(ConfigManager.conf.ORIGIN_SERVER, ConfigManager.conf.TEST_FILE, "LiveApp", ConfigManager.conf.STREAM_NAME, resultDir, 0);
		publisher.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void stepIn() {
		RTMPPlayer player = new RTMPPlayer(ConfigManager.conf.ORIGIN_SERVER, "LiveApp", ConfigManager.conf.STREAM_NAME, resultDir, players.size());
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
}
