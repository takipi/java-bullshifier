package generator;

public class MultiSwitcherGenerator
{
	private static write(outputDir, projectNames)
	{
		def switcherClassFile = new File("$outputDir/generated/MultiSwitcher.java")

		switcherClassFile.parentFile.mkdirs()

		def counter = 0

		def cases = projectNames.collect(
		{
			def projectName = it

			return "case ${counter++}: generated.Switcher${projectName}.call(); return;"
		}).join("\n\t\t\t")

		switcherClassFile.write("""package generated;

import helpers.Config;
import helpers.Context;

public class MultiSwitcher
{
	public static void call() throws Exception
	{
		int switcherToCall = Config.get().getRandom().nextInt(${projectNames.size()});

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
