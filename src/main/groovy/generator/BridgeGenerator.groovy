package generator;

public class BridgeGenerator {
	public static generate(methodName, classes) {
		def lines = []

		def methodToCallVariableName = getMethodToCallVariableName(methodName)
		def swtichMethods = randomMethods(classes, Config.bridgeSwitchSize)
		
		lines += ""
		lines += "try"
		lines += "{"
		lines += "	int $methodToCallVariableName = Config.get().getStickyPath(classId, methodId, ${swtichMethods.size()});"
		lines += ""
		lines += addSwitch(methodName, swtichMethods)
		lines += "}"
		lines += "catch (Exception e)"
		lines += "{"
		lines += "	if (Config.get().shouldWriteLogError(context))"
		lines += "	{"
		lines += "		logger.error(\"An error from ({}/{}/{})\", e);"
		lines += "	}"
		lines += "}"
		lines += ""
		lines += "if (Boolean.parseBoolean(\"true\")) { return; }"

		return lines
	}
	
	public static getMethodToCallVariableName(methodName) {
		return "${methodName}MethodToCall"
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
			"\tswitch ($methodToCallVariableName)",
			"\t{"
		]

		int counter = 0

		swtichMethods.each({
			switcher += "\t\tcase (${counter++}): ${it.owner.qualifyName()}.${it.name}(context); return;"
		})
		
		switcher += "\t}"
	}
}
