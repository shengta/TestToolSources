package antmedia.loadtester.scenarios;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import antmedia.loadtester.ConfigManager;
import antmedia.loadtester.InstantStat;
import antmedia.loadtester.RestManager;

public abstract class AbstractScenario {
	int duration;
	int load;
	ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);
	ScheduledFuture stepInFuture;
	ScheduledFuture stepOutFuture;
	ScheduledFuture statFuture;
	private AtomicBoolean running = new AtomicBoolean(false);
	public File resultDir;
	ArrayList<InstantStat> stats = new ArrayList<>();

	public void start() {
		resultDir = new File("/usr/local/test/results/"+new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date()));
		if(!resultDir.exists()) {
			resultDir.mkdirs();
		}
		readConf();
		int delay = duration/load/4;

		startInternal();

		stepInFuture = scheduledExecutorService.scheduleAtFixedRate(()->stepIn(), 0, delay, TimeUnit.MILLISECONDS);
		stepOutFuture = scheduledExecutorService.scheduleAtFixedRate(()->stepOut(), duration*3/4, delay, TimeUnit.MILLISECONDS);
		statFuture = scheduledExecutorService.scheduleAtFixedRate(()->collectStats(), 1, 1, TimeUnit.SECONDS);
		running.set(true);
	}

	void startInternal() {

	}

	void readConf() {
		duration = Integer.parseInt(ConfigManager.conf.DURATION)*1000;
		load = Integer.parseInt(ConfigManager.conf.LOAD_SIZE);
	}

	public void finishStepIn() {
		stepInFuture.cancel(true);
	}

	public void finishStepOut() {
		statFuture.cancel(true);

		try {
			saveStats();
		} catch (IOException e) {
			e.printStackTrace();
		}

		running.set(false);
		stepOutFuture.cancel(true);

	}

	public void saveStats() throws IOException {
		FileWriter resources = new FileWriter(new File(resultDir, "resources.csv"));
		resources.write(InstantStat.headers+"\n");
		for (InstantStat stat : stats) {
			resources.write(stat.toCsv()+"\n");
		}
		resources.close();
		
		new Thread() {
			@Override
			public void run() {
				ClassLoader classLoader = getClass().getClassLoader();
				try {
					Files.copy(classLoader.getResourceAsStream("report.html"), new File(resultDir, "report.html").toPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
		
		plotStats();
	}

	void plotStats() {
		plotCpuTime();
		plotMemoryTime();
		plotCountsTime();
	}
	
	public void plot(String title, String  xAxisLabel, String  yAxisLabel, XYDataset dataset, String fileName) {
		JFreeChart chart = ChartFactory.createXYLineChart(title, xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true,true,false);
		int width = 640;    /* Width of the image */
		int height = 480;   /* Height of the image */ 
		File lineChart = new File(resultDir,  fileName); 
		
		XYPlot plot = (XYPlot) chart.getXYPlot();
        ValueAxis domainAxis = plot.getDomainAxis(); //x axis
        domainAxis.setMinorTickCount(5);
        
        ((NumberAxis)plot.getRangeAxis()).setAutoRangeIncludesZero(false);
		
		try {
			ChartUtilities.saveChartAsJPEG(lineChart ,chart, width ,height);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void plotCpuTime() {
		XYSeries dataserieCPU = new XYSeries("CPU");
		long t0 = stats.get(0).time;
		for (InstantStat stat : stats) {
			dataserieCPU.add((stat.time-t0), stat.cpu);
		}
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(dataserieCPU);
		
		plot("CPU Usage vs Time","Time", "CPU %", dataset, "cpu_vs_time.jpeg");
	}

	void plotMemoryTime() {
		XYSeries dataserieMemory = new XYSeries("Memory");
		long t0 = stats.get(0).time;
		for (InstantStat stat : stats) {
			dataserieMemory.add((stat.time-t0), stat.memory/1024/1024);
		}
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(dataserieMemory);

		plot("Memory Usage vs Time","Time", "Memory in MB",dataset, "memory_vs_time.jpeg");
	}

	void plotCountsTime() {
		XYSeries dataserieLiveStream = new XYSeries("Live Streams");
		XYSeries dataserieWebRTCStream = new XYSeries("WebRTC Streams");
		XYSeries dataserieHLSViewer = new XYSeries("HLS Viewers");
		XYSeries dataserieWebRTCViewer = new XYSeries("WebRTC Viewers");
		XYSeries dataserieRTMPViewer = new XYSeries("RTMP Viewers");

		
		long t0 = stats.get(0).time;
		for (InstantStat stat : stats) {
			long time = stat.time-t0;
			
			dataserieLiveStream.add(time, stat.totalLiveStreamSize);
			dataserieWebRTCStream.add(time, stat.localWebRTCLiveStreams);
			dataserieHLSViewer.add(time, stat.localHLSViewers);
			dataserieWebRTCViewer.add(time, stat.localWebRTCViewers);
			dataserieRTMPViewer.add(time, stat.localRTMPViewers);
			
		}
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(dataserieLiveStream);
		dataset.addSeries(dataserieWebRTCStream);
		dataset.addSeries(dataserieHLSViewer);
		dataset.addSeries(dataserieWebRTCViewer);
		dataset.addSeries(dataserieRTMPViewer);
		
		plot("Player/Publisher Counts vs Time","Time", "Counts", dataset, "counts_vs_time.jpeg");
	}

	public void collectStats() {
		stats.add(RestManager.getStats(ConfigManager.conf.ORIGIN_SERVER));
	}

	public boolean isRunning() {
		return running.get();
	}

	public abstract void stepIn();
	public abstract void stepOut();
}
