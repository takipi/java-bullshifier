FROM openjdk:8-jdk-slim as bulshifier-colors

LABEL maintainer="support@overops.com"

# copy source code to container	
# COPY . ./
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

# install curl
RUN apt-get update && apt-get -y install curl

# Download agent
RUN curl -sL ${AGENT_URL}-${AGENT_VERSION}.tar.gz | tar -xvzf -

FROM openjdk:8-jre-slim

ARG COLOR="yellow"

# COPY --from=bulshifier-colors ./white/build/libs/white.jar ./white/white.jar
# COPY --from=bulshifier-colors ./white/run.sh ./white/run.sh

# COPY --from=bulshifier-colors ./yellow/build/libs/yellow.jar ./yellow/yellow.jar
# COPY --from=bulshifier-colors /yellow/run.sh ./yellow/run.sh

# COPY --from=bulshifier-colors ./red/build/libs/red.jar ./red/red.jar
# COPY --from=bulshifier-colors ./red/run.sh ./red/run.sh

# COPY --from=bulshifier-colors ./black/build/libs/black.jar ./black/black.jar
# COPY --from=bulshifier-colors ./black/run.sh ./black/run.sh

COPY --from=bulshifier-colors /white ./white
COPY --from=bulshifier-colors /yellow ./yellow
COPY --from=bulshifier-colors /red ./red
COPY --from=bulshifier-colors /black ./black
# RUN chmod +x ./black/run.sh ./red/run.sh /yellow/run.sh ./white/run.sh

COPY --from=agent /takipi/ /opt/takipi/

# WORKDIR ${COLOR}

# set default environmental variables
ENV TAKIPI_COLLECTOR_HOST=collector
ENV TAKIPI_COLLECTOR_PORT=6060
ENV IS_DAEMON=true
ENV JAVA_TOOL_OPTIONS=-agentpath:/opt/takipi/lib/libTakipiAgent.so=takipi.debug.logconsole

WORKDIR "${COLOR}"
# CMD ls -l ./java 
ENTRYPOINT ["/bin/bash", "./run.sh", "--processes-count 1"]