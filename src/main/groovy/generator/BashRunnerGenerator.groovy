package generator;

public class BashRunnerGenerator {
	private static write(outputDirectory, projectName) {
		def appUuidFile = new File("$outputDirectory/APP_UUID")
		def appTypeFile = new File("$outputDirectory/APP_TYPE")
		
		appUuidFile.write(UUID.randomUUID().toString())
		appTypeFile.write(projectName)
		
		Utils.ant.chmod(file:"$outputDirectory/run.sh", perm:"+x")
		Utils.ant.chmod(file:"$outputDirectory/increment-deployment.sh", perm:"+x")
	}
}
