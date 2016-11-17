package generator;

public class MethodGenerator
{
	private def owner
	private def name
	private def code = new StringBuilder()

	private def MethodGenerator(owner)
	{
		this.owner = owner
		this.name = Utils.generateName("met", "", (Utils.rand.nextInt(10) + 4))
	}

	private def addMethodId(methodId)
	{
		code.append("\t\tint methodId = $methodId;\n")
	}

	private def addLocals()
	{
		def logic = LocalsGenerator.generateLocals()
		def lines = logic.collect({ "\t\t$it" })

		code.append(lines.join("\n"))
	}

	private def addLogic()
	{
		def logic = LogicGenerator.generateBlocks([:])
		def lines = logic.print("\t\t")

		code.append(lines.join("\n"))
	}

	private def addBridge(classes)
	{
		def lines = BridgeGenerator.generate(classes).collect({ "\t\t$it" })

		code.append(lines.join("\n"))
	}

	def addEvent()
	{
			def lines = EventGenerator.addEvent()
	}

	private def generate()
	{
		def codeStr = code.toString()

		return """
	public static void $name(Context context) throws Exception
	{
		$codeStr
	}
"""
	}
}
