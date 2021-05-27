package generator;

public class MultiSwitcherGenerator {
	private static write(outputDir, projectNames) {
		def switcherClassFile = new File("$outputDir/generated/MultiSwitcher.java")

		switcherClassFile.parentFile.mkdirs()

		def counter = 0

		def cases = projectNames.collect({
			def projectName = it

			return "case ${counter++}: generated.Switcher${projectName}.call(); return;"
		}).join("\n\t\t\t")

		switcherClassFile.write("""package generated;

import helpers.Config;
import helpers.Context;
import java.util.Random;

public class MultiSwitcher
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
}
""")
	}
}
