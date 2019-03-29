package antmedia.loadtester.protocol;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;

public class RTMPPublisher {
	private String server;
	private String app;
	private Process process;
	private String stream;
	private String file;
	private File dir;
	private int id;
	
	public RTMPPublisher(String serverIp, String file, String appName, String streamId, File dir, int id) {
		this.server = serverIp;
		this.file = file;
		this.app = appName;
		this.stream = streamId;
		this.dir = dir;
		this.id = id;
	}

	public void start() {
		String command = "ffmpeg -re -i "+file+" -codec copy -f flv rtmp://"+server+"/"+app+"/"+stream;
		
		System.out.println(command);
		
		ProcessBuilder pb = new ProcessBuilder(command.split(" "));
		pb.redirectOutput(new File(dir, id+"_rtmp_publish_out.txt"));
		pb.redirectError(new File(dir, id+"_rtmp_publish_err.txt"));
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
