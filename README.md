# ShareIt

## Обзор

ShareIt - это сервис для совместного использования предметов, теперь улучшенный и дополненный новыми функциями. Пользователи могут делиться своими вещами, бронировать предметы, оставлять отзывы и просматривать отзывы других пользователей. Приложение теперь поддерживает работу с базой данных через Hibernate и JPA.

## Основные функции

- **Добавление предмета**: Пользователи могут добавлять предметы для совместного использования, указывая название, описание и статус доступности предмета.

- **Поиск предмета**: Пользователи могут искать доступные предметы для аренды. Если нужный предмет не найден, пользователь может оставить запрос на предмет.

- **Бронирование предмета**: Пользователи могут бронировать предмет на определенные даты. Владелец предмета должен подтвердить бронирование.

- **Отзывы**: После использования предмета пользователи могут оставлять отзывы, благодаря которым другие пользователи могут узнать больше о предмете и его владельце.

- **Просмотр бронирований**: Пользователи могут просматривать текущие, будущие, завершенные, ожидающие подтверждения и отклоненные бронирования.

- **Просмотр отзывов**: Пользователи могут просматривать отзывы о предметах.

## Структура проекта

Проект включает в себя контроллеры, сервисы, репозитории и сущности, аннотированные для работы с базой данных через JPA.

- **Контроллеры**: `UserController`, `ItemController` и `BookingController` обрабатывают запросы HTTP и возвращают ответы HTTP.

- **Сервисы**: `UserService`, `ItemService` и `BookingService` обрабатывают основную логику приложения.

- **Репозитории**: `UserRepository`, `ItemRepository`, `BookingRepository` и `CommentRepository` обеспечивают хранение данных в базе данных.

- **Сущности**: `User`, `Item`, `Booking` и `Comment` представляют собой модели данных, аннотированные для работы с Hibernate и JPA.

## Стэк технологий

### Бэкенд
1. **Java** - основной язык программирования для разработки бэкенд-части приложения.
2. **Spring Boot** - фреймворк для быстрой разработки приложений на Java с минимумом конфигурации.
3. **Hibernate** - ORM фреймворк для работы с базой данных.
4. **JPA (Java Persistence API)** - спецификация Java для управления реляционными данными в приложениях Java.
5. **PostgreSQL** - реляционная система управления базами данных для хранения и обработки данных.
6. **Spring Data JPA** - библиотека, облегчающая работу с JPA, автоматизируя реализацию репозиториев и предоставляя множество полезных функций.

### Дополнительные инструменты
1. **Git** - система контроля версий для отслеживания изменений в исходном коде проекта.
2. **Maven** - инструмент для автоматизации сборки проектов на Java.