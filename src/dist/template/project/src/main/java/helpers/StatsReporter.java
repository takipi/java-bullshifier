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
	private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
	
	private static AtomicLong ioCpuTaskCounter = new AtomicLong();

	private static long lastTimeReported = 0;
	private static long reportIntervalMillis = 1000;
	private static File reportFile = new File("report-stats.txt");
	
	public static void reportIoCpuTaskCompleted() {
		ioCpuTaskCounter.incrementAndGet();
	}
	
	public static void generateReport() {
		long now = System.currentTimeMillis();
	
		if (lastTimeReported >= (now - reportIntervalMillis)) {
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
		
		String jsonReport = toJson(statMap);
		
		appendToFile(jsonReport);
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
	
	private static String toJson(Map<String, Object> statMap) {
		StringBuilder jsonString = new StringBuilder("{");
		
		boolean first = true;
		
		
		for (String key : statMap.keySet()) {
			Object value = statMap.get(key);
		
			if (!first) {
				jsonString.append(", ");
				jsonString.append(LINE_SEPARATOR);
			}

			first = false;
			
			jsonString.append(formatAsString(key));
			jsonString.append(" : ");
			
			if (value instanceof Number) {
				jsonString.append(value);
			}
			else  {
				jsonString.append(formatAsString(value));
			}
		}

		jsonString.append("}");
		jsonString.append(LINE_SEPARATOR);
		
		return jsonString.toString();
	}

	private static String formatAsString(Object obj) {
		if (obj == null) {
			return "NULL";
		}
		
		return "\"" + obj.toString() + "\"";
	}
	
	private static void appendToFile(String jsonReport) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		try {
			fw = new FileWriter(reportFile, true);
			bw = new BufferedWriter(fw);
			bw.write(jsonReport);
			bw.write(LINE_SEPARATOR);
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
