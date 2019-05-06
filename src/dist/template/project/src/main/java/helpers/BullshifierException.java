package helpers;

import helpers.Config;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;

public class BullshifierException extends Exception {
	private static final ConcurrentMap<String, AtomicInteger> hitsCounterMap =
		new ConcurrentHashMap<String, AtomicInteger>();
		
	private final Context context;
	private final int totalUniqueEvents;
	
	public static BullshifierException build(Context context)
	{
		int hits = BullshifierException.getHitCount(context);
		int invs = Context.getInvCount(context);
		double failRate = (double) hits / (double) invs * 100;
		int failRateInt = (int)failRate;
		
		String message = String.format("(Event: %s) (Hits: %d) (Invs: %d) (Fail Rate: %d%%)",
				context.getRequestId(), hits, invs, failRateInt);
		
		return new BullshifierException(context, message);
	}
	
	public BullshifierException(Context context, String message) {
		super(message);
		this.context = context;
		this.totalUniqueEvents = hitsCounterMap.size();
	}
	
	public Context getContext()
	{
		return context;
	}
	
	public static void incHitsCount(Context context)
	{
		String requestId = context.getRequestId();
		
		if (!hitsCounterMap.containsKey(requestId)) {
			hitsCounterMap.putIfAbsent(requestId, new AtomicInteger());
		}
		
		AtomicInteger hitsCounter = hitsCounterMap.get(requestId);
		hitsCounter.addAndGet(1);
	}
	
	public static int getHitCount(Context context)
	{
		AtomicInteger hitsCounter = hitsCounterMap.get(context.getRequestId());
		Integer hits = hitsCounter.get();
		return hits == null ? -1 : hits;
	}
	
	@Override
	public String toString() {
		
		return getMessage();
	}
}
