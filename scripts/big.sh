#!/bin/bash

script_dir="$( cd "$( dirname "$0" )" && pwd )"
cd $script_dir/..

target_dir=`pwd`/big
classes_count=1000

echo "Generating to $target_dir"
./gradlew run -Pconfig-class=ComplexConfig -Poutput-directory=$target_dir -Pclasses=$classes_count
cd $target_dir

echo "Compiling"
gradle fatJar

echo ""
echo "Done"
echo ""
echo "To execute the new tester run:"
echo ""
echo "java -cp $target_dir/build/libs/tester.jar helpers.Main"
echo ""
