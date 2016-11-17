package helpers;

import generated.MultiSwitcher;

public class MultiMain
{
	public static void main(String[] args) {
		long count = Long.MAX_VALUE;
		long waitMillis = 1000;
		
		if (args.length == 2) {
			count = parseInt(args[0], count);
			waitMillis = parseInt(args[1], waitMillis);
		}
		
		for (long i = 0; i < count; i++) {
			try {
				MultiSwitcher.call();
			} catch (Exception e) {
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
