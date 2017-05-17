package generator;

public class Utils {
	private static ant = new AntBuilder()
	private static rand = new Random()

	private static generateName(prefix = "", suffix = "", length = 10, capital = true) {
		def preserveWord = ["new", "else", "try", "if", "for", "do", "while", "return", "private", "package", "import", "false", "null",
			"int", "double", "bool", "short", "char", "long", "case", "static", "public", "class", "switch", "true", "byte", "enum"]

		def generate = {
			def randomChars = (0..length).collect {
				return (char)(rand.nextInt(26) + 97)
			}

			if (capital) {
				randomChars[0] = Character.toUpperCase(randomChars[0])
			}

			def randomString = randomChars.join("")
		}

		def randomString = generate()

		while (randomString in preserveWord) {
			randomString = generate()
		}

		return "$prefix$randomString$suffix"
	}
}
