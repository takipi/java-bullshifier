package helpers;

import java.util.Random;
import java.util.Calendar;
import java.util.Date;

public class SimpleConfig extends Config {
	public static Random rand = new Random();

	public static Random getRandom() {
		return rand;
	}

	public boolean shouldThrow1000() {
		return false;
	}

	public boolean shouldThrowIllegal(Context context) {
		return context.counter > 10;
	}

	public boolean shouldThrowIO(Context context) {
		return false;
	}

	public boolean shouldWriteLogInfo(Context context) {
		return false;
	}

	public boolean shouldWriteLogWarn(Context context) {
		return false;
	}

	public boolean shouldWriteLogError(Context context) {
		return false;
	}

	public boolean shouldSuicide() {
		return false;
	}

	public boolean shouldRunAway(Context context) {
		return false;
	}

	public boolean shouldThrowSomething(int methodId, int classId) {
		return true;
	}

	public void updateContext(Context context) {
		context.counter++;
	}
}
