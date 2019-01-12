#/bin/bash

declare current_dir=`pwd`
declare bullshifier_name="black"

./gradlew run -Pskip-logic-code \
    -Pname=$bullshifier_name \
    -Poutput-directory=$bullshifier_name \
    -Psubprojects=1 \
    -Pio-cpu-intensive-matrix-size=0 \
    -Pconfig-class=SimpleConfig \
    -Pmethods-per-class=1 \
    -Plog-info-per-method=1 \
    -Plog-warn-per-method=0 \
    -Plog-error-per-method=0 \
    -Pbridge-switch-size=10 \
    -Pswitcher-max-routes=200 \
    -Pentry-points=20 \
    -Pclasses=2000 || exit 1

echo "Compiling"
cd $bullshifier_name && ./gradlew fatJar

echo ""
echo "Done"
echo ""
echo "To execute the new tester run:"
echo ""
echo "java -cp $current_dir/$bullshifier_name/build/libs/$bullshifier_name.jar helpers.Main"
echo ""
