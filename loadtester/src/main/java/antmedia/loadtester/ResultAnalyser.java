package antmedia.loadtester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ResultAnalyser {
	public int avgCpu = 0;
	public int maxCpu = 0;
	public long avgRam = 0;
	public long maxRam = 0;
	
	public ResultAnalyser(File resultFile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(resultFile));
		
		int count = 0;
		
		String line = reader.readLine(); //skip first line (headers)
		line = reader.readLine();
		
		while (line != null) {
			String[] tokens = line.split(";");
			
			long time = Long.parseLong(tokens[0]);
			int cpu =Integer.parseInt(tokens[1]);
			long ram = Long.parseLong(tokens[2]);
			int live =Integer.parseInt(tokens[3]);
			int webrtc =Integer.parseInt(tokens[4]);
			int hlsPlayer =Integer.parseInt(tokens[5]);
			int webrtcPlayer =Integer.parseInt(tokens[6]);
			int rtmpPlayer =Integer.parseInt(tokens[7]);
			
			maxRam = Math.max(maxRam, ram);
			maxCpu = Math.max(maxCpu, cpu);
			
			avgCpu = (avgCpu * count + cpu) / (count+1);
			avgRam = (avgRam * count + ram) / (count+1);
			
			count++;
			line = reader.readLine();
		}
	}

}
