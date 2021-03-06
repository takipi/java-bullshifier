package generator;

import static generator.Utils.*
import static generator.Config.*

class EventGenerator {
	static addEvent(className, methodName) {
		return [
			"String currentTime = dateFormat.format(new Date());",
			"long currentTimeMillis = System.currentTimeMillis();",
			"",
			"if (Config.get().shouldWriteLogInfo(context))",
			"{",
			"	logger.info(\"${className}.${methodName} called at {} (millis: {}) (stack-depth: {}) (prev method fail rate is: {})\",",
			"			currentTime, System.currentTimeMillis(), context.framesDepth, context.lastSpotPrecentage);",
			"}",
			"",
			"if (Config.get().shouldWriteLogWarn(context))",
			"{",
			"	logger.warn(\"${className}.${methodName} called at {} (millis: {}) (stack-depth: {}) (prev method fail rate is: {})\",",
			"			currentTime, System.currentTimeMillis(), context.framesDepth, context.lastSpotPrecentage);",
			"}",
			"",
			"context.instructionIndex = 1;",
			"",
			"if (Config.get().shouldFireEvent(context))",
			"{",
			"	BullshifierException.incHitsCount(context);",
			"	throw BullshifierException.build(context);\n",
			"}",
			"",
			"context.instructionIndex = 2;",
			"",
			"if (Config.get().shouldFireEvent(context))",
			"{",
			"	BullshifierException.incHitsCount(context);",
			"	throw BullshifierException.build(context);\n",
			"}",
			"",
			"context.instructionIndex = 3;",
			"",
			"if (Config.get().shouldFireEvent(context))",
			"{",
			"	BullshifierException.incHitsCount(context);",
			"	throw BullshifierException.build(context);\n",
			"}",
			"",
			"if (Config.get().shouldSuicide())",
			"{",
			"	System.exit(0);",
			"}",
			"",
			"if (Config.get().shouldRunAway(context))",
			"{",
			"	return;",
			"}"
		].collect({ "\t\t$it" })
	}
}
