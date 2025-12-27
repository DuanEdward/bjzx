# 多阶段构建：Maven 构建阶段
FROM maven:3.8-openjdk-8 AS builder

WORKDIR /app

# 复制 pom.xml 和源代码
COPY pom.xml .
COPY src ./src

# 构建项目（跳过测试以加快构建速度）
RUN mvn clean package -DskipTests

# 生产阶段：运行 Spring Boot 应用
FROM openjdk:8-jre-slim

WORKDIR /app

# 创建非 root 用户
RUN groupadd -r appuser && useradd -r -g appuser appuser

# 从构建阶段复制 jar 文件
COPY --from=builder /app/target/*.jar app.jar

# 创建必要的目录
RUN mkdir -p /app/uploads /app/logs && \
    chown -R appuser:appuser /app

# 切换到非 root 用户
USER appuser

EXPOSE 8080

# 设置默认 JVM 参数（可通过环境变量覆盖）
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]

