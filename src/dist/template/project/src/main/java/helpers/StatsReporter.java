package helpers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class StatsReporter {
	private static final long REPORT_INTERVAL_MILLIS = 10000l;
	private static final String REPORT_FILE_NAME = "report-stats.csv";

	private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

	// metrics not in use: "OpenFileDescriptorCount", "freeHeap", "FreeSwapSpaceSize", "maxHeap", 
	// "MaxFileDescriptorCount", "TotalSwapSpaceSize", "heapSize", "ProcessCpuTime", ,
	// "SystemCpuLoad", "CommittedVirtualMemorySize"
	//
	private static final String[] keys = { "reportTime", "tasksCount", "latency", "ProcessCpuLoad", "heapSize"};

	private final File reportFile;

	private final Lock latencyGetLock;
	private final Lock latencyUpdateLock;

	private final AtomicLong latencyTotal;
	private final AtomicInteger latencyReportsCounter;

	private int taskCounter;
	private long lastTimeReported;

	private volatile static StatsReporter instance = null;

	private StatsReporter()
	{
		this.reportFile = new File(REPORT_FILE_NAME);

		ReentrantReadWriteLock multiReportLock = new ReentrantReadWriteLock();
		this.latencyGetLock = multiReportLock.writeLock();
		this.latencyUpdateLock = multiReportLock.readLock();

		this.latencyTotal = new AtomicLong();
		this.latencyReportsCounter = new AtomicInteger();

		this.taskCounter = 0;
		this.lastTimeReported = 0;

		writeHeaderToFile();
	}
	
	public static StatsReporter get()
	{
		if (instance == null)
		{
			synchronized (StatsReporter.class)
			{
				if (instance == null)
				{
					instance = new StatsReporter();
				}
			}
		}

		return instance;
	}

	public void incTasksCompleted() {
		taskCounter++;
	}

	public void reportLatency(long latencyMillis) {
		latencyUpdateLock.lock();

		try {
			latencyTotal.addAndGet(latencyMillis);
			latencyReportsCounter.incrementAndGet();
		}
		finally {
			latencyUpdateLock.unlock();
		}
	}

	public void generateReport() {
		long now = System.currentTimeMillis();
	
		if (lastTimeReported >= (now - REPORT_INTERVAL_MILLIS)) {
			return;
		}

		doGenerateReport();

		lastTimeReported = System.currentTimeMillis();
	}

	private void doGenerateReport() {
		Map<String, Object> statMap = new HashMap<String, Object>();

		statMap.put("reportTime", System.currentTimeMillis());
		statMap.put("tasksCount", taskCounter);
		taskCounter = 0;

		statMap.put("latency", doLatency());

		addHeapStats(statMap);
		
		// doesn't work well with java9
		// addMoreStats(statMap);

		// String jsonReport = toJson(statMap);
		String csvReport = toCsv(statMap);
		appendToFile(csvReport);
	}

	private long doLatency()
	{
		latencyGetLock.lock();

		try {
			long totalLatency = latencyTotal.getAndSet(0);
			int count = latencyReportsCounter.getAndSet(0);

			if (count == 0) {
				return 0;
			}

			return totalLatency / count;
		}
		finally {
			latencyGetLock.unlock();
		}
	}

	private static void addHeapStats(Map<String, Object> map) {
		Runtime runtime = Runtime.getRuntime();
		
		map.put("heapSize", runtime.totalMemory());
		map.put("maxHeap", runtime.maxMemory());
		map.put("freeHeap", runtime.freeMemory());
	}

	private void addMoreStats(Map<String, Object> map) {
		OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();

		String getPrefix = "get";
		
		for (Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
			method.setAccessible(true);
			if (method.getName().startsWith(getPrefix) && Modifier.isPublic(method.getModifiers())) {
				Object value;
				try {
					value = method.invoke(operatingSystemMXBean);
				}
				catch (Exception e) {
					value = e;
				}
				
				String key = method.getName().substring(getPrefix.length());
				map.put(key, value);
			}
		}
	}

	private String toCsv(Map<String, Object> statMap) {
		StringBuilder csv = new StringBuilder("");	
		
		for (String key : keys) {
			Object value = statMap.get(key);
		
			csv.append(value).append(", ");
		}	
	
		csv.append(LINE_SEPARATOR);
		
		return csv.toString();
	}

	private void writeHeaderToFile()
	{
		String header = Arrays.toString(keys);
		header = header.substring(1, header.length() -1) + "," + LINE_SEPARATOR;

		appendToFile(header, false);
	}

	private void appendToFile(String reportText) {
		appendToFile(reportText, true);
	}

	private void appendToFile(String reportText, boolean append) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		try {
			fw = new FileWriter(reportFile, append);
			bw = new BufferedWriter(fw);
			bw.write(reportText);
			bw.flush();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (bw != null) {
				try {
					fw.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			if (fw != null) {
				try {
					fw.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
