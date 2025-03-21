# 1. JDK를 포함한 기본 이미지
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

# 2. Gradle 빌드
COPY . .
RUN chmod +x ./gradlew  # 실행 권한 부여
RUN ./gradlew bootJar

# 3. 런타임 이미지
FROM eclipse-temurin:17-jdk
WORKDIR /app

# 4. 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 5. 애플리케이션 실행
CMD ["java", "-jar", "app.jar"]
