# Используем официальный образ OpenJDK в качестве базового образа
FROM openjdk:17-jdk

# Создаем директорию для нашего приложения
WORKDIR /app

# Копируем файл JAR в контейнер
COPY target/taskmanagement-0.0.1-SNAPSHOT.jar app.jar

# Указываем команду для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
