package helpers;

import generated.*;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/test")
public class MyServlet extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		if (hasParameter(request, "cais")) {
			try {
				Class<?> xhClass = Class.forName("com.sparktale.bugtale.agent.a.h.XH");
				xhClass.getMethod("cs").invoke(null);
				xhClass.getMethod("ps").invoke(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
			
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
		
		System.out.println(String.format(
			"Throwing %d exceptions every %dms. starting at %dms from the beginning (%d threads) (%s stacktraces)",
			exceptionsCount, intervalMillis, warmupMillis, 
			singleThread ? 1 : threadCount,
			hideStackTraces ? "hide" : "show"));
		
		long startMillis = System.currentTimeMillis();
		long warmupMillisTotal = 0l;
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		long exceptionsCounter = 0l;
		
		for (int j = 0; j < runCount; j++) {
			List<Future> calls = new ArrayList<Future>();
			
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
						EntrypointSwitcher.randomCallable().call();
					} else {
						calls.add(executor.submit(EntrypointSwitcher.randomCallable()));
					}
				}
				catch (Exception e) {
					if (!hideStackTraces) {
						e.printStackTrace();
					}
				}
				
				exceptionsCounter++;
				
				long intervalStartMillis = System.currentTimeMillis();
				
				do {
					if (!singleThread && !hideStackTraces) {
						List<Future> doneCalls = new ArrayList<Future>();
						
						for (Future call : calls) {
							if (call.isCancelled() || call.isDone()) {
								try {
									call.get();
								} catch (Exception e) {
									if (e.getCause() != null) {
										e.getCause().printStackTrace();
									}
								}
								
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
				} while ((System.currentTimeMillis() - intervalStartMillis) < intervalMillis);
				
				if (((i + 1) % printStatusEvery) == 0) {
					long endMillis = System.currentTimeMillis();
					long diffMillis = (endMillis - startMillis);
					System.out.println("Took: " + (diffMillis - warmupMillisTotal) + " to throw " + exceptionsCounter + " exceptions");
				}
			}
			
			for (Future call : calls) {
				while (!call.isCancelled() && !call.isDone()) {
					try {
						Thread.currentThread().sleep(1);
					} catch (Exception e) { }
				}
				
				if (!hideStackTraces) {
					try {
						call.get();
					} catch (Exception e) {
						if (e.getCause() != null) {
							e.getCause().printStackTrace();
						}
					}
				}
			}
		}
		
		executor.shutdown();
		
		long endMillis = System.currentTimeMillis();
		long diffMillis = (endMillis - startMillis);
		System.out.println("Took: " + (diffMillis - warmupMillisTotal) + " to throw " + exceptionsCount + " exceptions");
		
		try {
			Thread.currentThread().sleep(1000);
		} catch (Exception e) { }
		
		if (hasParameter(request, "pais")) {
			try {
				Class<?> xhClass = Class.forName("com.sparktale.bugtale.agent.a.h.XH");
				xhClass.getMethod("ps").invoke(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static boolean hasParameter(HttpServletRequest request, String parameter) {
		return request.getParameter(parameter) != null;
	}
	
	private static String getParameter(HttpServletRequest request, String parameter) {
		return request.getParameter(parameter);
	}
	
	public static long parseLong(String str, long defaultValue) {
		try {
			return Long.parseLong(str);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static int parseInt(String str, int defaultValue) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			return defaultValue;
		}
	}
}
