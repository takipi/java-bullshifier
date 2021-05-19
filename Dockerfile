FROM openjdk:8-jdk-slim as bulshifier-colors

LABEL maintainer="support@overops.com"

# Copy source code to container	
COPY ./gradle ./gradle.sh
COPY ./src ./src
COPY ./gradlew ./gradlew
COPY ./gradle ./gradle
COPY ./build.gradle ./build.gradle

COPY ./examples/white.sh ./examples/white.sh 
COPY ./examples/yellow.sh ./examples/yellow.sh
COPY ./examples/red.sh ./examples/red.sh
COPY ./examples/black.sh ./examples/black.sh
COPY ./examples/colors.sh ./examples/colors.sh 

RUN chmod +x ./examples/*.sh

# Create bulshifier jars
RUN ["/bin/bash", "./examples/white.sh"]
RUN ["/bin/bash", "./examples/yellow.sh"]
RUN ["/bin/bash", "./examples/red.sh"]
RUN ["/bin/bash", "./examples/black.sh"]

FROM openjdk:8-jre-slim as agent

LABEL maintainer="support@overops.com"

ARG AGENT_VERSION=latest
ARG AGENT_URL=https://s3.amazonaws.com/app-takipi-com/deploy/linux/takipi-agent

# Install curl
RUN apt-get update && apt-get -y install curl

# Download agent
RUN curl -sL ${AGENT_URL}-${AGENT_VERSION}.tar.gz | tar -xvzf -

FROM openjdk:8-jre-slim

RUN groupadd --gid 1000 overops
RUN adduser --home /opt/overops --uid 1000 --gid 1000 overops

# Copy bullshier atrifacts 
WORKDIR /opt/overops

COPY --from=bulshifier-colors  --chown=1000:1000 /white ./white
COPY --from=bulshifier-colors  --chown=1000:1000 /yellow ./yellow
COPY --from=bulshifier-colors  --chown=1000:1000 /red ./red
COPY --from=bulshifier-colors  --chown=1000:1000 /black ./black

# Copy takipi agent 
COPY --from=agent --chown=1000:1000 /takipi/ ./agent

# Copy the start script to container
COPY  --chown=1000:1000 ./scripts/start.sh ./start.sh
RUN chmod u+x ./start.sh

# Install procps for ps command
RUN apt-get update && apt-get install -y procps

# Run as overops user
USER 1000:1000 

# set default environmental variables
ENV INERVAL_MILLIS=300
ENV RUNNING_DURATION_HOURS=0
ENV RUNNING_DURATION_MINUTES=0
ENV COLOR=white
ENV TAKIPI_COLLECTOR_HOST=collector
ENV TAKIPI_COLLECTOR_PORT=6060
ENV IS_DAEMON=true
ENV JAVA_TOOL_OPTIONS=-agentpath:/opt/overops/agent/lib/libTakipiAgent.so=takipi.debug.logconsole

ENTRYPOINT ["/bin/bash", "./start.sh"]
