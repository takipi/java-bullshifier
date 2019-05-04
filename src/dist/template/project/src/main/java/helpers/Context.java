package helpers;

import java.util.List;
import java.util.ArrayList;

public class Context
{
	public int framesDepth = 0;
	public List<int[]> path = new ArrayList<int[]>();
	public int classId = 0;
	public int methodId = 0;
	public int lastSpotPrecentage;

	public Context() { }
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		
		result.append("(fail-rate: ");
		result.append(lastSpotPrecentage);
		result.append("%) (frames: ");
		result.append(framesDepth);
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
		this.classId = classId;
		this.methodId = methodId;
		
		int[] classAndMethodId = new int[2];
		
		classAndMethodId[0] = classId;
		classAndMethodId[1] = methodId;
		
		path.add(classAndMethodId);
	}
	
	public String toPathString() {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < path.size(); i++) {
			int[] current = path.get(i);
			sb.append(">");
			sb.append(current[0]);
			sb.append(":");
			sb.append(current[1]);
		}
		
		return sb.toString();
	}
}
