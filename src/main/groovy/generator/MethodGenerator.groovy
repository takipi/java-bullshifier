package generator;

public class MethodGenerator {
	private def owner
	private def name
	private def code = new StringBuilder()

	private def MethodGenerator(owner, methodId) {
		this.owner = owner
		def paddedMethodId = String.format("%03d", methodId);
		this.name = "method$paddedMethodId"
	}

	public def addClassAndMethodId(classId, methodId) {
		code.append("\t\tint methodId = $methodId;\n")
		code.append("\t\tInteger entryPointNum = Config.get().entryPointIndex.get();")
		code.append("\t\tConfig.get().updateContext(context, entryPointNum, $classId, $methodId);\n\n")
	}

	private def addLocals() {
		def locals = LocalsGenerator.generateLocals()
		def lines = locals.collect({ "\t\t$it" })
		
		code.append("\t\t// Start of fake locals generator\n")
		code.append(lines.join("\n"))
		code.append("// End of fake locals generator\n")
		code.append("\n")
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
		code.append("\n")
	}

	private def generate() {
		def codeStr = code.toString()

		def methodToCallVariableName = BridgeGenerator.getMethodToCallVariableName(name)
		
		return new StringBuilder()
			.append("\tpublic static void $name(Context context) throws Exception\n")
			.append("\t{\n")
			.append(codeStr)
			.append("\n")
			.append("\t}\n").toString()
	}
}
