#!/bin/bash

script_dir="$( cd "$( dirname "$0" )" && pwd )"
cd $script_dir/..

classes_count=$1

if [ -n "$2" ]; then
	target_dir=`pwd`/$2
else
	target_dir=`pwd`/custom
fi

echo "Generating to $target_dir"
./gradlew run -Poutput-directory=$target_dir -Pclasses=$classes_count

if [ "$?" != "0" ]; then
	exit 1
fi

cd $target_dir

echo "Compiling"
./gradlew fatJar

echo ""
echo "Done"
echo ""
echo "To execute the new tester run:"
echo ""
echo "java -cp $target_dir/build/libs/tester.jar helpers.Main"
echo ""
