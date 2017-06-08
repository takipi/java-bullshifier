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

public class LoaderSwitcher@PROJECT_NAME@ extends ClassLoader {
	public static void call() throws Exception {
		String rootProjectDir = System.getenv(\"ROOT_PROJECT_DIR\");
		
		if (rootProjectDir == null) {
			System.out.println(\"ROOT_PROJECT_DIR environment variable is not defined\");
			System.exit(1);
			return;
		}
		
		String projectName = \"@PROJECT_NAME@\";
		String projectJar = String.format(\"%s/%s/build/libs/%s.jar\", 
			rootProjectDir, projectName, projectName);
		File projectJarFile = new File(projectJar);
		
		if (!projectJarFile.canRead()) {
			System.out.println(\"Missing project jar: \" + projectJarFile);
			System.exit(1);
			return;
		}
		
		URL url = new URL(\"file://\" + projectJar);
		URLClassLoader loader = new URLClassLoader(new URL[] { url });
		
		Class<?> switcherClass = Class.forName(\"generated.Switcher\" + projectName, true, loader);
		Method callMethod = switcherClass.getMethod(\"call\");
		callMethod.invoke(null);
	}
}

"""
}