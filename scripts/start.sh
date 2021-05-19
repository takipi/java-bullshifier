#!/bin/bash

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

echo "About to run:"
echo "$command"

if ! $command ; then 
	echo "Failed to run java bullshifier (command: $command)"
	exit 1
fi
