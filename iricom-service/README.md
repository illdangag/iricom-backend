# Iricom Backend

[![Build, Test](https://github.com/illdangag/iricom-backend/actions/workflows/pre-production-build-test.yml/badge.svg)](https://github.com/illdangag/iricom-backend/actions/workflows/pre-production-build-test.yml)

---

## Setting

### firebase

firebase의 비공개 키를 `resources/firebase-adminsdk.json`에 위치

### VM Option
```
-Dspring.datasource.url=jdbc:mariadb://{HOST}:{PORT}/{DATABASE}
-Dspring.datasource.username={USERNAME}
-Dspring.datasource.password={PASSWORD}
```

## Build

```shell
./gradlew build

./gradlew bootJar
```

## Run

```shell
java \
-Dspring.datasource.url=jdbc:mariadb://{HOST}:{PORT}/{DATABASE} \
-Dspring.datasource.username={USERNAME} \
-Dspring.datasource.password={PASSWORD} \
-jar ./iricom-backend-0.0.0.jar

```

## REST API Document

Spring REST Docs

{Iricom host}/docs/index.html