package generator;

public class LocalsGenerator
{
	private static deepConf=2;
	private static tabs = "    ";

	private static generateLocals()	{
		def result = [];
		result += generateLocalsInternal("root", 0);
		return result;
	}

	private static generateLocalsInternal(name, deep) {
		def num = Utils.rand.nextInt(4)

		if (deep <= deepConf) {
			switch (num) {
				case (0): return generateSet(name ,deep);
				case (1): return generateList(name ,deep);
				case (2): return generateArray(name ,deep);
				case (3): return generateMap(name ,deep);
			}
		} else {
			num = Utils.rand.nextInt(4)

			switch (num) {
				case (0): return generateString(name);
				case (1): return generateInt(name);
				case (2): return generateLong(name);
				case (3): return generateBoolean(name);
			}
		}
		
		return [];
	}

	private static generateSet(setName ,deep) {
		def result = ["Set<Object> $setName = new HashSet<Object>();"]
		result += generateSetInternal("${setName}", deep)
		return result;
	}

	private static generateSetInternal(name, deep) {
		def result = [];
		def limit = Utils.rand.nextInt(2)+1;

		for (int i = 0; i < limit; i++) {
				def localValueName = Utils.generateName("val", "", 10, true, true);
				result += generateLocalsInternal(localValueName, deep + 1);
				result += "${name}.add($localValueName);"
		}

		result += ""
		return result;
	}

	private static generateList(listName, deep) {
		def result = ["List<Object> $listName = new LinkedList<Object>();"]
		result += generateListInternal("${listName}", deep)
		return result;
	}

	private static generateListInternal(name, deep) {
		def result = [];
		def limit = Utils.rand.nextInt(2)+1;

		for (int i = 0; i < limit; i++) {
			def localValueName = Utils.generateName("val", "", 10, true, true);
			result += generateLocalsInternal(localValueName, deep + 1);
			result += "${name}.add($localValueName);"
		}

		result += ""
		return result;
	}

	private static generateArray(arrName, deep) {
		def size = Utils.rand.nextInt(10)+2;
		def result = ["Object[] $arrName = new Object[$size];"]
		result += generateArrayInternal("${arrName}","${size}", deep)
		return result;
	}

	private static generateArrayInternal(arrName ,size ,deep) {
		def localValueName = Utils.generateName("val", "", 10, true, true);

		def result = [];
		
		result += generateLocalsInternal(localValueName, deep + 1);
		result += "${tabs}$arrName[0] = $localValueName;"
		result += "for (int i = 1; i < $size; i++)"
		result += "{"
		result += "${tabs}$arrName[i] = Config.get().getRandom().nextInt(1000);"
		result += "}"
		result += ""
		result += ""
		
		return result;
	}

	private static generateMap(mapName , deep) {
		def result = ["Map<Object, Object> $mapName = new HashMap();"]
		result += generateMapInternal("${mapName}", deep)
		return result;
	}

	private static generateMapInternal(mapName, deep) {
		def result = [];
		def limit = Utils.rand.nextInt(2)+1;

		for (int i = 0; i < limit; i++) {
			def localValueName = Utils.generateName("mapVal", "", 10, true, true)
			def localKeyName = Utils.generateName("mapKey", "", 10, true, true)
			result += generateLocalsInternal(localValueName, deep + 1);
			result += generateLocalsInternal(localKeyName, deep + 1);
			result += "${mapName}.put(\"$localValueName\",\"$localKeyName\" );" 
		}

		result += ""
		return result;
	}

	private static generateString(stringName) {
		def result = [];
		def varStrValue = Utils.generateName("Str", "", 10, true, true);
		result += "String $stringName = \"$varStrValue\";"
		result += ""
		return result;
	}

	private static generateInt(intName) {
		def result = [];
		def varIntValue = "${Utils.rand.nextInt(1000)}";
		result += "int $intName = $varIntValue;"
		result += ""
		return result;
	}

	private static generateLong(longName) {
		def result = [];
		def l = "L"
		def varLongValue = "${Utils.rand.nextLong()}";
		result += "long $longName = $varLongValue$l;"
		result += ""
		return result;
	}

	private static generateBoolean(boolName) {
		def result = [];
		def varBooleanValue = "${Utils.rand.nextBoolean()}"
		result += "boolean $boolName = $varBooleanValue;"
		result += ""
		return result;
	}
}
