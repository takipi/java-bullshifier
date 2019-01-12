package generator;

public class BridgeGenerator {
	public static generate(methodName, classes) {
		def lines = []

		def swtichMethods = randomMethods(classes, Config.bridgeSwitchSize)

		lines += ""
		lines += addMethodToCallCalculation(methodName, swtichMethods.size())
		lines += ""
		lines += addSwitch(methodName, swtichMethods)
		lines += ""
		lines += "if (Boolean.parseBoolean(\"true\")) { return; }"
		lines += ""

		return lines
	}
	
	public static getMethodToCallVariableName(methodName) {
		return "${methodName}MethodToCall"
	}
	
	private static addMethodToCallCalculation(methodName, methodsCount)
	{
		def methodToCallVariableName = getMethodToCallVariableName(methodName)
		
		return [
			"if (Config.get().isStickyPath())",
			"{",
			"	if ($methodToCallVariableName == -1)",
			"	{",
			"		$methodToCallVariableName = Config.get().getStickyPath(classId, methodId, $methodsCount);",
			"	}",
			"}",
			"else",
			"{",
			"	$methodToCallVariableName = Config.get().getRandom().nextInt($methodsCount);",
			"}"
		]
	}
	
	private static randomMethods(classes, bridgeSwitchSize) {
		return (1..bridgeSwitchSize).collect({
			def classIndex = Utils.rand.nextInt(classes.size())
			return classes[classIndex].randomMethod()
		})
	}

	private static addSwitch(methodName, swtichMethods) {
		def methodToCallVariableName = getMethodToCallVariableName(methodName)
		
		def switcher = [
			"switch ($methodToCallVariableName)",
			"{"
		]

		int counter = 0

		swtichMethods.each({
			switcher += "\tcase (${counter++}): ${it.owner.qualifyName()}.${it.name}(context); return;"
		})
		
		switcher += "}"
	}
}
