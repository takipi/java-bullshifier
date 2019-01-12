package generator;

class ClassGenerator {
	private def name
	private def classPackage
	private def methods = []
	private def classId

	private def ClassGenerator(classId) {
		def packageParts = Utils.rand.nextInt(Config.maxPackageLength) + 1
		this.classId = classId
		this.name = Utils.generateName("", "", (Utils.rand.nextInt(10) + 4), true, false)
		this.classPackage = "generated." + (0..packageParts).collect(
		{
			return Utils.generateName("", "", (Utils.rand.nextInt(3) + 2), false, true)
		}).join(".")
	}

	private def addMethod() {
		def method = new MethodGenerator(this)

		methods += method
	}

	private def randomMethod() {
		return methods[Utils.rand.nextInt(methods.size())]
	}

	private def qualifyName() {
		return "${classPackage}.$name"
	}

	private def generateMethods(classes, withLogic, withBridge, withLocals, withEvent) {
		def methodCounter = 0

		methods.each {
			it.addClassAndMethodId(classId, methodCounter++)

			if (withLocals) {
				it.addLocals()
			}

			if (withEvent) {
				it.addEvents()
			}

			if (withBridge) {
				it.addBridge(classes)
			}

			if (withLogic) {
				it.addLogic()
			}
		}
	}

	private def toPath() {
		def packagePath = classPackage.replace(".", "/")

		return "$packagePath/${name}.java"
	}

	private def write(outputDir) {
		def code = new StringBuilder()
		code.append(classHeader())

		methods.each {
			code.append(it.generate());
			code.append("\n")
		}

		code.append("}\n")

		def classFile = new File("$outputDir/${toPath()}")
		classFile.parentFile.mkdirs()
		classFile.write(code.toString())
	}

	private def classHeader() {
		return """package $classPackage;

import helpers.Config;
import helpers.Context;
import helpers.BullshifierException;
import java.util.*;
import java.util.logging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;


public class $name
{
	 public static final int classId = $classId;
	 static final Logger logger = LoggerFactory.getLogger(${name}.class);
"""
	}
}
