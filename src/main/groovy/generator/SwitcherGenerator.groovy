package generator;

public class SwitcherGenerator {
	private static write(classes, outputDir, projectName) {
		def switcherClassName = "Switcher$projectName"

		def counter = 0

		def switchStatements = classes.collect({
			def clazz = it

			return clazz.methods.collect({
				return ["case ${counter++}: ${clazz.qualifyName()}.$it.name(new Context()); return;"]
			})
		}).flatten()

		def maxCasesPerClass = 1000
		def subClassesCount = (switchStatements.size() / maxCasesPerClass)
		def mainMethodIfs = []

		(0..(subClassesCount - 1)).each {
			def start = it * maxCasesPerClass
			def end = Math.min(start + maxCasesPerClass, switchStatements.size() - 1)
			def subClassesCases = switchStatements[start..end]

			def CasesStr = subClassesCases.join("\n\t\t\t")

			def switcherSubClassFile = new File("$outputDir/generated/${switcherClassName}${it}.java")

			switcherSubClassFile.write("""package generated;
import helpers.Config;
import helpers.Context;
public class $switcherClassName$it
{
		public static void call(int number) throws Exception
		{
			switch (number)
			{
				$CasesStr
				default: return;
			}
		}
}
"""
)

			mainMethodIfs += """
		if (number >= $start && number <= $end)
		{
			$switcherClassName${it}.call(number);
			return;
		}
"""
		}

		def switcherClassFile = new File("$outputDir/generated/${switcherClassName}.java")

		switcherClassFile.write("""package generated;
import helpers.Config;
import helpers.Context;
public class $switcherClassName
{
	public static void call() throws Exception
	{
		call(Config.get().getRandom().nextInt(${switchStatements.size()}));
	}
	public static void call(int number) throws Exception
	{
		${mainMethodIfs.join("\n\t\t\t")}
	}
}
""")

		return switcherClassName
	}
}
