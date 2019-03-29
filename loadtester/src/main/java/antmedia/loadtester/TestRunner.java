package antmedia.loadtester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.Arrays;

import antmedia.loadtester.scenarios.AbstractScenario;
import antmedia.loadtester.scenarios.HLSPlayScenario;
import antmedia.loadtester.scenarios.RTMPPlayScenario;
import antmedia.loadtester.scenarios.RTMPPublishScenario;
import antmedia.loadtester.scenarios.WebRTCPlayScenario;
import antmedia.loadtester.scenarios.WebRTCPublisherScenario;

public class TestRunner {

	static AbstractScenario scenario;
	static boolean logedIn = false;

	public static void runTest(String type) {

		if(!logedIn) {
			RestManager.signUp(ConfigManager.conf.ORIGIN_SERVER);
			RestManager.login(ConfigManager.conf.ORIGIN_SERVER);
			logedIn = true;
		}

		if(type.contentEquals(RTMPPublishScenario.name)) {
			scenario = new RTMPPublishScenario();
		}
		else if(type.contentEquals(RTMPPlayScenario.name)) {
			scenario = new RTMPPlayScenario();
		}
		else if(type.contentEquals(HLSPlayScenario.name)) {
			scenario = new HLSPlayScenario();
		}
		else if(type.contentEquals(WebRTCPublisherScenario.name)) {
			scenario = new WebRTCPublisherScenario();
		}
		else if(type.contentEquals(WebRTCPlayScenario.name)) {
			scenario = new WebRTCPlayScenario();
		}

		scenario.start();
	}

	public static String getHistory() {
		String history = "";
		File file = new File("/home/antmedia/test/results");
		if(file.exists()) {
			String[] dirs = file.list();
			Arrays.sort(dirs);
			history = "<ul>";
			for (String dir : dirs) {
				history += "<li><a href='/testresults/"+dir+"/report.html"+"' target='_blank'>"+dir+"</a></li>";
			}
			history += "</ul>";
		}

		return history;
	}

	public static boolean getIsTestFinished() {
		while (scenario.isRunning()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
		}

		return true;
	}

}
