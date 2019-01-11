package helpers;

import java.util.Random;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public abstract class Config
{
	public static Random rand = new Random();

	public static Random getRandom() {
		return rand;
	}

	private volatile static Config instance;

	public static Config get() {
		if (instance != null) {
			return instance;
		}

		synchronized (Config.class) {
			if (instance != null) {
				return instance;
			}

			instance = new SimpleConfig();

			return instance;
		}
	}
	
	public static void setFramesRangeFromCommandLine(int[] framesCountRange) {
		if ((framesCountRange == null) ||
			(framesCountRange.length != 2)) {
			System.out.println("Invalid frames range " + Arrays.toString(framesCountRange));
			return;
		}
		
		int from = framesCountRange[0];
		int to = framesCountRange[1];
		
		Config.get().setFramesRange(from, to);
	}
	
	public int framesRangeFrom = -1;
	public int framesRangeTo = -1;
	
	public void setFramesRange(int from, int to) {
		if (from < 0) {
			from = 0;
		}
		
		if (to < from) {
			to = from;
		}

		this.framesRangeFrom = from;
		this.framesRangeTo = to;
	}
	
	public boolean shouldThrowIllegal(Context context) {
		if (framesRangeFrom < 0 || framesRangeTo < 0) {
			return internalShouldThrowIllegal(context);
		}
		
		if (context.victomFrame == null) {
			context.victomFrame = framesRangeFrom + rand.nextInt((framesRangeTo - framesRangeFrom) + 1);
		}
		
		return context.victomFrame <= context.counter;
	}

	public abstract boolean shouldThrow1000();
	public abstract boolean internalShouldThrowIllegal(Context context);
	public abstract boolean shouldThrowIO(Context context);
	public abstract boolean shouldWriteLogInfo(Context context);
	public abstract boolean shouldWriteLogWarn(Context context);
	public abstract boolean shouldWriteLogError(Context context);
	public abstract boolean shouldSuicide();
	public abstract boolean shouldRunAway(Context context);
	public abstract boolean shouldThrowSomething(int methodId, int classId);
	public abstract boolean shouldDoIoCpuIntensiveLogic(Context context);
	public abstract void updateContext(Context context);
}
