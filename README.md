# Iricom Backend

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

## REST API Document

Spring REST Docs

{Iricom host}/docs/index.html