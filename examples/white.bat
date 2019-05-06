@echo off
REM assuming this script is inside "java-bullshifier\examples" directory

set bullshifier_name=white
cd ..

echo.
echo Generating %bullshifier_name% bullshifier...
call gradlew run -Pskip-logic-code ^
	-Pname=%bullshifier_name% ^
	-Poutput-directory=%bullshifier_name% ^
	-Psubprojects=1 ^
	-Pio-cpu-intensive-matrix-size=0 ^
	-Pmethods-per-class=1 ^
	-Plog-info-per-method=1 ^
	-Plog-warn-per-method=0 ^
	-Plog-error-per-method=0 ^
	-Pbridge-switch-size=2 ^
	-Pswitcher-max-routes=10 ^
	-Pentry-points=1 ^
	-Pclasses=10 

echo.
echo Compiling %bullshifier_name% bullshifier...
cd %bullshifier_name%
call gradlew fatJar

echo.
echo Done!
echo.
echo To execute the new tester run:
echo.
echo "java -cp %bullshifier_name%/build/libs/%bullshifier_name%.jar helpers.Main <args>"
echo.
echo   Run with --help for a list of command line arguments
echo.
echo   Example args:
echo.
echo     Throws an exception every minute for a year
echo.
echo     "-ec 1440 -im 60000 -rc 365 -wm 0 -st -hs -sp -fc <stack-traces-length> -aa <alias>"
echo.
