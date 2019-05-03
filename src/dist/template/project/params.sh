#!/bin/bash

declare -A params_list
declare -A params_var
declare -A params_desc
declare -A params_short_to_long
declare -A params_short
declare -A params_type
declare -A params_default
declare -A params_skipped_list

declare -rA params_types=([boolean]="boolean" [expect_value]="expectValue")

function params_add()
{
	local long_name=$1
	local short_name=$2
	local default_value=$3
	local variable_name=$4
	local param_type=$5
	local description=$6
	
	[ -z "$short_name" ] && $short_name=$long_name
	[ -z "$long_name" ] && $long_name=$short_name
	
	if [ -z "$long_name" ]; then
		echo "Parameter name name is missing"
		return 1
	fi
	
	if [ -z "$variable_name" ]; then
		echo "Variable name is missing"
		return 1
	fi
	
	params_list[$long_name]="exists"
	params_var[$long_name]=$variable_name
	params_desc[$long_name]=$description
	params_type[$long_name]=$param_type
	params_short[$long_name]=$short_name
	params_default[$long_name]=$default_value

	params_short_to_long[$short_name]=$long_name

	declare -g $variable_name="$default_value"
	
	return 0
}

function params_parse_command_line()
{
# return 0 upon success, 1 if help 

	while true; do
		if [ -z "$1" ]; then
			break
		fi
		
		if [ "$1" == "--help" -o "$1" == "-h" ]; then
			return 1
		fi
		
		local param_name
		
		if [[ $1 == --* ]]; then
			param_name=${1/--/}
			[ -n ${params_list[$param_name]} ] || param_name=${params_short_to_long[$param_name]}
			shift 1
		elif [[ $1 == -* ]]; then
			local param_short_name=${1/-/}
			param_name=${params_short_to_long[$param_short_name]}
			[ -n "$param_name" ] || param_name=$param_short_name
			shift 1
		else
			params_skipped_list[$1]="skipped"
			shift 1
			continue
		fi
		
		local param_type=${params_type[$param_name]}
		
		if [ -z $param_type ]; then
			echo "Skipping parameter: $param_name, no such option"
			continue
		fi

		local variable_name=${params_var[$param_name]}
		
	 	if [ -n "$variable_name" ]; then
	 		case $param_type in
	 			${params_types[boolean]})
	 				declare -g $variable_name="true"
	 			;;
	 			expect_value)
					echo "Param: $param_name = $1"
					declare -g $variable_name=$1
					shift 1
				;;
	 			*)
				;;
			esac
	 	fi
	done
}

function params_usage()
{
	local name
	local short_form
	local initial_line=$1
	
	if [ -n "$initial_line" ]; then
		printf "\n"
		printf "$initial_line\n"
		printf "\n"
	fi
	
	printf "\t  %-20s  %-6s %-10s %s\n" \
			"Name" " "  "Default" "Description"
	
	printf "\t  %-20s  %-6s %-10s %s\n" \
			"¯¯¯¯              " "        "  "¯¯¯¯¯¯¯   " "¯¯¯¯¯¯¯¯¯¯¯"
				
	for name in "${!params_list[@]}"; do
		short_form=${params_short[$name]}
		
		printf "\t--%-20s -%-6s %-10s %s\n" \
				"${name}" "${short_form}"  "${params_default[$name]}" "${params_desc[$name]}"
	done
	
	printf "\n"
}
