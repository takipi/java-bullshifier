package generator;

import java.util.concurrent.Callable
import java.util.concurrent.Executors

public class AppGenerator {
	private static scriptFile
	private static commandLineOptions

	public static main(args) {
		initialize();

		def commandLine = commandLineOptions.parse(args)

		if (commandLine.help) {
			println commandLineOptions.usage()
			System.exit(0)
		}

		if (commandLine."output-directory") {
			Config.rootDirectory = new File(commandLine."output-directory")
		} else {
			Config.rootDirectory = new File("output")
		}

		if (commandLine."classes") {
			Config.classesCount = Integer.parseInt(commandLine."classes")
		}

		if (commandLine."subprojects") {
			Config.subprojectsCount = Integer.parseInt(commandLine."subprojects")
		}

		if (commandLine."config-class") {
			Config.configClassToUse = commandLine."config-class"
		}

		if (commandLine."template-directory") {
			Config.templateDirectory = commandLine."template-directory"
		}

		if (!Config.rootDirectory.isDirectory()) {
			Config.rootDirectory.mkdirs()
		}

		def isMultiProject = Config.subprojectsCount > 1

		println "Start generating $Config.subprojectsCount projects"

		if (isMultiProject) {
			Utils.ant.delete(dir:Config.rootDirectory)
			Config.rootDirectory.mkdirs()
			generateSubProjects(Config.rootDirectory, Config.subprojectsCount)
		} else {
			generateProject(Config.rootDirectory, "")
		}

		println "Done All!"
	}

	private static generateSubProjects(rootDirectory, subprojectsCount) {
		def projectNames = []

		(1..subprojectsCount).each {
			def projectName = Utils.generateName("Proj")

			generateProject(Config.rootDirectory, projectName)
			projectNames += projectName
		}

		Utils.ant.copy(todir:"$rootDirectory", overwrite:false) {
			fileset(dir:"$Config.templateDirectory/multiproject")
		}

		GradleSettingsGenerator.write(rootDirectory, "GeneratedAgregator", projectNames + "root")
		GradleGenerator.write("$rootDirectory/root", projectNames, "helpers.MultiMain")
		MultiSwitcherGenerator.write("$rootDirectory/root/src/main/java", projectNames)
	}

	private static generateProject(rootDirectory, projectName) {
		def projectDir = "$rootDirectory/$projectName"
		def generatedDir = new File("$projectDir/src/main/java/$Config.generatedPackage")

		if (!generatedDir.exists()) {
			generatedDir.mkdirs()
		}

		println "Generating $projectName to $generatedDir"

		if (generatedDir.exists()) {
			Utils.ant.delete(dir:generatedDir)
		}

		generatedDir.mkdirs()

		Utils.ant.copy(todir:"$projectDir", overwrite:true)
		{
			fileset(dir:"$Config.templateDirectory/project")
		}
		
		Utils.ant.chmod(file:"$projectDir/gradlew", perm:"+x")

		println "\tGenerating $Config.classesCount classes"

		def classes = generateClasses()

		println "\tGenerating dynamic code"

		classes.each({ it.generateMethods(classes, true, true, true, true)})

		println "\tWriting ${classes.size()} classes"

		writeClasses(classes, generatedDir)

		println "\tWriting switcher"

		def switcherClassName = SwitcherGenerator.write(classes, generatedDir.parentFile, projectName)
		Utils.ant.replace(file:"$projectDir/src/main/java/helpers/Config.java", token:"@CONFIG_CLASS@", value:Config.configClassToUse)
		Utils.ant.replace(file:"$projectDir/src/main/java/helpers/MyServlet.java", token:"@SWITCHER_CLASS_NAME@", value:switcherClassName)

		println "\tWriting entrypoints"
		EntryPointGenerator.write(generatedDir.parentFile, switcherClassName)

		println "\tWriting gradle configurations"

		GradleSettingsGenerator.write(projectDir, projectName, [])

		println "\tDone $projectName"
	}

	private static generateClasses() {
		return (1..Config.classesCount).collect(
		{
		  def methodsCount = Utils.rand.nextInt(Config.maxMethodsPerClass) + 1
		  def clazz = new ClassGenerator(it)

			(1..methodsCount).each {
				clazz.addMethod()
			}

			return clazz
		})
	}

	private static writeClasses(classes, generatedDir) {
		def pool = Executors.newFixedThreadPool(10)
		def futures = []

		classes.each({
			def clazz = it

			futures += pool.submit({
				clazz.write(generatedDir.parentFile)
			} as Callable)
		})

		futures.each({ it.get() })
		pool.shutdown()
	}

	private static initialize() {
		def scriptPath = Config.class.protectionDomain.codeSource.location.path
		scriptFile = new File(scriptPath)

		commandLineOptions = new CliBuilder()

		commandLineOptions.width = 130
		commandLineOptions.usage = "groovy ${scriptFile.name}.groovy [params]"
		commandLineOptions.footer = ""
		commandLineOptions.header = ""

		commandLineOptions.h(
			longOpt:"help", "Print this usage")

		commandLineOptions._(
			longOpt:"output-directory",
			args:1,
			argName:"dir",
			"Output directory for the generated application")

		commandLineOptions._(
			longOpt:"classes",
			args:1,
			argName:"number",
			"The number of generated classes (default to $Config.classesCount)")

		commandLineOptions._(
			longOpt:"subprojects",
			args:1,
			argName:"number",
			"The number of generated projects (default to $Config.subprojectsCount)")

		commandLineOptions._(
			longOpt:"config-class",
			args:1,
			argName:"class name",
			"The name of the config class (default to SimpleConfig)")

		commandLineOptions._(
			longOpt:"template-directory",
			args:1,
			argName:"template directory",
			"The path to the template direcotry (defult to 'template')")
	}
}
