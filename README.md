# Банковская система управления картами (REST API)

Управление банковскими картами с поддержкой ролей, безопасности, перевода средств и аудита. Реализовано на основе Spring Boot, JWT, PostgreSQL и Docker.

## Описание проекта

Система предоставляет REST API для управления банковскими картами с поддержкой двух ролей: ADMIN и USER.

### Основные функции
- Администратор (ADMIN):
  - Создание, блокировка, активация и удаление карт
  - Управление пользователями (активация/деактивация, назначение ролей)
  - Просмотр всех карт и заявок на действия с картами
  - Утверждение или отклонение запросов на блокировку карт

- Пользователь (USER):
  - Просмотр своих карт (с пагинацией и сортировкой)
  - Запрос на блокировку своей карты
  - Перевод средств между своими картами
  - Просмотр общего баланса

## Атрибуты карты
- Номер карты: Зашифрован в БД, отображается как **** **** **** 1234
- Владелец: Имя владельца
- Срок действия: Месяц/год
- Статус: ACTIVE, BLOCKED, EXPIRED
- Баланс: Денежный баланс

## Технологии
- Java 17+
- Spring Boot (Web, Security, Data JPA)
- JWT для аутентификации
- PostgreSQL (в Docker)
- Liquibase — миграции БД
- Docker & Docker Compose
- Swagger UI / OpenAPI 3
- Шифрование номеров карт (AES)

## Безопасность
- Аутентификация и авторизация через Spring Security + JWT
- Ролевой доступ: ADMIN и USER
- Шифрование номеров карт в базе
- Маскирование номеров в API

## API
Документация доступна через Swagger UI после запуска:
http://localhost:8080/swagger-ui.html

Также доступна OpenAPI спецификация: docs/openapi.yaml

## Запуск приложения

1. Клонируйте репозиторий:

```bash
git clone https://github.com/Esternit/Bank_REST.git
cd Bank_REST
```

2. Настройте переменные окружения:

```bash
cp .env.example .env
```
Отредактируйте .env:
```env
POSTGRES_DB=bank_db
POSTGRES_USER=bank_user
POSTGRES_PASSWORD=bank_pass
JWT_SECRET=your_jwt_secret_key_here
ENCRYPTION_SECRET=your_encryption_secret_32_chars
```

Убедитесь, что JWT_SECRET и ENCRYPTION_SECRET длинные (рекомендуется 32+ символа).

3. Запустите через Docker:
```bash
docker-compose up -d --build
```

Приложение будет доступно:
- API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html

4. Аутентификация
Используйте:
POST /api/auth/login
{
  "username": "admin",
  "password": "Elaq7er1glh2"
}

Поддерживаются:
- admin (роль ADMIN)
- user (роль USER)
Пароль: Elaq7er1glh2

Полученный JWT-токен вставьте в Swagger как Bearer токен.

## Запуск тестов
```bash 
mvn test
```

## Поддержка
По вопросам — обращайтесь к автору.

Готово к использованию в dev-среде. Подходит для демонстрации и расширения.

Проект: Bank_REST
Автор: Esternit
Дата: 2025