package generator;

public class BridgeGenerator {
	private static generate(classes) {
		def lines = []

		def swtichMethods = randomMethods(classes, Config.bridgeSwitchSize)

		lines += EventGenerator.addEvent()
		lines += ""
		lines += "Config.get().updateContext(context);"
		lines += "int methodToCall = Config.get().getRandom().nextInt(${swtichMethods.size()});"
		lines += ""
		lines += addSwitch(swtichMethods)
		lines += ""
		lines += "if (Boolean.parseBoolean(\"true\")) { return; }"
		lines += ""

		return lines
	}

	private static randomMethods(classes, bridgeSwitchSize) {
		return (0..bridgeSwitchSize).collect({
			def classIndex = Utils.rand.nextInt(classes.size())
			return classes[classIndex].randomMethod()
		})
	}

	private static addSwitch(swtichMethods) {
		def switcher = [
			"switch (methodToCall)",
			"{"
		]

		int counter = 0

		swtichMethods.each({
			switcher += "\tcase (${counter++}): ${it.owner.qualifyName()}.${it.name}(context); return;"
		})
		
		switcher += "}"
	}
}
