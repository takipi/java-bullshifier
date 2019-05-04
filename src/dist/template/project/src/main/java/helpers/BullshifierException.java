package helpers;

import helpers.Config;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;

public class BullshifierException extends Exception {
	private static final ConcurrentMap<String, AtomicInteger> pathToCounterMap =
		new ConcurrentHashMap<String, AtomicInteger>();
		
	private final Context context;
	
	public BullshifierException(Context context) {
		this.context = context;
		
		String pathString = context.toPathString();
		
		if (!pathToCounterMap.containsKey(pathString)) {
			pathToCounterMap.putIfAbsent(pathString, new AtomicInteger());
		}
		
		AtomicInteger pathCounter = pathToCounterMap.get(pathString);
		pathCounter.addAndGet(1);
	}
	
	public Context getContext()
	{
		return context;
	}
	
	@Override
	public String toString() {
		if (context == null) {
			return "context is null";
		}
		
		String pathString = context.toPathString();
		AtomicInteger pathCounter = pathToCounterMap.get(pathString);
		
		Integer entryPointNum = Config.get().entryPointIndex.get();
		
		if (entryPointNum == null)
		{
			entryPointNum = -1;
		}
		
		String eventId = Integer.toString(entryPointNum) + ":" + context.classId + ":" + context.methodId;
		
		return "unique events: " + pathToCounterMap.size() +
			", events id: " + eventId +
			", hits: " + pathCounter.toString() + " times" +
			", context is: " + context.toString();
	}
}
