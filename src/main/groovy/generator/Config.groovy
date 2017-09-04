package generator;

public class Config {
    public static rootDirectory
    public static generatedPackage = "generated"
    public static templateDirectory = "template"
    public static configClassToUse = "SimpleConfig"

    public static classesCount = 10
    public static methodsPerClass = 5
    public static maxPackageLength = 4
    public static bridgeSwitchSize = 4
    public static switcherMaxRoutes = Integer.MAX_VALUE

    public static subprojectsCount = 1

    public static minBlocksPerMethod = 2
    public static maxBlocksPerMethod = 4
    public static maxBlocksDepth = 2
    public static maxLoopStart = 0
    public static maxLoopEnd = 10000
    public static maxElseIfBlocks = 2
    public static maxExpressionLength = 2

    public static entryPointNum = 20
    
    public static logInfoPerMethod = 5
    public static logWarnPerMethod = 5
    public static logErrorPerMethod = 5
    
    public static ioCpuIntensiveMatrixSize = 300
    public static ioCpuIntensiveFileLimit = 100
    public static ioCpuIntensiveRepeats = 7 
}
