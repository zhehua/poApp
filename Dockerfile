#FROM maven:3.6.0-jdk-8-alpine AS MAVEN_BUILD
#RUN mvn package
## 使用java环境
#FROM openjdk:8-jre
## 缓存目录
##VOLUME tmp
## 将当前项目的jar包添加到容器中
#EXPOSE 8080
#COPY --from=MAVEN_BUILD "target/poApp-1.0-SNAPSHOT.jar" "poApp.jar"
## 当容器启动时 执行启动命令
#ENTRYPOINT ["java","-jar","poApp.jar"]

FROM maven as build
WORKDIR /code
COPY . /code/
RUN mvn package

FROM openjdk:8-jre
EXPOSE 8080
WORKDIR /app
COPY --from=build /code/target/poApp-1.0-SNAPSHOT.jar .
CMD java -jar poApp-1.0-SNAPSHOT.jar
