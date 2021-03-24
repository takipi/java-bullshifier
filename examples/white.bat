@echo off
REM assuming we're at "java-bullshifier\examples" and output to "java-bullshifier\output"

set bullshifier_name=white
set output_directory=output\%bullshifier_name%
cd ..
echo Generating %bullshifier_name% bullshifier...
call gradlew run -Pskip-logic-code ^
	-Pname=%bullshifier_name% ^
	-Poutput-directory=%output_directory% ^
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
echo Compiling %bullshifier_name% bullshifier...
cd %output_directory%
call gradlew fatJar
cd ..\..\examples
call colors.bat %bullshifier_name%
pause
