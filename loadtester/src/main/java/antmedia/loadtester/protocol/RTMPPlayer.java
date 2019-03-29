package antmedia.loadtester.protocol;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;

public class RTMPPlayer {
	private String server;
	private String app;
	private Process process;
	private String stream;
	private File dir;
	private int id;

	public RTMPPlayer(String serverIp, String appName, String streamId, File dir, int id) {
		this.server = serverIp;
		this.app = appName;
		this.stream = streamId;
		this.dir = dir;
		this.id = id;
	}

	public void start() {
		String command = "ffmpeg -rtmp_live live -i rtmp://"+server+"/"+app+"/"+stream+" -f null /dev/null";
		System.out.println("command:"+command);
		ProcessBuilder pb = new ProcessBuilder(command.split(" "));
		pb.redirectOutput(new File(dir, id+"_rtmp_play_out.txt"));
		pb.redirectError(new File(dir, id+"__rtmp_play_err.txt"));
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
