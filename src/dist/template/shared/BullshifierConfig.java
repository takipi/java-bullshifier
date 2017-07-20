package shared

public class BullshifierConfig
{
		public long intervalMillis;
		public long warmupMillis;
		public int threadCount ;
		public int printStatusEvery;
		public int runCount;
		public boolean singleThread;
		public boolean hideStackTraces;
		public long exceptionsCount;

		public  static BullshifierConfig fromCommandLineOptions(CommandLine cmd)
		{	
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
			
			boolean singleThread = false;
			
			if (cmd.hasOption("single-thread")) {
				singleThread = true;
			}
			
			boolean hideStackTraces = false;
			
			if (cmd.hasOption("hide-stacktraces")) {
				hideStackTraces = true;
			}
			
			// return bulshifire object
		}
		
		public static void fromServletRequest(HttpServletRequest request)
		{
			
			long exceptionsCount = 1;
			
			if (hasParameter(request, "ec")) {
				exceptionsCount = parseLong(getParameter(request, "ec"), exceptionsCount);
			}
			
			long intervalMillis = 1000;
			
			if (hasParameter(request, "im")) {
				intervalMillis = parseLong(getParameter(request, "im"), intervalMillis);
			}
			
			long warmupMillis = 0;
			
			if (hasParameter(request, "wm")) {
				warmupMillis = parseLong(getParameter(request, "wm"), warmupMillis);
			}
			
			int threadCount = 5;
			
			if (hasParameter(request, "tc")) {
				threadCount = parseInt(getParameter(request, "tc"), threadCount);
			}
			
			int printStatusEvery = Integer.MAX_VALUE;
			
			if (hasParameter(request, "pse")) {
				printStatusEvery = parseInt(getParameter(request, "pse"), printStatusEvery);
			}
			
			int runCount = 1;
			
			if (hasParameter(request, "rc")) {
				runCount = parseInt(getParameter(request, "rc"), runCount);
			}
			
			boolean singleThread = false;
			
			if (hasParameter(request, "st")) {
				singleThread = true;
			}
			
			boolean hideStackTraces = false;
			
			if (hasParameter(request, "hs")) {
				hideStackTraces = true;
			}
			// return bulshifire object
		}		
}

// BulshifierConfig
//     fields:
//         long intervalMillis;
//         long warmupMillis;
//         int threadCount ;
//         int printStatusEvery;
//         int runCount;
//         boolean singleThread;
//         boolean hideStackTraces;
//         long exceptionsCount;
//     static methods:
//         fromCommandLineOptions(CommandLine cmd)
//         fromServletRequest(HttpServletRequest request)