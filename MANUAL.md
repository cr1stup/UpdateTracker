### Как запустить приложение и проверить его работу

1. Склонируйте репозиторий

2. Откройте проект и задайте значения переменных для токенов [telegram](https://docs.radist.online/radist.online-docs/nashi-produkty/radist-web/podklyucheniya/telegram-bot/instrukciya-po-sozdaniyu-i-nastroiki-bota-v-botfather) и [gitbub](https://www.geeksforgeeks.org/how-to-generate-personal-access-token-in-github/):

   Run -> Edit Configuration -> Modify Options -> Enable Environment variables

   для BotApplication - TELEGRAM_TOKEN        
   для ScrapperApplication - GITHUB_TOKEN

3. Запустите [compose.yml](compose.yml)

4. Запустите ScrapperApplication

5. Запустите BotApplication

6. Проверьте отслеживание обновлений:

- создайте публичный репозиторий на github с README.md файлом
- отправьте в бот команду /start, затем /track и следуйте его инструкциям, сохранив ссылку на ваш репозиторий
- сделайте любой коммит или создайте новый issue и бот пришлет сообщение об обновлении в течение 10 секунд

