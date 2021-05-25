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
		
		if (commandLine."methods-per-class") {
			Config.methodsPerClass = Integer.parseInt(commandLine."methods-per-class")
		}
		
		if (commandLine."log-info-per-method") {
			Config.logInfoPerMethod = Integer.parseInt(commandLine."log-info-per-method")
		}
		
		if (commandLine."log-warn-per-method") {
			Config.logWarnPerMethod = Integer.parseInt(commandLine."log-warn-per-method")
		}
		
		if (commandLine."log-error-per-method") {
			Config.logErrorPerMethod = Integer.parseInt(commandLine."log-error-per-method")
		}
		
		if (commandLine."bridge-switch-size") {
			Config.bridgeSwitchSize = Integer.parseInt(commandLine."bridge-switch-size")
		}
		
		if (commandLine."switcher-max-routes") {
			Config.switcherMaxRoutes = Integer.parseInt(commandLine."switcher-max-routes")
		}
		
		if (commandLine."entry-points") {
			Config.entryPointNum = Integer.parseInt(commandLine."entry-points")
		}
		
		if (commandLine."seed") {
			Config.seed = Integer.parseInt(commandLine."seed")
		}
		
		if (commandLine."template-directory") {
			Config.templateDirectory = commandLine."template-directory"
		}

		if (commandLine."io-cpu-intensive-matrix-size") {
			Config.ioCpuIntensiveMatrixSize = commandLine."io-cpu-intensive-matrix-size"
		}
		
		if (commandLine."skip-logic-code") {
			Config.shouldGenerateLogicCode = false
		}
		
		def singleProjectName = "tester"
		
		if (commandLine."name")
		{
			singleProjectName = commandLine."name"
		}
		
		if (!Config.rootDirectory.isDirectory()) {
			Config.rootDirectory.mkdirs()
		}

		if (!Utils.loadNamesFromFile()) {
			println "Using random numbers"
		}
		
		def isMultiProject = Config.subprojectsCount > 1

		println "Start generating $Config.subprojectsCount projects"

		if (isMultiProject) {
			Utils.ant.delete(dir:Config.rootDirectory)
			Config.rootDirectory.mkdirs()
			generateSubProjects(Config.rootDirectory, Config.subprojectsCount)
		} else {
			generateProject(Config.rootDirectory, singleProjectName)
		}

		println "Done All!"
	}

	private static generateSubProjects(rootDirectory, subprojectsCount) {
		def projectNames = []
		def generatedDir = new File("$rootDirectory/root/src/main/java/$Config.generatedPackage")
		
		if (!generatedDir.exists()) {
			generatedDir.mkdirs()
		}
		
		(1..subprojectsCount).each {
			def projectName = Utils.generateName("Proj", "", 10, true, true)
			def projectDir = "${Config.rootDirectory}/$projectName";
			
			generateProject(projectDir, projectName)
			projectNames += projectName
			
			LoaderSwitcherGenerator.write(generatedDir, projectName)
		}

		Utils.ant.copy(todir:"$rootDirectory", overwrite:false) {
			fileset(dir:"$Config.templateDirectory/multiproject")
		}
		
		Utils.ant.chmod(file:"$rootDirectory/gradlew", perm:"+x")

		GradleSettingsGenerator.write(rootDirectory, "GeneratedAgregator", projectNames + "root")
		GradleGenerator.write("$rootDirectory/root", projectNames, "helpers.MultiMain")
		MultiSwitcherGenerator.write("$rootDirectory/root/src/main/java", projectNames)
		LoaderMultiSwitcherGenerator.write("$rootDirectory/root/src/main/java", projectNames)
	}

	private static generateProject(projectDir, projectName) {
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
		
		if(Config.seed != null){
			println "\tCreating application with seed: $Config.seed"
		}else{
			println "\tCreating random application"
		}
		
		println "\tGenerating $Config.classesCount classes"

		def classes = generateClasses()

		println "\tGenerating dynamic code"

		classes.each({ it.generateMethods(classes, Config.shouldGenerateLogicCode, true, true, true)})

		println "\tWriting ${classes.size()} classes"

		writeClasses(classes, generatedDir)

		println "\tWriting switcher"

		def switcherClassName = SwitcherGenerator.write(classes, generatedDir.parentFile, projectName)
		Utils.ant.replace(file:"$projectDir/src/main/java/helpers/MyServlet.java", token:"@SWITCHER_CLASS_NAME@", value:switcherClassName)

		println "\tWriting entrypoints ${Config.entryPointNum}"
		EntryPointGenerator.write(generatedDir.parentFile, switcherClassName)

		println "\tWriting gradle configurations"

		GradleSettingsGenerator.write(projectDir, projectName, [])
		
		println "\tWriting bash runner"
		
		BashRunnerGenerator.write(projectDir, projectName)

		println "\tDone $projectName"
	}

	private static generateClasses() {
		return (1..Config.classesCount).collect(
		{
		  def methodsCount = Config.methodsPerClass
		  def clazz = new ClassGenerator(it)

		  	def methodCounter = 0
		  	
			(1..methodsCount).each {
				clazz.addMethod(methodCounter++)
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
			longOpt:"name",
			args:1,
			argName:"str",
			"The name of the output jar")
		
		commandLineOptions._(
				longOpt:"seed",
				args:1,
				argName:"number",
				"The seed used to generate the application (If not set a random application is generated every time)")
		
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
			longOpt:"methods-per-class",
			args:1,
			argName:"number",
			"The number of methods per class (default to $Config.methodsPerClass)")
		
		commandLineOptions._(
			longOpt:"log-info-per-method",
			args:1,
			argName:"number",
			"The number of info statements per method (default to $Config.logInfoPerMethod)")
		
		commandLineOptions._(
			longOpt:"log-warn-per-method",
			args:1,
			argName:"number",
			"The number of warn statements per method (default to $Config.logWarnPerMethod)")
		
		commandLineOptions._(
			longOpt:"log-error-per-method",
			args:1,
			argName:"number",
			"The number of error statements per method (default to $Config.logErrorPerMethod)")
		
		commandLineOptions._(
			longOpt:"bridge-switch-size",
			args:1,
			argName:"number",
			"The number of methods that may be called from each generated method (default to $Config.bridgeSwitchSize)")
		
		commandLineOptions._(
			longOpt:"switcher-max-routes",
			args:1,
			argName:"number",
			"The maximum methods available from switcher (default to $Config.switcherMaxRoutes)")
		
		commandLineOptions._(
			longOpt:"entry-points",
			args:1,
			argName:"number",
			"Number of entry points")
		
		commandLineOptions._(
			longOpt:"template-directory",
			args:1,
			argName:"template directory",
			"The path to the template direcotry (defult to 'template')")
		
		commandLineOptions._(
			longOpt:"io-cpu-intensive-matrix-size",
			args:1,
			argName:"number",
			"matrix size to use for io/cpu intensive logic (defaults to $Config.ioCpuIntensiveMatrixSize")

		commandLineOptions._(
			longOpt:"skip-logic-code",
			"set for generate without logic code")
	}
}
