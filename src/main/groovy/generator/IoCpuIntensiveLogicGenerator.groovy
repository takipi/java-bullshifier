package generator;

import java.util.concurrent.Callable
import java.util.concurrent.Executors

public class IoCpuIntensiveLogicGenerator {
	static generate() {
		"""

		int currentIteration = context.counter % $Config.ioCpuIntensiveFileLimit;
		int matrixSize = $Config.ioCpuIntensiveMatrixSize;

		if (matrixSize <= 0) {
			return;
		}
		
		// random matrix
		int[][] matrix = new int[matrixSize][matrixSize];
		java.util.Random rand = new java.util.Random();
		for (int i = 0; i < matrixSize ; i++) {
			for (int j = 0; j < matrixSize ; j++) {
				matrix[i][j] = rand.nextInt();
			}
		}
	
		// files
		String directoryName = "output";
		java.io.File directory = new java.io.File(directoryName);
		directory.mkdirs();
		String filePrefix = "matrix_" + java.lang.Thread.currentThread().currentThread().getId() + "_";
		String pathPrefix = "output/" + filePrefix;
		
 		// find smallest file number
		int smallestFileIndex = -1;
		java.io.File[] files = directory.listFiles();
		for (java.io.File file : files) {
			if (file.getName().startsWith(filePrefix))
			{
				String fileSuffixNumber = file.getName().substring(filePrefix.length());
				int number = Integer.parseInt(fileSuffixNumber);
				
				if (smallestFileIndex < number) {
					smallestFileIndex = number;
				}
			}
		}

		// init default old matrix
		int[][] oldMatrix = new int[matrixSize][matrixSize];
		for (int i = 0; i < matrixSize; i++) {
			for (int j = 0 ; j < matrixSize ; j++) {
				oldMatrix[i][j] = 0;
				
				if (i == j) {
					oldMatrix[i][j] = 1;
				}
			}
		}
		
		// read old matrix
		java.io.File oldFile = new java.io.File(pathPrefix + (smallestFileIndex));

		if ((smallestFileIndex > 0) && (oldFile.exists())) {
			java.io.BufferedReader br = null;
			
			try {
				br = new java.io.BufferedReader(new java.io.FileReader(oldFile));
				String line = br.readLine();
				int lineNumber = 0 ;
				
				while(line != null) {
					String[] splitted = line.split(" ");
					
					if (splitted.length != matrixSize) {
						break;
					}
					
					for (int i = 0 ; i < matrixSize ; i++) {
						oldMatrix[i][lineNumber] = java.lang.Integer.parseInt(splitted[i]);
					}
					
					lineNumber++;
					if (lineNumber >= matrixSize) {
						break;
					}

					line = br.readLine();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally	{
				if (br != null)
				{
					try
					{
						br.close();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		
		// multiply matrices
		int[][] resultMatrix = new int[matrixSize][matrixSize];
		
		for (int i = 0 ; i < matrixSize ; i++) {
			for (int j = 0 ; j < matrixSize ; j++) {
				int res = 0;
				for (int k = 0 ; k < matrixSize ; k++) {
					res += oldMatrix[i][k] * matrix[k][j];
				}
				
				resultMatrix[i][j] = res;
			}
		}
		
		// write new matrix
		java.io.File file = new java.io.File(pathPrefix + (smallestFileIndex + 1));
		java.io.BufferedWriter bw = null;
		
		try {
			bw = new java.io.BufferedWriter(new java.io.FileWriter(file));
			for (int i = 0; i < matrixSize; i++) {
				for (int j = 0; j < matrixSize; j++) {
					bw.write(java.lang.Integer.toString(resultMatrix[i][j]));
					bw.write(" ");
				}
				bw.newLine();
				bw.flush();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (bw != null) {
				try {
					bw.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		"""
	}
}