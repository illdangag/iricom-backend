# Iricom Backend

[![Build, Test](https://github.com/illdangag/iricom-backend/actions/workflows/pre-production-build-test.yml/badge.svg)](https://github.com/illdangag/iricom-backend/actions/workflows/pre-production-build-test.yml)

## Build

```shell
./gradlew clean build bootJar -Pprofile=local -Pstorage=file

./gradlew clean build bootJar -Pprofile=pre-production -Pstorage=s3
```
