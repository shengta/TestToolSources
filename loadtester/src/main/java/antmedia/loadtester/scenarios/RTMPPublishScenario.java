package antmedia.loadtester.scenarios;


import java.util.concurrent.ConcurrentLinkedQueue;

import antmedia.loadtester.ConfigManager;
import antmedia.loadtester.protocol.RTMPPublisher;

public class RTMPPublishScenario extends AbstractScenario{
	public static final String name = "RTMP PUBLISH";
	ConcurrentLinkedQueue<RTMPPublisher> publishers = new ConcurrentLinkedQueue<>();

	@Override
	public void stepIn() {
		RTMPPublisher publisher = new RTMPPublisher(ConfigManager.conf.ORIGIN_SERVER, ConfigManager.conf.TEST_FILE, "LiveApp", ConfigManager.conf.STREAM_NAME+"-"+publishers.size(), resultDir, publishers.size());
		publishers.add(publisher);
		publisher.start();
		
		if(publishers.size() == load) {
			finishStepIn();
		}
	}

	@Override
	public void stepOut() {
		publishers.poll().stop();
		if(publishers.isEmpty()) {
			finishStepOut();
		}
	}
}
