package helpers;

import generated.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main
{
	public static void main(String[] args) throws Exception {
		long count = Long.MAX_VALUE;
		long waitMillis = 1000;
		long startSleep = 0;
		
		if (args.length > 0) {
			count = parseInt(args[0], count);
			
			if (args.length > 1) {
				waitMillis = parseInt(args[1], waitMillis);
				
				if (args.length > 2) {
					startSleep = parseInt(args[2], startSleep);
				}
			}
		}
		
		System.out.println(String.format("Throwing %d exceptions every %dms. starting at %dms from the beginning",
			count, waitMillis, startSleep));
		
		try
		{
			if (startSleep > 0)
			{
				Thread.sleep(startSleep);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		ExecutorService executor = Executors.newFixedThreadPool(5);

		for (long i = 0; i < count; i++) {
			try {
				executor.submit(EntrypointSwitcher.randomCallable());
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			try {
				Thread.currentThread().sleep(waitMillis);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static long parseInt(String str, long defaultValue) {
		try {
			return Long.parseLong(str);
		} catch (Exception e) {
			return defaultValue;
		}
	}
}
