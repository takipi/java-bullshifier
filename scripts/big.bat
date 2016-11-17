@echo off

set script_dir=%~dp0
cd %script_dir%\..

set target_dir=small
set classes_count=8000

echo Generating to 0%target_dir%
call gradlew run -Pconfig-class=ComplexConfig -Poutput-directory=%target_dir% -Pclasses=%classes_count%
cd %target_dir%

echo Compiling
gradle fatJar

echo To execute the new tester run:
echo java -cp %target_dir%/build/libs/tester.jar helpers.Main
