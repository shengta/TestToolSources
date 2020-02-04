package antmedia.loadtester.protocol;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;

public class WebRTCPublisher {
	private String server;
	private String app;
	private Process process;
	private String stream;
	private String file;
	private File dir;
	private int id;
	
	public WebRTCPublisher(String serverIp, String file, String appName, String streamId, File dir, int id) {
		this.server = serverIp;
		this.file = file;
		this.app = appName;
		this.stream = streamId;
		this.dir = dir;
		this.id = id;
	}

	public void start() {
		String command = "java -cp webrtc-test.jar:libs/* -Djava.library.path=libs/native io.antmedia.Starter";
		command += " -s "+server+" -m publisher -f "+file+" -i "+stream+" -u false";
		ProcessBuilder pb = new ProcessBuilder(command.split(" "));
		pb.directory(new File("/usr/local/test/webrtctest"));

		pb.redirectOutput(new File(dir, id+"_webrtc_publish_out.txt"));
		pb.redirectError(new File(dir, id+"_webrtc_publish_err.txt"));
		try {
			process = pb.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		process.destroyForcibly();
	}
}
