package helpers;

import java.io.File;
import java.util.Random;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.BufferedReader;

public class EventsSpot {
	public static Random rand = new Random();
	
	public static boolean shouldFireEvent(File spotDataDir, Context context) {
		int currentLuck = Math.abs(rand.nextInt() % 100);
		Integer spotPrecentage = loadSpotData(spotDataDir, 
				context.entryPointId, context.classId, context.methodId, context.instructionIndex);
		
		if (spotPrecentage == null)
		{
			spotPrecentage = generateSpotData();
			saveSpotData(spotDataDir, context.entryPointId, context.classId, context.methodId, context.instructionIndex, spotPrecentage);
		}
		
		context.lastSpotPrecentage = spotPrecentage;
		
		return currentLuck < spotPrecentage;
	}
	
	private static int generateSpotData()
	{
		int spotPurpose = Math.abs(rand.nextInt() % 100);
		
		if (spotPurpose < 50)
		{
			return 0;
		}
		
		if (spotPurpose > 95)
		{
			return 100;
		}
		
		int spotStrengh = 10 - (spotPurpose % 10);
		int spotLuck = Math.abs(rand.nextInt() % 100);
		
		return Math.abs(spotLuck % (spotPurpose / spotStrengh));
	}
	
	private static Integer loadSpotData(File spotDataDir, int entryPointId, int classId, int methodId, int instructionIndex)
	{
		File spotDataFile = getSpotDataFile(spotDataDir, entryPointId, classId, methodId, instructionIndex);
		
		if (spotDataFile == null) {
			return null;
		}
		
		if (!spotDataFile.canRead()) {
			return null;
		}
		
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		
		try {
			fileReader = new FileReader(spotDataFile);
			bufferedReader = new BufferedReader(fileReader);
			
			String spotPrecentageLine = bufferedReader.readLine();
			
			if (spotPrecentageLine == null) {
				return null;
			}
			
			return Integer.parseInt(spotPrecentageLine);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
				if (fileReader != null) {
					fileReader.close();
				}
			} catch (Exception e) { }
		}
	}
	
	private static boolean saveSpotData(File spotDataDir, int entryPointId, int classId, int methodId, int instructionIndex, int spotPrecentage)
	{
		File spotDataFile = getSpotDataFile(spotDataDir, entryPointId, classId, methodId, instructionIndex);
		
		if (spotDataFile == null) {
			return false;
		}
		
		FileOutputStream out = null;
		
		try {
			out = new FileOutputStream(spotDataFile);
			out.write(Integer.toString(spotPrecentage).getBytes());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) { }
		}
	}
	
	public static File getSpotDataFile(File spotDataDir, int entryPointId, int classId, int methodId, int instructionIndex) {
		spotDataDir.mkdirs();
		
		if (!spotDataDir.isDirectory()) {
			System.out.println("Spot dir is not a directory " + spotDataDir);
			return null;
		}
		
		return new File(spotDataDir, entryPointId + ":" + classId + ":" + methodId + ":" + instructionIndex);
	}
}
