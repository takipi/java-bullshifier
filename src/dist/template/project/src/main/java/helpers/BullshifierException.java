package helpers;

public class BullshifierException extends Exception {
	private final Context context;
	
	public BullshifierException(Context context) {
		this.context = context;
	}
	
	public Context getContext()
	{
		return context;
	}
	
	@Override
	public String toString() {
		return context == null ? "context is null" : "context is: " + context.toString();
	}
}
