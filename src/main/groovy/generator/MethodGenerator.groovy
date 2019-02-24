package generator;

public class MethodGenerator {
	private def owner
	private def name
	private def code = new StringBuilder()

	private def MethodGenerator(owner) {
		this.owner = owner
		this.name = Utils.generateName("", "", (Utils.rand.nextInt(10) + 4), false, false)
	}

	public def addClassAndMethodId(classId, methodId) {
		code.append("\t\tint methodId = $methodId;\n")
		code.append("\t\tConfig.get().updateContext(context, $classId, $methodId);\n\n")
	}

	private def addLocals() {
		def logic = LocalsGenerator.generateLocals()
		def lines = logic.collect({ "\t\t$it" })

		code.append(lines.join("\n"))
	}

	private def addLogic() {
		def logic = LogicGenerator.generateBlocks([:])
		def lines = logic.print("\t\t")

		code.append(lines.join("\n"))
	}

	private def addBridge(classes) {
		def lines = BridgeGenerator.generate(name, classes).collect({ "\t\t$it" })

		code.append(lines.join("\n"))
	}

	private def addEvents() {
		def lines = EventGenerator.addEvent()
		
		code.append(lines.join("\n"))
	}

	private def generate() {
		def codeStr = code.toString()

		def methodToCallVariableName = BridgeGenerator.getMethodToCallVariableName(name)
		
		return new StringBuilder()
			.append("\tprivate static int $methodToCallVariableName = -1;\n")
			.append("\t\n")
			.append("\tpublic static void $name(Context context) throws Exception\n")
			.append("\t{\n")
			.append(codeStr)
			.append("\t}\n").toString()
	}
}
