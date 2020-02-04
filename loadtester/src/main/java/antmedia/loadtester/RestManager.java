package antmedia.loadtester;


import java.util.Map;

import org.apache.http.HttpHeaders;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class RestManager {
	
	static Gson gson = new Gson();
	
	static CookieStore cookieStore = new BasicCookieStore();
	static HttpContext httpContext = new BasicHttpContext();
	
	static {
		httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
	}

	private static String getRootURL(String ip) {
		return "http://"+ip+":5080/rest";
	}

	public static boolean signUp(String ip) {
		
		JsonObject obj = new JsonObject();
		obj.addProperty("fullName", "antmedia");
		obj.addProperty("email", "antmedia");
		obj.addProperty("password", "antmedia");
		
		boolean result = false;
		
		try {
			
			HttpUriRequest post = RequestBuilder.post().setUri(getRootURL(ip)+"/addInitialUser")
					.setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
					.setEntity(new StringEntity(obj.toString()))
					.build();
			
			CloseableHttpClient client = HttpClients.custom().setRedirectStrategy(new LaxRedirectStrategy()).build();
			CloseableHttpResponse response = client.execute(post, httpContext);
			String content = EntityUtils.toString(response.getEntity());
			
			if (response.getStatusLine().getStatusCode() != 200) {
				System.out.println(response.getStatusLine()+content);
			}
			
			result = (response.getStatusLine().getStatusCode() == 200);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static boolean login(String ip) {
		JsonObject obj = new JsonObject();
		obj.addProperty("email", ConfigManager.conf.USER);
		obj.addProperty("password", ConfigManager.conf.PASS);
		
		boolean result = false;
		
		try {
			
			HttpUriRequest post = RequestBuilder.post().setUri(getRootURL(ip)+"/authenticateUser")
					.setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
					.setEntity(new StringEntity(obj.toString()))
					.build();
			
			CloseableHttpClient client = HttpClients.custom().setRedirectStrategy(new LaxRedirectStrategy()).build();
			CloseableHttpResponse response = client.execute(post, httpContext);

			String content = EntityUtils.toString(response.getEntity());
			
			if (response.getStatusLine().getStatusCode() != 200) {
				System.out.println(response.getStatusLine()+content);
			}
			
			result = (response.getStatusLine().getStatusCode() == 200);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static InstantStat getStats(String ip) {
		String url = getRootURL(ip)+"/getSystemResourcesInfo";
		
		//System.out.println("url:"+url);
		HttpUriRequest get = RequestBuilder.get().setUri(url)
				.setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
				.build();
		
		InstantStat stat = new InstantStat();
		try {
			CloseableHttpClient client = HttpClients.custom().setRedirectStrategy(new LaxRedirectStrategy()).build();
			CloseableHttpResponse response = client.execute(get, httpContext);
			String content = EntityUtils.toString(response.getEntity());
		
			//System.out.println(content);
			
			Map parsed = new Gson().fromJson(content, Map.class);
			
			stat.cpu = ((Double)((Map)parsed.get("cpuUsage")).get("systemCPULoad")).intValue();
			stat.memory = ((Double)((Map)parsed.get("systemMemoryInfo")).get("inUseMemory")).longValue();
			stat.totalLiveStreamSize = ((Double)parsed.get("totalLiveStreamSize")).intValue();
			stat.localWebRTCLiveStreams = ((Double)parsed.get("localWebRTCLiveStreams")).intValue();
			stat.localWebRTCViewers = ((Double)parsed.get("localWebRTCViewers")).intValue();
			stat.localHLSViewers = ((Double)parsed.get("localHLSViewers")).intValue();
			stat.time = System.currentTimeMillis();
			
			System.out.println(stat.toCsv());
			
			if (response.getStatusLine().getStatusCode() != 200) {
				System.out.println(response.getStatusLine()+content);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return stat;
	}

	public static WebRTCClientStat getWebRTCClientStats(String ip, String id) {
		String url = getRootURL(ip)+"/request?_path=/WebRTCAppEE/rest/broadcast/getWebRTCClientStats/"+id;
		
		//System.out.println("url:"+url);
		HttpUriRequest get = RequestBuilder.get().setUri(url)
				.setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
				.build();
		
		WebRTCClientStat stat = new WebRTCClientStat();

		try {
			CloseableHttpClient client = HttpClients.custom().setRedirectStrategy(new LaxRedirectStrategy()).build();
			CloseableHttpResponse response = client.execute(get, httpContext);
			String content = EntityUtils.toString(response.getEntity());
		
			Map[] parsed = new Gson().fromJson(content, Map[].class);
			
			double videoFrameSendPeriodMax = 0;
			double audioFrameSendPeriodMax = 0;
			
			double measuredBitrateTotal = 0;
			double sendBitrateTotal = 0;
			double videoFrameSendPeriodTotal = 0;
			double audioFrameSendPeriodTotal = 0;

			
			for (Map map : parsed) {
				double measuredBitrate = (Double) map.get("measuredBitrate");
				double sendBitrate = (Double) map.get("sendBitrate");
				double videoFrameSendPeriod = (Double) map.get("videoFrameSendPeriod");
				double audioFrameSendPeriod = (Double) map.get("audioFrameSendPeriod");
				
				videoFrameSendPeriodMax = Math.max(videoFrameSendPeriodMax, videoFrameSendPeriod);
				audioFrameSendPeriodMax = Math.max(audioFrameSendPeriodMax, audioFrameSendPeriod);
				
				measuredBitrateTotal += measuredBitrate;
				sendBitrateTotal += sendBitrate;
				videoFrameSendPeriodTotal += videoFrameSendPeriod;
				audioFrameSendPeriodTotal += audioFrameSendPeriod;
			}
			
			if(parsed.length > 0) {
				stat.measuredBitrate = (int) (measuredBitrateTotal/parsed.length);
				stat.sendBitrate = (int) (sendBitrateTotal/parsed.length);
				stat.videoFrameSendPeriod = (int) (videoFrameSendPeriodTotal/parsed.length);
				stat.audioFrameSendPeriod = (int) (audioFrameSendPeriodTotal/parsed.length);
				stat.videoFrameSendPeriodMax = (int) videoFrameSendPeriodMax;
				stat.audioFrameSendPeriodMax = (int) audioFrameSendPeriodMax;
				stat.time = System.currentTimeMillis();
			}
			
			//System.out.println(stat.toCsv());
			
			if (response.getStatusLine().getStatusCode() != 200) {
				System.out.println(response.getStatusLine()+content);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return stat;
	}
}
