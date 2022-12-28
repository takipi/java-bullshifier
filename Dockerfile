FROM openjdk:8-jdk-slim
LABEL maintainer="support@harness.io"
ARG AGENT_FILENAME=harness.tar.gz

# Install curl
RUN apt-get update

RUN groupadd --gid 1000 harness
RUN adduser --home /opt/harness --uid 1000 --gid 1000 harness

# Run as harness user
USER 1000:1000

# Copy bullshier atrifacts
WORKDIR /opt/harness

# Install the agent
COPY ${AGENT_FILENAME} ./
RUN tar -xvzf ./${AGENT_FILENAME}

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
ENV ET_COLLECTOR_URL=http://collector:6060
ENV IS_DAEMON=true

ENTRYPOINT ["/bin/bash", "./start.sh"]
