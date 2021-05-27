#!/bin/bash

# Rebuild Build Color - If SEED is passed in
if [[ -n "${GEN_SEED}" ]]; then
	rm -rf $COLOR
	buildCommand="./examples/$COLOR.sh"
	if ! $buildCommand ; then
		echo "Failed to run java bullshifier (command: $buildCommand)"
		exit 1
	fi
fi

runCommand="./start-run.sh"
if ! $runCommand ; then
	echo "Failed to run generated application (command: $runCommand)"
	exit 1
fi