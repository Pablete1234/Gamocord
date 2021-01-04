FROM maven:3.6.3-openjdk-11-slim as BUILDER

ENV BUILD_ROOT /build

# Setting up build project files
RUN mkdir -p $BUILD_ROOT
WORKDIR $BUILD_ROOT

COPY . .

# Building Gamocord
RUN mvn -q clean install
RUN mv target/Gamocord*.jar /tmp/gamocord.jar

# Reseting the image build with a JLink-prepared Alpine Linux
FROM adoptopenjdk/openjdk11:alpine-jre

# Creating the runner user
RUN addgroup -g 1000 gamocord && \
    adduser -u 1000 -h /gamocord -D -G gamocord gamocord

WORKDIR /gamocord

USER gamocord

# Copying files from BUILDER step
COPY --from=BUILDER --chown=gamocord:gamocord /tmp/gamocord.jar ./

# Final settings
CMD ["java", "-jar", "gamocord.jar"]
