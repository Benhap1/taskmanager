# Task Management 

## Описание проекта

Task Management  — это простая система управления задачами, разработанная на языке Java с использованием Spring Boot. Система предоставляет возможность создавать, редактировать, удалять и просматривать задачи, а также оставлять комментарии к ним. Каждая задача содержит заголовок, описание, статус, приоритет, автора и исполнителя. Проект реализует только API.

## Функциональные возможности

- **Аутентификация и авторизация пользователей**: Пользователи могут регистрироваться и входить в систему с использованием email и пароля.
- **Управление задачами**: Пользователи могут создавать задачи, редактировать их, удалять, просматривать, менять статус и назначать исполнителей.
- **Просмотр задач**: Пользователи могут просматривать задачи других пользователей.
- **Комментарии**: Возможность оставлять комментарии к задачам.
- **Фильтрация и пагинация**: API поддерживает фильтрацию и пагинацию при получении задач и комментариев.
- **JWT-аутентификация**: Доступ к API защищен с использованием JWT токенов.
- **Документация API**: В проекте настроен Swagger UI для удобного просмотра и тестирования API.

## Технологии и зависимости

- **Java 17**
- **Spring Boot 3.3.2**
- **Spring Security**
- **Spring Data JPA**
- **JWT (JSON Web Tokens)**
- **Hibernate**
- **MySQL**
- **Swagger/OpenAPI**
- **Docker & Docker Compose**

## Установка и запуск проекта

### 1. Клонирование репозитория

```bash
git clone https://github.com/Benhap1/taskmanager
cd C:\Users
```
### 2. Запуск проекта при помощи Docker
Для запуска проекта локально выполните следующую команду:
 ```bash
    docker-compose up --build
 ```

### 3. Настройка базы данных MYSQL
Прошу поменять порты если заняты. Также до запуска приложения необходимо проверить наличие базы данных taskmanagement либо создать ее самостоятельно. 

### 4. Swagger
Ссылка тут: http://localhost:8080/swagger-ui/index.html
 
Для авторизации пользователя выполните следующий запрос:

#### Регистрация пользователя

- **Метод:** `POST`
- **URL:** `/users/register`

#### Пример тела запроса:

```json
{
  "id": 1,
  "email": "user1@gmail.com",
  "password": "password1",
  "role": "AUTHOR"
}
```

#### Аунтентификация пользователя:

- **Метод:** `POST`
- **URL:** `/users/login`

#### Пример тела запроса:

```json
{
  "id": 1,
  "email": "user1@gmail.com",
  "password": "password1",
  "role": "AUTHOR"
}
```
В случае Status 200 в Response body будет получен JWT-токен, который необходимо будет вставить в поле Value.
Это поле вы найдете сверху справа в Authorize bearerAuth  (http, Bearer) интерфейса Swagger.

### 4. Наполнение базы данных
После аутентификации появиться возможность тестировать запросы.

