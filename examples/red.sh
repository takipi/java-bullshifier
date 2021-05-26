#/bin/bash

source examples/colors.sh

declare bullshifier_name="red"

./gradlew run -Pskip-logic-code \
    -Pname=$bullshifier_name \
    -Poutput-directory=$bullshifier_name \
    -Psubprojects=1 \
    -Pio-cpu-intensive-matrix-size=0 \
    -Pmethods-per-class=1 \
    -Plog-info-per-method=1 \
    -Plog-warn-per-method=0 \
    -Plog-error-per-method=0 \
    -Pbridge-switch-size=3 \
    -Pswitcher-max-routes=200 \
    -Pentry-points=20 \
    -Pclasses=500 \
     $seedParameter || exit 1

finish_bullshifier $bullshifier_name
