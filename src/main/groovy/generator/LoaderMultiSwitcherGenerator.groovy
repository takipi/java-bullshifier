package generator;

public class LoaderMultiSwitcherGenerator {
	private static write(outputDir, projectNames) {
		def switcherClassFile = new File("$outputDir/generated/LoaderMultiSwitcher.java")

		switcherClassFile.parentFile.mkdirs()

		def counter = 0

		def cases = projectNames.collect({
			def projectName = it

			return "case ${counter++}: generated.LoaderSwitcher${projectName}.call(); return;"
		}).join("\n\t\t\t")
		
		def resetLoadersStatements = projectNames.collect({
			def projectName = it
			
			return "generated.LoaderSwitcher${projectName}.resetLoaders();"
		}).join("\n\t\t")

		switcherClassFile.write("""package generated;

import helpers.Config;
import helpers.Context;
import java.util.Random;

public class LoaderMultiSwitcher
{
	private static final Random rand = Config.get.getRandom();
	
	public static void call() throws Exception
	{
		int switcherToCall = rand.nextInt(${projectNames.size()});

		switch (switcherToCall)
		{
			$cases
			default: return;
		}
	}
	
	public static void resetLoaders() {
		$resetLoadersStatements
	}
}
""")
	}
}
