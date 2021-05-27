#/bin/bash

source examples/colors.sh

declare bullshifier_name="yellow"

./gradlew run -Pskip-logic-code \
    -Pname=$bullshifier_name \
    -Poutput-directory=$bullshifier_name \
    -Psubprojects=1 \
    -Pio-cpu-intensive-matrix-size=0 \
    -Pmethods-per-class=1 \
    -Plog-info-per-method=1 \
    -Plog-warn-per-method=0 \
    -Plog-error-per-method=0 \
    -Pbridge-switch-size=2 \
    -Pswitcher-max-routes=50 \
    -Pentry-points=5 \
    -Pclasses=100 \
     $seedParameter || exit 1

finish_bullshifier $bullshifier_name
