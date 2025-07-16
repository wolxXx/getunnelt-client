FROM ubuntu:22.04

# Basis Tools
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    wine64 \
    cabextract \
    unzip \
    wget \
    curl \
    xz-utils \
    rpm \
    fakeroot \
    dpkg \
    gnupg \
    git \
    build-essential \
    python3 \
    git

# WiX Toolset installieren (über Wine)
RUN wget https://github.com/wixtoolset/wix3/releases/download/wix3112rtm/wix311-binaries.zip \
 && unzip wix311-binaries.zip -d /wix \
 && rm wix311-binaries.zip

ENV PATH="/wix:${PATH}"

# Java-Umgebungsvariablen
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH="$JAVA_HOME/bin:$PATH"

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew
