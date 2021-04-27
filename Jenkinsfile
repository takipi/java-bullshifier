javaBullshifierVersion = ''
agentVersion = ''
fullTag = ''
tagCheck = ''
javaBullshifierTags = ["test"]
dockerOptions= '--network=host'
// imageName='overops/java-bullshifier'
imageName='hodik1012/java-bullshifier'

pipeline {

    parameters {
        booleanParam(name: 'FORCE_PUBLISH', defaultValue: false, description: 'Forces a build and publish')
        string(name: 'AGENT_VERSION', defaultValue: 'latest', description:'Build and publish a specific agent version. Note: Only Full version tag is published if not latest.')
    }

    environment {
        registryCred = 'hod-docker-hub'
    }

    agent any
    stages {
        stage('Cloning Git') {
            steps {
                git([url: 'https://github.com/takipi/java-bullshifier', branch: 'feature/OO-11642/deploy-java-bullshifier-with-jenkins'])
            }
        }

        stage('Determine versions and tags') {
            steps {
                script{
                    // Determine the Java Bullshifier Version
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
                    tagCheck = sh(returnStdout: true, script:"python3 ./scripts/version-support.py --check-docker-tag --repository event-generator --tag ${fullTag}").trim()
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
                        options = ( dockerOptions + ' --build-arg AGENT_VERSION=' + params.AGENT_VERSION + ' .')
                    }

                    dockerImage = docker.build( imageName, options )
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
                    // Publish to Docker Hub
                    docker.withRegistry('', registryCred ) {
                        for(String tag in javaBullshifierTags) {
                            dockerImage.push(tag)
                        }
                    }
                }
            }
        }
    }
}
