package antmedia.loadtester;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;

public class TestRunner {
	
	static Process process;

	public static void runTest(String type) {
		ProcessBuilder pb = new ProcessBuilder("/home/antmedia/test/TestScriptAndTools-master/Common/runtest.sh", type);
		pb.redirectOutput(Redirect.INHERIT);
		pb.redirectError(Redirect.INHERIT);
		try {
			process = pb.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getHistory() {
		String history = "";
		try {
			FileInputStream fstream = new FileInputStream("/home/antmedia/test/results/history");
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine;

			history = "<ul>";
			while ((strLine = br.readLine()) != null)   {
				strLine = strLine.replace("/home/antmedia/test/results", "");
				history += "<li><a href='/testresults"+strLine+"/measures.png"+"' target='_blank'>"+strLine+"</a></li>";
			}
			history += "</ul>";
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return history;
	}

	public static boolean getIsTestFinished() {
		while (process.isAlive()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
		}
		
		return true;
	}

}
