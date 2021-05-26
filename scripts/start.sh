#!/bin/bash

# Build Color
buildCommand="./examples/$COLOR.sh"
if ! $buildCommand ; then
	echo "Failed to run java bullshifier (command: $buildCommand)"
	exit 1
fi

# Run Color
command="/opt/overops/$COLOR/run.sh --run-in-container "

if [[ -n "${RUNNING_DURATION_HOURS}" ]]; then
        command+="--running-hours $RUNNING_DURATION_HOURS "
fi

if [[ -n "${RUNNING_DURATION_MINUTES}" ]]; then
        command+="--running-minutes $RUNNING_DURATION_MINUTES "
fi

if [[ -n "${INERVAL_MILLIS}" ]]; then
        command+="--interval-millis $INERVAL_MILLIS "
fi

if [[ -n "${APP_SEED}" ]]; then
        command+="--seed $APP_SEED "
fi

echo "About to run:"
echo "$command"

export JAVA_TOOL_OPTIONS=-agentpath:/opt/overops/takipi/lib/libTakipiAgent.so=takipi.debug.logconsole

if ! $command ; then
	echo "Failed to run generated application (command: $command)"
	exit 1
fi
