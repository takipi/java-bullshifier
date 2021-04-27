#!/usr/bin/env python3

import argparse
import os
import sys
import requests


class VersionSupportCLI():

    def __init__(self):
        self.parser = argparse.ArgumentParser()

        # Commands: Each will return a value to stdout for JenkinsPipeline
        self.parser.add_argument('--get-agent-version', action="store_true", dest="get_agent_version")
        self.parser.add_argument('--get-version', action="store_true", dest="get_version")
        self.parser.add_argument('--check-docker-tag', action="store_true", dest="check_docker_tag")

        # Additional Options:
        self.parser.add_argument('--tag', help='Docker Tag to Check for if exists')
        self.parser.add_argument('--registry', default='dockerhub')
        self.parser.add_argument('--repository', default='overops-java-bullshifier')
        self.parser.add_argument('--username')
        self.parser.add_argument('--token')


        rootdir, _ = os.path.split(os.path.dirname(os.path.realpath(__file__)))
        pass

    def run(self):

        args = self.parser.parse_args()
        result = ""

        try:
            if args.get_version:
                result = self.get_version()
                pass
            elif args.get_agent_version:
                result = self.get_latest_hosted_agent_version()
                pass
            elif args.check_docker_tag:
                if args.registry == 'dockerhub':
                    result = self.check_docker_hub_tags( args.repository, args.tag )
                else:
                    result = self.check_local_docker_tags( args.registry, args.repository, args.username, 
                               args.token, args.tag)
                pass
        except Exception as e:
            # Whatever went wrong exit with an error code.
            sys.exit(1)

        print(result.strip())
        pass

    '''
    Pulls the version from the POM.
    '''
    def get_version(self):
        return "2.0.0"
    '''
    Pulls the latest agent version from public hosted site.
    '''
    def get_latest_hosted_agent_version(self):
        res = requests.get("https://s3.amazonaws.com/app-takipi-com/deploy/linux/takipi-agent-latest-version")
        return res.text

    '''
    Checks a given tag against docker hub using no auth. Based on V1 Docker registry spec.

    returns: true if tag is found.
    '''
    def check_docker_hub_tags(self, repository_name, tag):

        r = requests.get(f"https://registry.hub.docker.com/v1/repositories/overops/{repository_name}/tags")
        
        for info in r.json():
            if info["name"] == tag:
                return 'true'

        return 'false'        

    '''
    Checks a given tag against a local artifactory registry. Output from this command is described here:

    https://www.jfrog.com/confluence/display/JCR6X/JFrog+Container+Registry+REST+API#JFrogContainerRegistryRESTAPI-ListDockerTags

    returns: true if tag is found.
    '''
    def check_local_docker_tags(self, registry_url, repository_name, username, token, tag):

        url = f"{registry_url.rstrip('/')}/v2/{repository_name}/tags/list"
        r = requests.get(url, auth=(username, token))

        for t in r.json()['tags']:
            if t == tag:
                return 'true'

        return 'false'

if __name__ == "__main__":
    cli = VersionSupportCLI()
    cli.run()
    pass