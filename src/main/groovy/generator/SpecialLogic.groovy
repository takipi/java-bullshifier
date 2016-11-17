package generator;

public class SpecialLogic
{
	private static generateSpecial()
	{
		switch (Utils.rand.nextInt(3))
		{
			case (0): return parseIntBlock()
			case (1): return synchronizedBlock()
			case (2): return newFileBlock()
		}

		return []
	}

	private static parseIntBlock()
	{
		def num = Utils.generateName("num")

		return [
			"try",
			"{",
				"\tInteger.parseInt(\"$num\");",
			"}",
			"catch(NumberFormatException e) ",
			"{",
				"\te.printStackTrace();",
			"}"
		]
	}

	private static synchronizedBlock()
	{
		return [
			"Object locker = new Object();",
			"",
			"synchronized (locker)",
			"{",
				"\tSystem.out.println(\"synchronized block\");",
			"}"
		]
	}

	private static newFileBlock()
	{
		def path = []
		def len = Utils.rand.nextInt(10)

		for (def i = 0; i < len; i++)
		{
			if (i - 1 == len)
			{
				path += Utils.generateName("file")
			}
			else
			{
				path += Utils.generateName("dir")
			}
		}

		def pathStr = path.join("/")

		return [
			"java.io.File file = new java.io.File(\"/$pathStr\");",
			"",
			"if (file.canRead())",
			"{",
				"\tSystem.out.println(\"File exists\");",
			"}",
			"else",
			"{",
				"\tSystem.out.println(\"File not exists\");",
			"}"
		]
	}
}
