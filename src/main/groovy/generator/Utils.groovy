package generator;

public class Utils {
	private static ant = new AntBuilder()
	private static rand = (Config.seed == null ? new Random() : new Random(Config.seed));
	private static predefinedNames
	
	private static loadNamesFromFile() {
		try {
			def url = Utils.class.getResource("/predefined-names.txt")
			
			if (url == null) {
				println "Unable to find predefined-names.txt"
				return
			}
			
			def file = new File(url.toURI())
			
			if (!file.canRead()) {
				println "Unable to read %file, from url: $url"
				return
			}
			
			predefinedNames = file.text.readLines()
		}
		catch(Exception e) {
			println "Error reading predefined-names.txt"
			e.printStackTrace();
		}
	}
	
	private static namesOfJavaStuff = [
		"Strategy", "Factory", "Singletone", "Builder", "Aadapter", 
		"Bridge", "Manager", "Util", "Helper", "Resource", 
		"Data", "Entity", "Convertor", "Compressor", "Handler", 
		"Facade", "Composite", "Concurrent", "Optimizer", "Syncer",
		"Runner", "Composer", "Navigator", "Worker", "Migrator",
		"Generator", "Compiler", "Destroyer", "Executor", "Thread",
		"Switcher", "Loader", "Dumper", "Initializer", "Interface",
		"Delegator", "Logger", "Monitor", "Reporter", "Killer"
	]
	
	private static generateName(prefix = "", suffix = "", length = 10, capital = true, random = false) {
		def preserveWord = ["new", "else", "try", "if", "for", "do", "while", "return", "private", "package", "import", "false", "null",
			"int", "double", "bool", "short", "char", "long", "case", "static", "public", "class", "switch", "true", "byte", "enum"]

		def generate = {
			def randomChars
			
			if (predefinedNames == null || random)
			{
				randomChars = generateRandomChars(length)
			}
			else
			{
				randomChars = generateFunnyName(length)
			}

			if (capital) {
				randomChars[0] = Character.toUpperCase(randomChars[0])
			} else {
				randomChars[0] = Character.toLowerCase(randomChars[0])
			}

			def randomString = randomChars.join("")
		}

		def randomString = generate()

		while (randomString in preserveWord) {
			randomString = generate()
		}

		return "$prefix$randomString$suffix"
	}
	
	private static generateRandomChars(length)
	{
		return (0..length).collect {
			return (char)(rand.nextInt(26) + 97)
		}
	}
	
	private static usedNames = [].toSet()
	
	private static generateFunnyName(length) {
		def availableNamesSize = predefinedNames.size() * namesOfJavaStuff.size()
		
		while (true) {
			if (usedNames.size() >= (availableNamesSize - 1000)) {
				return generateRandomChars(length)
			}

			def randomNameIndex = rand.nextInt(predefinedNames.size())
			def javaStuffIndex = rand.nextInt(namesOfJavaStuff.size())
			def predefinedName = predefinedNames[randomNameIndex]
			def javaStuff = namesOfJavaStuff[javaStuffIndex]
			
			def generatedName = predefinedName + javaStuff
			
			if (usedNames.contains(generatedName)) {
				continue
			}
			
			usedNames.add(generatedName)
			
			return generatedName.toCharArray()
		}
	}
}
