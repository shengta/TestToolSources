package antmedia.loadtester;

public class Config {
	public String ORIGIN_SERVER = "localhost";
	private String EDGE_ACCESS_POINT = "localhost";
	public String TEST_FILE = "/usr/local/test/Test.mp4";
	public String DURATION = "60";
	public String LOAD_SIZE = "5";
	public String STREAM_NAME = "deneme";
	public String USER = "antmedia";
	public String PASS = "antmedia";
	
	public String getORIGIN_SERVER() {
		return ORIGIN_SERVER;
	}
	public void setORIGIN_SERVER(String oRIGIN_SERVER) {
		ORIGIN_SERVER = oRIGIN_SERVER;
	}
	public String getTEST_FILE() {
		return TEST_FILE;
	}
	public void setTEST_FILE(String tEST_FILE) {
		TEST_FILE = tEST_FILE;
	}
	public String getDURATION() {
		return DURATION;
	}
	public void setDURATION(String dURATION) {
		DURATION = dURATION;
	}
	public String getLOAD_SIZE() {
		return LOAD_SIZE;
	}
	public void setLOAD_SIZE(String lOAD_SIZE) {
		LOAD_SIZE = lOAD_SIZE;
	}
	public String getSTREAM_NAME() {
		return STREAM_NAME;
	}
	public void setSTREAM_NAME(String sTREAM_NAME) {
		STREAM_NAME = sTREAM_NAME;
	}
	public String getEDGE_ACCESS_POINT() {
		return EDGE_ACCESS_POINT;
	}
	public void setEDGE_ACCESS_POINT(String eDGE_ACCESS_POINT) {
		EDGE_ACCESS_POINT = eDGE_ACCESS_POINT;
	}
	public String getUSER() {
		return USER;
	}
	public void setUSER(String uSER) {
		USER = uSER;
	}
	public String getPASS() {
		return PASS;
	}
	public void setPASS(String pASS) {
		PASS = pASS;
	}
}
