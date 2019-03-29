package antmedia.loadtester.scenarios;


import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import antmedia.loadtester.ConfigManager;
import antmedia.loadtester.InstantStat;
import antmedia.loadtester.protocol.HLSPlayer;
import antmedia.loadtester.protocol.RTMPPlayer;
import antmedia.loadtester.protocol.RTMPPublisher;

public class HLSPlayScenario extends AbstractScenario{
	public static final String name = "HLS PLAY";
	RTMPPublisher publisher;
	ConcurrentLinkedQueue<HLSPlayer> players = new ConcurrentLinkedQueue<>();

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
		HLSPlayer player = new HLSPlayer(ConfigManager.conf.ORIGIN_SERVER, "LiveApp", ConfigManager.conf.STREAM_NAME, resultDir, players.size());
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
