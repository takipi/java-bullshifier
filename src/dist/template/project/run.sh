#!/bin/bash

declare -r script_dir=`cd "$( dirname "$0" )" && pwd`
cd $script_dir

source params.sh

declare emulatorDataDir=emulator-data

declare runInContainer="false"
declare runningHours=0
declare runningMinutes=1
declare processesCount=5
declare hostnamePrefix="hostname-"
declare serversCount=5
declare appsCount=2
declare dryRun="false"
declare processHeapSize=10m
declare intervalMillis=60000
declare extraJVMArgs=""
declare sleepSeconds=0
declare appType="Undefined"
declare appUuid="ffffffff-ffff-ffff-ffff-ffffffffffff"

function parse_command_line()
{
	params_add "run-in-container" "ric" "$runInContainer" "runInContainer" "boolean" \
			"Do not use nohup, and run 1 process always"

	params_add "processes-count" "pc" "$processesCount" "processesCount" "expect_value" \
			"Number of processes to run"

	params_add "process-heap-size" "hs" "$processHeapSize" "processHeapSize" "expect_value" \
			"The heap size of every Java process (both min and max)"

	params_add "interval-millis" "im" "$intervalMillis" "intervalMillis" "expect_value" \
			"Interval between events (millis)"

	params_add "servers-count" "sc" "$serversCount" "serversCount" "expect_value" \
			"Number of servers"

	params_add "apps-count" "ac" "$appsCount" "appsCount" "expect_value" \
			"Number of apps"

	params_add "hostname-prefix" "host" "$hostnamePrefix" "hostnamePrefix" "expect_value" \
			"A prefix to append to all server names"

	params_add "running-hours" "hours" "$runningHours" "runningHours" "expect_value" \
			"The number of hours this app should be running"

	params_add "running-minutes" "min" "$runningMinutes" "runningMinutes" "expect_value" \
			"The number of minutes this app should be running"

	params_add "jvm-args" "jvm" "$extraJVMArgs" "extraJVMArgs" "expect_value" \
			"JVM arguments to be passed to the application"

	params_add "dry" "d" "$dryRun" "dryRun" "boolean" \
			"Print commands to console, instead of actually running the apps"

	params_add "sleep-seconds" "ss" "$sleepSeconds" "sleepSeconds" "expect_value" \
			"Seconds to wait before starting a new app"

	if ! params_parse_command_line $@; then
		params_usage "Bullshifier run usage:"
		exit 0
	fi
}

function get_deployment_name()
{
	local appName=$1
	local appDataDir=$2
	local deploymentSeed="0"
	
	if [ -r "$appDataDir/DEPLOYMENT_SEED" ]; then
		deploymentSeed=$(cat $appDataDir/DEPLOYMENT_SEED)
	fi
	
	echo "`hostname`-$appName-$deploymentSeed"
}

function run_bullshifiers()
{
	parse_command_line $@
	
	local millisInMinute=60000
		
	if [ -r "APP_TYPE" ]; then
		appType=$(cat APP_TYPE)
	fi
	
	if [ -r "APP_UUID" ]; then
		appUuid=$(cat APP_UUID)
	fi

	if [ "$runInContainer" == "true" ]; then
		processesCount=1
	fi

	if [[ "$intervalMillis" -lt "$millisInMinute" ]]; then
		let exceptionCount="$millisInMinute/$intervalMillis"
		let runningCount="($runningHours*60)+($runningMinutes%60)"
	else
		let exceptionCount="1"
		let MinutesCount="$intervalMillis/$millisInMinute"
		let runningCount="(($runningHours*60)+($runningMinutes%60))/$MinutesCount"
	fi

	if [ "$runningMinutes" == "0" -a "$runningHours" == "0" ];then
		runningCount=1
		exceptionCount=0
	fi

	for ((i=1;i<=$processesCount;i++)); do
		local serverIndex=$(($i%$serversCount))
		local serverName="${hostnamePrefix}$serverIndex"
		local appIndex=$(($i%$appsCount))
		local appName="$appType-$appIndex"
		
		local appDataDir="$emulatorDataDir/$appName"
		mkdir -p "$appDataDir"
		
		local deploymentName=$(get_deployment_name $appName $appDataDir)
		appDataDir="$appDataDir/$deploymentName"
		
		local nameParams="-Dtakipi.server.name=$serverName -Dtakipi.name=$appName -Dtakipi.deployment.name=$deploymentName"
		local javaHeapSize="-Xmx$processHeapSize -Xms$processHeapSize"
		local jarName="$script_dir/build/libs/${appType}.jar"
		local durationPlan="--run-count $runningCount --exceptions-count $exceptionCount --interval-millis $intervalMillis"
		local behaviourPlan="--sticky-path $appDataDir/stack-traces --events-spot $appDataDir/errors"
		local appConfig="--single-thread --hide-stacktraces --warmup-millis 0 --frames-range 50"
		local jvmInternalParams="-XX:CICompilerCount=2 -XX:ParallelGCThreads=1"
		local command="$JAVA_HOME/bin/java $jvmInternalParams -Dapp.uuid=$appUuid $nameParams $javaHeapSize -jar $jarName $durationPlan $behaviourPlan $appConfig"
		
		if [ "$dryRun" == "false" ]; then
			if [ "$runInContainer" == "true" ]; then
				$command
			else
				nohup $command &
				sleep $sleepSeconds
			fi
		else
			echo "nohup $command &"
			echo "sleep $sleepSeconds"
		fi
	done
}

run_bullshifiers $@
