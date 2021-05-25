package generator;

public class BashRunnerGenerator {
	private static write(outputDirectory, projectName) {
		def appUuidFile = new File("$outputDirectory/APP_UUID")
		def appTypeFile = new File("$outputDirectory/APP_TYPE")
		
		appUuidFile.write(UUID.randomUUID().toString())
		appTypeFile.write(projectName)
		
		Utils.ant.chmod(file:"$outputDirectory/run.sh", perm:"+x")
		Utils.ant.chmod(file:"$outputDirectory/increment-deployment.sh", perm:"+x")

		// Informational file; just to know what seed was used if you want to share the application
		if(Config.seed != null){
			def seedFile = new File("$outputDirectory/SEED")
			seedFile.write("Seed: "+Config.seed)
		}

	}
}
