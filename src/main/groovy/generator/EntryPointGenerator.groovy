package generator;

public class EntryPointGenerator {
	private static write(outputDir, switcherClassName) {
		writeEntrypointSwitcher(outputDir)
		writeEntrypointsCallable(outputDir, switcherClassName)
	}

	private static writeEntrypointSwitcher(outputDir) {
		def entrypointSwitcherFile = new File("$outputDir/generated/EntrypointSwitcher.java")
		entrypointSwitcherFile.write(generateEntrypointSwitcher())
	}

	private static generateEntrypointSwitcher() {
		def lines = "";

		lines += "package generated;\n"
		lines += "\n"
		lines += "import helpers.Config;\n"
		lines += "import helpers.Context;\n"
		lines += "import java.util.concurrent.Callable;\n"
		lines += "\n"
		lines += "public class EntrypointSwitcher\n"
		lines += "{\n"
		lines += "	public static Callable randomCallable() throws Exception\n"
		lines += "	{\n"
		lines += "		int entrypointIndex = Config.get().getRandom().nextInt(${Config.entryPointNum}) + 1;\n"
		lines += "\n"
		lines += "		switch (entrypointIndex)\n"
		lines += "		{\n"
		lines += addCases()
		lines += "		}\n"
		lines += "	return null;\n"
		lines += "	}\n"
		lines += "}\n"

		return lines
	}

	private static writeEntrypointsCallable(outputDir, switcherClassName) {
		for (int i = 1; i <= Config.entryPointNum; i++) {
			def entrypointCallableFile = new File("$outputDir/generated/EntrypointCallable${i}.java")

			entrypointCallableFile.write(generateEntrypointsCallable(i, switcherClassName))
		}
	}

	private static addCallToSwitcher(i, switcherClassName) {
		def lines = "";

		// if (i % 2 == 0) {
		// 	lines += "		try\n"
		// 	lines += "		{\n"
		// 	lines += "			${switcherClassName}.call();\n"
		// 	lines += "		}\n"
		// 	lines += "		catch (Exception e) \n"
		// 	lines += "		{\n"
		// 	lines += "			//e.printStackTrace();\n"
		// 	lines += "		}\n"
		// } else {
			lines += "		${switcherClassName}.call();\n"
		// }

		return lines
	}

	private static generateEntrypointsCallable(i, switcherClassName) {
		return """
		package generated;
		
		import helpers.Config;
		import helpers.Context;
		import java.util.concurrent.Callable;

		public class EntrypointCallable${i} implements Callable<Object> {
			public Object call() throws Exception {				
				long startTime = System.currentTimeMillis();
				try {
					${addCallToSwitcher(i, switcherClassName)}
				}
				finally {
					helpers.StatsReporter.get().reportLatency(System.currentTimeMillis() - startTime);
				}
				return null;
			}
		}

		""";
	}

	private static addCases() {
		def lines = "";

		for (int i = 1; i <= Config.entryPointNum; i++) {
			lines += "			case ${i}: return new EntrypointCallable${i}();\n"
		}

		return lines
	}
}
