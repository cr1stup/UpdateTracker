![Build](https://github.com/central-university-dev/backend-academy-2025-spring-template/actions/workflows/build.yaml/badge.svg)

# Update Tracker

<!-- этот файл можно и нужно менять -->

Проект сделан в рамках курса Академия Бэкенда.

Приложение для отслеживания обновлений контента по ссылкам.
При появлении новых событий отправляется уведомление в Telegram.

Проект написан на `Java 23` с использованием `Spring Boot 3`.

Проект состоит из 2-х приложений:
* Bot
* Scrapper

Для работы требуется БД `PostgreSQL`. Используется кэширование с `Redis`, присутствует опциональная зависимость на `Kafka`.

Для дополнительной справки: [HELP.md](./HELP.md)

Инструкция по запуску: [MANUAL.md](./MANUAL.md)
