# 소스 코드 컴파일, 실행 가능한 .jar 만들기
FROM gradle:8.5-jdk21 AS build
WORKDIR /home/gradle/src

# 소스 코드를 컨테이너 안으로 복사
COPY --chown=gradle:gradle . .

RUN gradle build -x test --no-daemon

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# build 단계의 .jar 파일만 app.jar로 복사
COPY --from=build /home/gradle/src/build/libs/*-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]