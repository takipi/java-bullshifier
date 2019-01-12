package helpers;

import java.util.List;
import java.util.ArrayList;

public class Context
{
	public int counter = 0;
	public Integer victomFrame = null;
	public List<int[]> path = new ArrayList<int[]>();

	public Context() { }
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		
		result.append("(frames: ");
		result.append(counter);
		result.append(", path: [");
		
		for (int i = 0; i < path.size(); i++) {
			int classId = path.get(i)[0];
			int methodId = path.get(i)[1];
			
			result.append(classId);
			
			if (methodId != 0)
			{
				result.append(":");
				result.append(methodId);
			}
			
			if ((i + 1) < path.size()) {
				result.append(", ");
			}
		}
		
		result.append("])");
		
		return result.toString();
	}
	
	public void addPath(int classId, int methodId)
	{
		int[] classAndMethodId = new int[2];
		
		classAndMethodId[0] = classId;
		classAndMethodId[1] = methodId;
		
		path.add(classAndMethodId);
	}
}
