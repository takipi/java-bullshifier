package helpers;


import java.util.Random;
import java.util.Calendar;
import java.util.Date;

public class ComplexConfig extends Config {
	public static int eventCounter = 0;

	public void updateEventCounter(boolean shouldMakeEvent) {
		if (shouldMakeEvent == true) {
			eventCounter++;
		}
	}

	@Override
	public boolean shouldThrow1000() {
		Calendar cal = Calendar.getInstance();

		if (((cal.get(Calendar.HOUR) == 13) || (cal.get(Calendar.HOUR) == 1)) && ((cal.get(Calendar.MINUTE) > 9 && cal.get(Calendar.MINUTE) < 21) || cal.get(Calendar.MINUTE) > 39 && cal.get(Calendar.MINUTE) < 51)) {
			updateEventCounter(true);
			return true;
		}
		
		return false;
	}

	@Override
	public boolean shouldThrowIllegal(Context context) {
		boolean shouldThrowIllegal = Config.getRandom().nextBoolean();
		updateEventCounter(shouldThrowIllegal);
		
		return shouldThrowIllegal;
	}

	@Override
	public boolean shouldThrowIO(Context context) {
		boolean shouldThrowIO = Config.getRandom().nextBoolean();
		updateEventCounter(shouldThrowIO);
		
		return shouldThrowIO;
	}

	@Override
	public boolean shouldWriteLogInfo(Context context) {
		boolean shouldWriteLogInfo = (context.counter % 150 == 0);
		return shouldWriteLogInfo;
	}

	@Override
	public boolean shouldWriteLogWarn(Context context) {
		boolean shouldWriteLogWarn = Config.getRandom().nextBoolean();
		updateEventCounter(shouldWriteLogWarn);
		
		return shouldWriteLogWarn;
	}

	@Override
	public boolean shouldWriteLogError(Context context) {
		boolean shouldWriteLogError = Config.getRandom().nextBoolean();
		updateEventCounter(shouldWriteLogError);
		
		return shouldWriteLogError;
	}

	@Override
	public boolean shouldSuicide() {
		return false;
	}

	@Override
	public boolean shouldRunAway(Context context) {
		return (context.counter > 25);
	}

	@Override
	public boolean shouldThrowSomething(int methodId, int classId) {
		if (methodId == 3) {
			return (classId % 50 == 0);
		}
		
		return false;
	}

	@Override
	public void updateContext(Context context) {
		context.counter++;
	}
}
