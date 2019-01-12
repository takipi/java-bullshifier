#/bin/bash

source examples/colors.sh

declare bullshifier_name="killer"

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
    -Pswitcher-max-routes=1000 \
    -Pentry-points=200 \
    -Pclasses=5000 || exit 1

finish_bullshifier $bullshifier_name
