package generator;

public class LogicGenerator {
	static generate(methodName, context) {
		def method = new MethodBlock()
		method.name = methodName
		method.generate(context)

		return method
	}

	static generateBlocks(context) {
		def body = new BodyBlock()
		body.generate(context)

		return body
	}

	static class Block {
		static currentId = 0

		def parent
		def id
		def childIndex

		def Block() {
			this.id = currentId++
		}

		def depth() {
			def counter = 0
			def current = this

			while (current) {
				counter++
				current = current.parent
			}

			return counter
		}

		def path() {
			def result = []
			def current = this

			while (current) {
				result += current
				current = current.parent
			}

			return result
		}

		def scope() {
			def path = path()

			return (path + path.collect({ it.nodesAbove() })).flatten().unique()
		}

		def nodesAbove() {
			if (!parent) {
				return []
			}

			return parent.nodesAbove(this)
		}

		def print(tabs) {
			return []
		}
	}

	static class BodyBlock extends Block {
		def body = [:]

		def generate(context) {
			def shouldGenerateMoreBlocks = Utils.rand.nextBoolean()
			def depth = depth()

			if (depth > 1 && (depth >= Config.maxBlocksDepth || !shouldGenerateMoreBlocks)) {
				def special = new SpecialBlock()

				special.parent = this
				special.generate(context)

				this.body = [(special.id):(special)]
			} else {
				def blocksCount = Utils.rand.nextInt(Config.minBlocksPerMethod + Config.maxBlocksPerMethod) + Config.minBlocksPerMethod

				for (def i = 0; i < blocksCount; i++) {
					def block = randomBlock()
					addBlock(block)
				}

				this.body.each {
					it.value.generate(context);
				}
			}
		}

		def addBlock(block) {
			this.body[block.id] = block

			block.childIndex = this.body.size()
			block.parent = this
		}

		def randomBlock() {
			def block = Utils.rand.nextInt(6)

			switch (block)
			{
				case (0): return new ForBlock()
				case (1): return new IfBlock()
				case (2): return new WhileBlock()
				case (3): return new VarBlock()
				case (4): return new TryBlock()
				case (5): return new AssignBlock()
				//case (5): return new CallBlock()
			}
		}

		def print(tabs) {
			def result = []
			result += "${tabs}{"
			body.each
			{
				result += it.value.print("$tabs\t")
			}
			result += "${tabs}}"
			return result
		}

		def nodesAbove(child) {
			return body.collect({ it.value }).grep({ it.childIndex < child.childIndex })
		}
	}

	static class VarHolder extends BodyBlock {
		def var
	}

	static class MethodBlock extends BodyBlock {
		def name
		def body

		def generate(context) {
			super.generate(context)

			def returnBlock = new ReturnBlock()
			addBlock(returnBlock)
			returnBlock.generate(context)
		}

		def print(tabs) {
			def result = []
			result += "${tabs}public static long $name()"
			result += super.print(tabs)
			result += "${tabs}"
			return result
		}

		public String toString() {
			return "method"
		}
	}

	static class ForBlock extends BodyBlock {
		def from
		def to
		def loopIndex

		def generate(context) {
			super.generate(context)
			this.from = Utils.rand.nextInt(Config.maxLoopStart + 1)
			this.to = Utils.rand.nextInt(Config.maxLoopEnd) + from
			this.loopIndex = "loopIndex${depth()}$id"
		}

		def print(tabs) {
			def result = []
			result += "${tabs}int $loopIndex = 0;"
			result += "${tabs}for ($loopIndex = $from; $loopIndex < $to; $loopIndex++)"
			result += super.print(tabs)
			result += "${tabs}"
			return result
		}

		public String toString() {
			return "for"
		}
	}

	static class ElseIfBlock extends BodyBlock {
		def body
		def condition

		def generate(context) {
			super.generate(context)
			this.condition = generateCondition(this)
		}

		def print(tabs) {
			def result = []
			result += "${tabs}else if ($condition)"
			result += super.print(tabs)
			return result
		}

		public String toString() {
			return "elsif"
		}
	}

	static class ElseBlock extends BodyBlock {
		def body

		def print(tabs) {
			def result = []
			result += "${tabs}else"
			result += super.print(tabs)
			return result
		}

		public String toString() {
			return "else"
		}
	}

	static class IfBlock extends BodyBlock {
		def condition
		def elseIfBlocks = []
		def elseBlock

		def generate(context) {
			super.generate(context)
			this.condition = generateCondition(this)

			def shouldGenerateElseBlock = Utils.rand.nextBoolean()

			if (shouldGenerateElseBlock) {
				this.elseBlock = new ElseBlock()
				this.elseBlock.parent = this.parent
				this.elseBlock.generate(context)
			}

			def elseIfBlockCount = Utils.rand.nextInt(Config.maxElseIfBlocks)

			for (def i = 0; i < elseIfBlockCount; i++) {
				def elseIfBlock = new ElseIfBlock()
				elseIfBlock.parent = this.parent
				elseIfBlock.generate(context)
				elseIfBlocks += elseIfBlock
			}
		}

		def print(tabs) {
			def result = []
			result += "${tabs}if ($condition)"
			result += super.print(tabs)

			for (def elseIfBlock : elseIfBlocks) {
				result += elseIfBlock.print(tabs)
			}

			if (elseBlock) {
				result += elseBlock.print(tabs)
			}

			result += "${tabs}"
			return result
		}

		public String toString() {
			return "if"
		}
	}

	static class WhileBlock extends BodyBlock {
		def body
		def loopIndex
		def from
		def to

		def generate(context) {
			super.generate(context)
			this.loopIndex = "whileIndex${depth()}${id}"
			this.from = Utils.rand.nextInt(Config.maxLoopStart + 1)
			this.to = Utils.rand.nextInt(Config.maxLoopEnd) + from
		}

		def print(tabs) {
			def result = []
			result += "${tabs}long $loopIndex = $from;"
			result += "${tabs}"
			result += "${tabs}while ($loopIndex-- > 0)"
			result += super.print(tabs)
			result += "${tabs}"
			return result
		}

		public String toString() {
			return "while"
		}
	}

	static class CatchBlock extends BodyBlock {
		def var

		def generate(context) {
			this.var = "ex${depth()}$id"
		}
		
		def print(tabs) {
			def result = []
			result += "${tabs}catch (Exception $var)"
			result += super.print(tabs)
			return result
		}

		public String toString() {
			return "catch"
		}
	}

	static class FinallyBlock extends BodyBlock {
		def print(tabs) {
			def result = []
			result += "${tabs}finally"
			result += super.print(tabs)
			return result
		}

		public String toString() {
			return "finally"
		}
	}

	static class TryBlock extends BodyBlock {
		def catchBlock
		def finallyBlock

		def generate(context) {
			super.generate(context)

			def shouldGenerateCatchBlock = Utils.rand.nextBoolean()
			def shouldGenerateFinallyBlock = Utils.rand.nextBoolean()

			if (!shouldGenerateCatchBlock) {
				shouldGenerateFinallyBlock = true
			}

			if (shouldGenerateCatchBlock) {
				this.catchBlock = new CatchBlock()
				this.catchBlock.parent = this.parent
				this.catchBlock.generate(context)
			}

			if (shouldGenerateFinallyBlock) {
				this.finallyBlock = new FinallyBlock()
				this.finallyBlock.parent = this.parent
				this.finallyBlock.generate(context)
			}
		}

		def print(tabs) {
			def result = []
			result += "${tabs}try"
			result += super.print(tabs)

			if (catchBlock) {
				result += catchBlock.print(tabs)
			}

			if (finallyBlock) {
				result += finallyBlock.print(tabs)
			}

			result += "${tabs}"
			return result
		}

		public String toString() {
			return "try"
		}
	}

	static class VarBlock extends VarHolder {
		def value

		def generate(context) {
			this.var = Utils.generateName("var", "", 10, true, true);
			this.value = generateValue(this.parent)
		}

		def print(tabs) {
			def result = []
			result += "${tabs}long $var = $value;"
			return result
		}

		public String toString() {
			return "var"
		}
	}

	static class CallBlock extends VarHolder {
		def function

		def generate(context) {
			var = Utils.generateName("ret", "", 10, true, true)
		}

		def print(tabs) {
			def result = []
			result += "${tabs}$var = call"
			return result
		}

		public String toString() {
			return "call"
		}
	}

	static class ReturnBlock extends Block {
		def value

		def generate(context) {
			this.value = generateValue(this)
		}

		def print(tabs) {
			def result = []
			result += "${tabs}return $value;"
			return result
		}

		public String toString() {
			return "call"
		}
	}

	static class AssignBlock extends VarHolder {
		def value

		def generate(context) {
			def scopedVars = scope().grep({ it instanceof VarHolder }).collect({ it.var }).grep({ it })

			if (scopedVars) {
				var = scopedVars[Utils.rand.nextInt(scopedVars.size())]

				if (var) {
					value = generateValue(this)
				}
			}
		}

		def print(tabs) {
			def result = []

			if (var) {
				result += "${tabs}$var = $value;"
			}

			return result
		}

		public String toString() {
			return "assign"
		}
	}

	static class SpecialBlock extends Block {
		def code

		def generate(context) {
			this.code = SpecialLogic.generateSpecial()
		}

		def print(tabs) {
			def result = []
			def codeStr = this.code.join("\n$tabs")
			result += "$tabs$codeStr"
			return result
		}

		public String toString() {
			return "special"
		}
	}

	static generateCondition(parent) {
		def value = generateValue(parent, 2)
		def num = Utils.rand.nextInt(1000000) + 1

		return "($value % $num) == 0"
	}

	static generateValue(parent, maxLength = Config.maxExpressionLength) {
		def leafers = [
			{
				def from = Utils.rand.nextInt(10)
				def to = Utils.rand.nextInt(1000) + from

				return "Config.get().getRandom().nextInt($to) + $from"
			},
			{
				return "${Utils.rand.nextInt(10000)}"
			},
			{
				def scope = parent.scope()
				def vars = scope.grep({ it instanceof VarHolder }).collect({ it.var })
				def loops = scope.grep({ it instanceof ForBlock || it instanceof WhileBlock }).collect({ it.loopIndex })
				def all = (vars + loops).grep({ it }).grep({ it != parent })

				if (all)
				{
					return all[Utils.rand.nextInt(all.size())]
				}
			}
		]

		def operators = [ "+", "-", "*" ]

		def expressionLength = Utils.rand.nextInt(maxLength)
		def first = true
		def result = ""

		(0..expressionLength).each {
			def leaf = leafers[Utils.rand.nextInt(leafers.size())]()

			while (!leaf) {
				leaf = leafers[Utils.rand.nextInt(leafers.size())]()
			}

			if (!first) {
				result += " "
				result += operators[Utils.rand.nextInt(operators.size())]
				result += " "
			}

			first = false
			result += "($leaf)"
		}

		return result
	}
}
