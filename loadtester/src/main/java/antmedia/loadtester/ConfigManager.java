package antmedia.loadtester;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

import com.google.gson.Gson;

public class ConfigManager {
	
	static Config conf;
	static String file = "/home/antmedia/test/TestScriptAndTools-master/Common/config.json";
	static String fileJson = ""; 
	
	public static String getConfig() {
		if(conf == null) {
			readFromFile();
			conf = new Gson().fromJson(fileJson, Config.class);
		}
		
		return new Gson().toJson(conf);
	}

	public static void setConfig(String confJson) {
		System.out.println("conf:"+confJson);
		
		conf = new Gson().fromJson(confJson, Config.class);
		writeToFile();
	}

	private static void writeToFile() {
		try {
		FileWriter fw = new FileWriter(file);
		fw.write(new Gson().toJson(conf));
		fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void readFromFile() {
		try {
			FileInputStream fstream = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine;

			while ((strLine = br.readLine()) != null)   {
			  fileJson += strLine;
			}
			
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
