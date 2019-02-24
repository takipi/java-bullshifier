
declare current_dir=`pwd`

function finish_bullshifier() {
	local bullshifier_name=$1
	
	echo "Compiling"
	cd $bullshifier_name && ./gradlew fatJar

	echo ""
	echo "Done"
	echo ""
	echo "To execute the new tester run:"
	echo ""
	echo "java -cp $bullshifier_name/build/libs/$bullshifier_name.jar helpers.Main <args>"
	echo ""
	echo "  Run with --help for a list of command line arguments"
	echo ""
	echo "  Example args:"
	echo ""
	echo "    Throws an exception every minute for a year"
	echo ""
	echo "    -ec 1440 -im 60000 -rc 365 -wm 0 -st -hs -sp -fc <stack-traces-length> -aa <alias>"
	echo ""
	echo ""
}
