package helpers;

import java.util.Random;
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

			instance = new @CONFIG_CLASS@();

			return instance;
		}
	}

	public abstract boolean shouldThrow1000();
	public abstract boolean shouldThrowIllegal(Context context);
	public abstract boolean shouldThrowIO(Context context);
	public abstract boolean shouldWriteLogInfo(Context context);
	public abstract boolean shouldWriteLogWarn(Context context);
	public abstract boolean shouldWriteLogError(Context context);
	public abstract boolean shouldSuicide();
	public abstract boolean shouldRunAway(Context context);
	public abstract boolean shouldThrowSomething(int methodId, int classId);
	public abstract void updateContext(Context context);
}
