#############################################################
#               STAGE 1 - BUILD THE APPLICATION
#############################################################

# Use an official Maven image that already contains
# Maven + Java 21.
#
# We use this image ONLY to compile the project.
#
# "AS build" gives this stage a name so that we can
# reference it later.
FROM maven:3.9.9-eclipse-temurin-21 AS build


# Docker containers have their own filesystem.
# Everything from now on happens inside /app.
WORKDIR /app


#############################################################
# Copy only files required for dependency download first.
#
# Docker caches layers.
#
# If pom.xml doesn't change, Maven dependencies will not
# be downloaded again on every build.
#############################################################

COPY pom.xml .

COPY .mvn .mvn

COPY mvnw .


#############################################################
# Linux containers don't automatically make scripts executable.
#
# This command gives execute permission to mvnw.
#############################################################

RUN chmod +x mvnw


#############################################################
# Download all Maven dependencies.
#
# Because source code isn't copied yet,
# Docker can cache this entire step.
#
# Result:
# Future builds become MUCH faster.
#############################################################

RUN ./mvnw dependency:go-offline


#############################################################
# Now copy the actual Java source code.
#############################################################

COPY src src


#############################################################
# Compile the application.
#
# clean
#   -> removes previous build files
#
# package
#   -> creates executable Spring Boot jar
#
# -DskipTests
#   -> skips unit tests during deployment
#############################################################

RUN ./mvnw clean package -DskipTests



#############################################################
#              STAGE 2 - RUN THE APPLICATION
#############################################################

# This image contains ONLY Java Runtime.
#
# Notice:
# No Maven.
# No compiler.
#
# This keeps the final Docker image much smaller.
FROM eclipse-temurin:21-jre


#############################################################
# Working directory inside runtime container.
#############################################################

WORKDIR /app


#############################################################
# Copy ONLY the generated jar from the previous stage.
#
# We DO NOT copy:
# - source code
# - Maven cache
# - pom.xml
#
# That's why this is called a Multi-Stage Build.
#############################################################

COPY --from=build /app/target/*.jar app.jar


#############################################################
# Spring Boot listens on 8080 inside the container.
#
# This does NOT actually publish the port.
#
# It simply documents that this container
# expects traffic on 8080.
#############################################################

EXPOSE 8080


#############################################################
# Command executed when container starts.
#
# Equivalent to:
#
# java -jar app.jar
#############################################################

ENTRYPOINT ["java", "-jar", "app.jar"]