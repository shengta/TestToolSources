package antmedia.loadtester.protocol;

import java.io.File;
import java.io.IOException;

public class WebRTCPlayer {
	
	private String server;
	private String app;
	private Process process;
	private String stream;
	private File dir;
	private int id;

	public WebRTCPlayer(String serverIp, String appName, String streamId, File dir, int id) {
		this.server = serverIp;
		this.app = appName;
		this.stream = streamId;
		this.dir = dir;
		this.id = id;
	}
	
	public void start() {
		String command = "java -cp webrtc-test.jar:libs/* -Djava.library.path=libs/native io.antmedia.Starter";
		command += " -s "+server+" -i "+stream + " -u false";
		System.out.println("command:"+command);
		ProcessBuilder pb = new ProcessBuilder(command.split(" "));
		pb.directory(new File("/usr/local/test/webrtctest"));
		
		pb.redirectOutput(new File(dir, id+"_webrtc_play_out.txt"));
		pb.redirectError(new File(dir, id+"_webrtc_play_err.txt"));
		try {
			process = pb.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		long t0 = System.currentTimeMillis();
		while(process.isAlive()) {
			process.destroyForcibly();
		}
		
		System.out.println("WebRTCPlayer.stop() in:"+(System.currentTimeMillis()-t0));

	}
}
