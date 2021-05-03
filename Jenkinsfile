def javaBullshifierVersion = ''
def agentVersion = ''
def fullTag = ''
def tagCheck = ''
def javaBullshifierTags = []
def dockerOptions= '--network=host'
def imageName = 'overops-java-bullshifier'
def localRepoPath = ('docker/' + imageName)

pipeline {

    parameters {
        booleanParam(name: 'FORCE_PUBLISH', defaultValue: false, description: 'Forces a build and publish')
        string(name: 'VERSION', defaultValue: 'latest', description:'Build and publish a specific agent version. Note: Only Full version tag is published if not latest.')
        booleanParam(name: 'PUBLISH_TO_AWS', defaultValue: true, description: 'Publish to AWS Registry')
    }

    environment {
        awsRegCred = 'ecr:us-east-1:aws-takipi-dev-service'
        registryCred = 'container-registry-build-guy'
        gitCred = 'build-guy'
    }

    agent any
    stages {
        stage('Cloning Git') {
            steps {
                git([url: 'https://github.com/takipi/java-bullshifier.git', branch: 'master', credentialsId: gitCred ])
            }
        }

     stage('Determine versions and tags') {
          environment {
                LOCAL_REGISTRY_CREDS = credentials("${registryCred}")
            }

            steps { 
                script{
                    // Determine the Java Bullshifier Version (Reads local VERSION file)
                    javaBullshifierVersion = sh(returnStdout: true, script: 'python3 ./scripts/version-support.py --get-version').trim()

                    // Determine the latest agent version and add latest tags. otherwise only use the agent parameter.
                    // Note: When setting the Agent Version as param only the full tag is pushed.
                    if ( params.AGENT_VERSION == 'latest' ) {
                        agentVersion = sh(returnStdout: true, script: 'python3 ./scripts/version-support.py --get-agent-version').trim()
                        javaBullshifierTags.add('latest')
                        javaBullshifierTags.add(javaBullshifierVersion)
                    } else {
                        agentVersion = params.AGENT_VERSION
                    }

                    // Add full unique tag i.e. 2.13-agent-4.54.0
                    fullTag = (javaBullshifierVersion + '-agent-' + agentVersion)
                    javaBullshifierTags.add(fullTag)

                    // Determine if the tag doesn't exists if not we should build and publish.
                    registryAPIEndpoint = (env.LOCAL_DOCKER_REGISTRY_URL + '/artifactory/api/docker/docker')
                    tagCheck = sh(returnStdout: true, script:"python3 ./scripts/version-support.py --check-docker-tag --tag ${fullTag} --repository ${imageName} --registry ${registryAPIEndpoint} --username \$LOCAL_REGISTRY_CREDS_USR --token \$LOCAL_REGISTRY_CREDS_PSW").trim()
                }
            }
        }

        stage('Build') {
            when {
                anyOf {
                    // Run Build if forced or if the tag does not exists.
                    expression { return params.FORCE_PUBLISH }
                    expression { tagCheck == 'false' }
                }
            }

            steps {
                script {
                    options = ''

                    // Build using the latest agent or one passed in as a parameter.
                    if ( params.AGENT_VERSION == 'latest' ) {
                        options = (dockerOptions + ' .')
                    } else {
                        options = ( dockerOptions + ' --build-arg VERSION=' + params.AGENT_VERSION + ' .')
                    }

                    // Note: "building" two images in order to have correct image name for each registry
                    //       Second call is no-op but is done this way based on the docker plugin api limitations.
                    dockerImage = docker.build( localRepoPath, options )
                    awsDockerImage = docker.build( imageName, options )
                }
            }
        }

        stage('Publish Image') {
            when {
                anyOf {
                    // Run Build if forced or if the tag does not exists.
                    expression { return params.FORCE_PUBLISH }
                    expression { tagCheck == 'false' }
                }
            }

            steps {
                script {
                    docker.withRegistry(env.LOCAL_DOCKER_REGISTRY_URL, registryCred ) {
                            for(String tag in javaBullshifierTags) {
                                 echo(tag)
                                dockerImage.push(tag)
                            }
                    }

                    // Publish image to private ECR
                    if (params.PUBLISH_TO_AWS) {
                        docker.withRegistry(env.AWS_TAKIPI_DEV_REGISTRY_URI, awsRegCred ) {
                            for(String tag in javaBullshifierTags) {
                                awsDockerImage.push(tag)
                            }
                        }
                    }
                }
            }
        }
    }
}
