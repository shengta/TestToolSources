package antmedia.loadtester.scenarios;


import java.util.concurrent.ConcurrentLinkedQueue;

import antmedia.loadtester.ConfigManager;
import antmedia.loadtester.protocol.WebRTCPublisher;

public class WebRTCPublisherScenario extends AbstractScenario{
	public static final String name = "WEBRTC PUBLISH";
	ConcurrentLinkedQueue<WebRTCPublisher> publishers = new ConcurrentLinkedQueue<>();

	@Override
	public void stepIn() {
		WebRTCPublisher publisher = new WebRTCPublisher(ConfigManager.conf.ORIGIN_SERVER, ConfigManager.conf.TEST_FILE, "WebRTCAppEE", ConfigManager.conf.STREAM_NAME+"-"+publishers.size(), resultDir, publishers.size());
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
