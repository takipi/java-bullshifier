package generator;

public class LoaderSwitcherGenerator {
	private static write(generatedDir, projectName) {
		def outputFile = new File(generatedDir, "LoaderSwitcher${projectName}.java");
		outputFile.write(template.replace("@PROJECT_NAME@", projectName))
	}
	
	static template = """
package generated;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.io.File;
import java.util.Random;

public class LoaderSwitcher@PROJECT_NAME@ extends ClassLoader {
	private static final int loadersCount;
	private static URLClassLoader[] loaders = new URLClassLoader[5];
	private static final Random rand = new Random();
	
	static {
		String loadersPerProjectStr = System.getenv(\"LOADERS_PER_PROJECT\");
		
		if (loadersPerProjectStr == null) {
			loadersCount = 5;
		} else {
			try {
				loadersCount = Integer.parseInt(loadersPerProjectStr);
			} catch (Exception e) {
				e.printStackTrace();
				loadersCount = 5;
			}
		}
	}
	
	private static URLClassLoader getLoader() throws Exception {
		int loaderIndex = rand.nextInt(loadersCount);
		
		if (loaders[loaderIndex] != null) {
			return loaders[loaderIndex];
		}
		
		synchronized (LoaderSwitcher@PROJECT_NAME@.class) {
			if (loaders[loaderIndex] != null) {
				return loaders[loaderIndex];
			}
			
			String rootProjectDir = System.getenv(\"ROOT_PROJECT_DIR\");
			
			if (rootProjectDir == null) {
				System.out.println(\"ROOT_PROJECT_DIR environment variable is not defined\");
				System.exit(1);
				return null;
			}
			
			String projectName = \"@PROJECT_NAME@\";
			String projectJar = String.format(\"%s/%s/build/libs/%s.jar\", 
				rootProjectDir, projectName, projectName);
			File projectJarFile = new File(projectJar);
			
			if (!projectJarFile.canRead()) {
				System.out.println(\"Missing project jar: \" + projectJarFile);
				System.exit(1);
				return null;
			}
			
			URL url = new URL(\"file://\" + projectJar);
			loaders[loaderIndex] = new URLClassLoader(new URL[] { url });
			
			return loaders[loaderIndex];
		}
	}
	
	public static void call() throws Exception {
		String projectName = \"@PROJECT_NAME@\";
		Class<?> switcherClass = Class.forName(\"generated.Switcher\" + projectName, true, getLoader());
		Method callMethod = switcherClass.getMethod(\"call\");
		callMethod.invoke(null);
	}
}

"""
}
