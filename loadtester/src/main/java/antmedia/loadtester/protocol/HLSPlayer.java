package antmedia.loadtester.protocol;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;

public class HLSPlayer {
	private String server;
	private String app;
	private Process process;
	private String stream;
	private File dir;
	private int id;

	
	public HLSPlayer(String serverIp, String appName, String streamId, File dir, int id) {
		this.server = serverIp;
		this.app = appName;
		this.stream = streamId;
		this.dir = dir;
		this.id = id;
	}

	public void start() {
		String command = "ffmpeg -i http://"+server+":5080/"+app+"/streams/"+stream+".m3u8 -f null /dev/null";
		System.out.println(command);
		
		ProcessBuilder pb = new ProcessBuilder(command.split(" "));
		pb.redirectOutput(new File(dir, id+"_hls_out.txt"));
		pb.redirectError(new File(dir, id+"_hls_err.txt"));
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
