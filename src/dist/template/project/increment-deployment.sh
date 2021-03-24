#!/bin/bash

declare -r script_dir=`cd "$( dirname "$0" )" && pwd`
cd $script_dir

declare -r emulatorDataDir=emulator-data

mkdir -p $emulatorDataDir

function increment_seed()
{
	for appName in `ls $emulatorDataDir`; do
		local appDataDir="$emulatorDataDir/$appName"
		
		if [ ! -d "$appDataDir" ]; then
			continue
		fi

		local deploymentSeedFile="$appDataDir/DEPLOYMENT_SEED"
		local currentSeed=0
		
		if [ -r "$deploymentSeedFile" ]; then
			let currentSeed=$(cat $deploymentSeedFile)
		fi
		
		let newSeed="$currentSeed+1"
		echo $newSeed > $deploymentSeedFile
	done
}

increment_seed
