package helpers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class StatsReporter {
	private static final long REPORT_INTERVAL_MILLIS = 10000l;
	private static final String REPORT_FILE_NAME = "report-stats.csv";

	private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
	
	private static String[] keys = { "reportTime", "OpenFileDescriptorCount", "FreePhysicalMemorySize", 
									"heapSize", "ProcessCpuLoad", "FreeSwapSpaceSize", "TotalPhysicalMemorySize", 
									"TotalSwapSpaceSize", "freeHeap", "ProcessCpuTime", "MaxFileDescriptorCount",
									"SystemCpuLoad", "maxHeap", "numberOfIoCpuTasks", "CommittedVirtualMemorySize" };

	private static AtomicLong ioCpuTaskCounter = new AtomicLong();

	private static long lastTimeReported = 0;
	private static File reportFile = new File(REPORT_FILE_NAME);
	
	public static void reportIoCpuTaskCompleted() {
		ioCpuTaskCounter.incrementAndGet();
	}
	
	public static void generateReport() {
		long now = System.currentTimeMillis();
	
		if (lastTimeReported >= (now - REPORT_INTERVAL_MILLIS)) {
			return;
		}
		
		doGenerateReport();

		lastTimeReported = System.currentTimeMillis();
	}
	
	private static void doGenerateReport() {
		Map<String, Object> statMap = new HashMap<String, Object>();

		statMap.put("reportTime", System.currentTimeMillis());
		statMap.put("numberOfIoCpuTasks", countAndResetTotalIoCpuTasksCompleted());

		addHeapStats(statMap);
		addMoreStats(statMap);

		// String jsonReport = toJson(statMap);
		String csvReport = toCsv(statMap);
		appendToFile(csvReport);
	}

	private static long countAndResetTotalIoCpuTasksCompleted() {
		return ioCpuTaskCounter.getAndSet(0);
	}
	
	private static void addHeapStats(Map<String, Object> map) {
		Runtime runtime = Runtime.getRuntime();
		
		map.put("heapSize", runtime.totalMemory());
		map.put("maxHeap", runtime.maxMemory());
		map.put("freeHeap", runtime.freeMemory());
	}
	
	private static void addMoreStats(Map<String, Object> map) {
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

	private static String toCsv(Map<String, Object> statMap) {
		StringBuilder csv = new StringBuilder("");	
		
		for (String key : keys) {
			Object value = statMap.get(key);
		
			csv.append(value).append(", ");
		}	
	
		csv.append(LINE_SEPARATOR);
		
		return csv.toString();
	}

	private static String formatAsString(Object obj) {
		if (obj == null) {
			return "NULL";
		}

		return "\"" + obj.toString() + "\"";
	}

	private static void appendToFile(String reportText) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		try {
			fw = new FileWriter(reportFile, true);
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
