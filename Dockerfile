FROM openjdk:8-jdk-slim
LABEL maintainer="support@overops.com"

# Install curl
RUN apt-get update && apt-get -y install curl

RUN groupadd --gid 1000 overops
RUN adduser --home /opt/overops --uid 1000 --gid 1000 overops

# Run as overops user
USER 1000:1000

# Copy bullshier atrifacts
WORKDIR /opt/overops

# Download agent
ARG AGENT_VERSION=latest
ARG AGENT_URL=https://s3.amazonaws.com/app-takipi-com/deploy/linux/takipi-agent
RUN curl -sL ${AGENT_URL}-${AGENT_VERSION}.tar.gz | tar -xvzf -

# Copy source code to container
COPY --chown=1000:1000 ./gradle ./gradle.sh
COPY --chown=1000:1000 ./src ./src
COPY --chown=1000:1000 ./gradlew ./gradlew
COPY --chown=1000:1000 ./gradle ./gradle
COPY --chown=1000:1000 ./build.gradle ./build.gradle
COPY --chown=1000:1000 ./examples/*.sh ./examples/
COPY --chown=1000:1000 ./scripts/*.sh ./

# Precompile Colors
RUN ["/bin/bash", "./examples/white.sh"]
RUN ["/bin/bash", "./examples/yellow.sh"]
RUN ["/bin/bash", "./examples/red.sh"]
RUN ["/bin/bash", "./examples/black.sh"]

# Change Permissions (Windows Build Support)
RUN chmod u+x *.sh
RUN chmod u+x examples/*.sh

# Default Environmental Variables
ENV INERVAL_MILLIS=300
ENV RUNNING_DURATION_HOURS=0
ENV RUNNING_DURATION_MINUTES=5
ENV COLOR=white
ENV TAKIPI_COLLECTOR_HOST=collector
ENV TAKIPI_COLLECTOR_PORT=6060
ENV IS_DAEMON=true

ENTRYPOINT ["/bin/bash", "./start.sh"]
