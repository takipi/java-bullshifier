package helpers;

import generated.*;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;

public class Main
{
	public static void main(String[] args) throws Exception {
		Options options = createCommandLineOptions();
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);
		
		if (cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java helpers.Main", options);
			return;
		}
		
		long exceptionsCount = Long.MAX_VALUE;
		
		if (cmd.hasOption("exceptions-count")) {
			exceptionsCount = parseLong(cmd.getOptionValue("exceptions-count"), exceptionsCount);
		}
		
		long intervalMillis = 1000;
		
		if (cmd.hasOption("interval-millis")) {
			intervalMillis = parseLong(cmd.getOptionValue("interval-millis"), intervalMillis);
		}
		
		long warmupMillis = 0;
		
		if (cmd.hasOption("warmup-millis")) {
			warmupMillis = parseLong(cmd.getOptionValue("warmup-millis"), warmupMillis);
		}
		
		int threadCount = 5;
		
		if (cmd.hasOption("thread-count")) {
			threadCount = parseInt(cmd.getOptionValue("thread-count"), threadCount);
		}
		
		int printStatusEvery = Integer.MAX_VALUE;
		
		if (cmd.hasOption("print-status-every")) {
			printStatusEvery = parseInt(cmd.getOptionValue("print-status-every"), printStatusEvery);
		}
		
		int runCount = 1;
		
		if (cmd.hasOption("run-count")) {
			runCount = parseInt(cmd.getOptionValue("run-count"), runCount);
		}
		
		if (cmd.hasOption("frames-range")) {
			int[] framesCountRange = parseRange(cmd.getOptionValue("frames-range"), (new int[] { 0, 10 }));

			if (framesCountRange != null) {
				System.out.println("Setting frames range " + framesCountRange[0] + ".." + framesCountRange[1]);
				Config.get().setFramesRangeFromCommandLine(framesCountRange);
			}
		}
		
		boolean singleThread = false;
		
		if (cmd.hasOption("single-thread")) {
			singleThread = true;
		}
		
		if (cmd.hasOption("sticky-path")) {
			Config.get().setStickyPathsDir(cmd.getOptionValue("sticky-path"));
		}
		
		boolean hideStackTraces = false;
		
		if (cmd.hasOption("hide-stacktraces")) {
			hideStackTraces = true;
		}
		
		System.out.println(String.format(
			"(Exceptions: %d) (Interval: %dms) (Warmup: %dms) (Threads: %d) (%s stacktraces) (sticky path: %s)",
			exceptionsCount, intervalMillis, warmupMillis, 
			singleThread ? 1 : threadCount,
			hideStackTraces ? "hide" : "show",
			Config.get().getStickyPathsDir()));
		
		long startMillis = System.currentTimeMillis();
		long warmupMillisTotal = 0l;
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		long tasksCompleted = 0l;
		
		for (int j = 0; j < runCount; j++) {
			List<Future> calls = new ArrayList<Future>();
			warmupMillisTotal = 0l;
			startMillis = System.currentTimeMillis();
			tasksCompleted = 0l;
			
			try {
				long warmupStartMillis = System.currentTimeMillis();
				
				if (warmupMillis > 0) {
					Thread.sleep(warmupMillis);
				}
				
				warmupMillisTotal += (System.currentTimeMillis() - warmupStartMillis);
			} catch (Exception e) { }
			
			if (runCount > 1) {
				System.out.println("Starting iteration number: " + (j + 1));
			}

			for (long i = 0; i < exceptionsCount; i++) {
				try {
					if (singleThread) {
						tasksCompleted++;
						EntrypointSwitcher.randomCallable().call();
						StatsReporter.get().incTasksCompleted();
					} else {
						calls.add(executor.submit(EntrypointSwitcher.randomCallable()));
					}
				}
				catch (Exception e) {
					handleException(e, hideStackTraces, false);
				}
				
				long intervalStartMillis = System.currentTimeMillis();
				
				do {
					if (!singleThread) {
						List<Future> doneCalls = new ArrayList<Future>();
						
						for (Future call : calls) {
							if (call.isCancelled() || call.isDone()) {
								try {
									call.get();
								} catch (Exception e) {
									handleException(e, hideStackTraces, true);
								}
								
								tasksCompleted++;
								StatsReporter.get().incTasksCompleted();
								doneCalls.add(call);
							}
						}
						
						for (Future doneCall : doneCalls) {
							calls.remove(doneCall);
						}
					}
					
					if (intervalMillis > 0l) {
						try {
							Thread.currentThread().sleep(100);
						} catch (Exception e) { }
					}
					
					StatsReporter.get().generateReport();
				} while ((System.currentTimeMillis() - intervalStartMillis) < intervalMillis);
				
				if (tasksCompleted > 0 && (tasksCompleted % printStatusEvery) == 0) {
					long endMillis = System.currentTimeMillis();
					long diffMillis = (endMillis - startMillis);
					System.out.println("Took: " + (diffMillis - warmupMillisTotal) + " to throw " + tasksCompleted + " exceptions");
				}
			}
			
			for (Future call : calls) {
				while (!call.isCancelled() && !call.isDone()) {
					try {
						Thread.currentThread().sleep(1);
					} catch (Exception e) { }
				}
				
				try {
					tasksCompleted++;
					call.get();
				} catch (Exception e) {
					handleException(e, hideStackTraces, true);
				}
				
				if (tasksCompleted > 0 && (tasksCompleted % printStatusEvery) == 0) {
					long endMillis = System.currentTimeMillis();
					long diffMillis = (endMillis - startMillis);
					System.out.println("Took: " + (diffMillis - warmupMillisTotal) + " to throw " + tasksCompleted + " exceptions");
				}
			}
		}
		
		if (tasksCompleted > 0 && (tasksCompleted % printStatusEvery) == 0) {
			long endMillis = System.currentTimeMillis();
			long diffMillis = (endMillis - startMillis);
			System.out.println("Took: " + (diffMillis - warmupMillisTotal) + " to throw " + tasksCompleted + " exceptions");
		}
		
		executor.shutdown();
		
		long endMillis = System.currentTimeMillis();
		long diffMillis = (endMillis - startMillis);
		System.out.println("Took: " + (diffMillis - warmupMillisTotal) + " to throw " + tasksCompleted + " exceptions");
	}
	
	private static void handleException(Exception exception, boolean hideStackTraces, boolean nested) {
		if (nested) {
			if (exception.getCause() instanceof Exception) {
				exception = (Exception) exception.getCause();
			}
		}
		
		if (!(exception instanceof BullshifierException)) {
			exception.printStackTrace();
			return;
		}
		
		BullshifierException bex = (BullshifierException) exception;
		
		if (!hideStackTraces) {
			exception.printStackTrace();
			return;
		}
		
		if (bex != null) {
			System.out.println(bex.toString());
		}
	}

	public static long parseLong(String str, long defaultValue) {
		try {
			return Long.parseLong(str);
		} catch (Exception e) {
			System.out.println("Error parsing long " + str);
			return defaultValue;
		}
	}

	public static int parseInt(String str, int defaultValue) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			System.out.println("Error parsing int " + str);
			return defaultValue;
		}
	}
	
	public static int[] parseRange(String str, int[] defaultValue)
	{
		if (str == null) {
			System.out.println("Parse range error: null");
			return defaultValue;
		}
		
		int from = defaultValue[0];
		int to = defaultValue[1];
		
		if (str.indexOf("..") == -1) {
			from = parseInt(str, defaultValue[0]);
			to = from;
		} else {
			String[] parts = str.split("\\.\\.");
			
			if (parts == null || parts.length != 2) {
				System.out.println("Parse range error: invalid format: " + str);
				return defaultValue;
			}
			
			from = parseInt(parts[0], from);
			to = parseInt(parts[1], to);
		}
		
		int[] result = new int[2];
		result[0] = from;
		result[1] = to;
		return result;
	}
	
	private static Options createCommandLineOptions() {
		Options options = new Options();
		
		options.addOption("h", "help", false, "Print this help");
		options.addOption("st", "single-thread", false, "Run everything directly from the main thread (default to false)");
		options.addOption("hs", "hide-stacktraces", false, "Determine whether to print the stack traces of the exceptions (default to false)");
		options.addOption("pse", "print-status-every", true, "Print to screen every n events (default to Integer.MAX_VALUE)");
		options.addOption("tc", "thread-count", true, "The number of threads (default to 5)");
		options.addOption("ec", "exceptions-count", true, "The number of exceptions to throw (default to 1000)");
		options.addOption("wm", "warmup-millis", true, "Time to wait before starting to throw exceptions (in millis) (default to 0)");
		options.addOption("im", "interval-millis", true, "Time between exceptions (in millis) (default to 1000)");
		options.addOption("rc", "run-count", true, "The number of times to run all (default to 1)");
		options.addOption("fc", "frames-range", true, "Choose a random number between a range in '(X..)?Y' format. (default is 1..1)");
		options.addOption("sp", "sticky-path", true, "A path to store constant paths in the code");
		
		return options;
	}
}
