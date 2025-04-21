package backend.academy.bot.util;

public interface BotMessages {
    String UNKNOWN_COMMAND = "неизвестная команда";

    String START_NAME = "/start";
    String START_DESCRIPTION = "регистрация пользователя";
    String START_HANDLE =
            """
        Добро пожаловать в бот для отслеживания обновлений по ссылкам!
        Введите /help для просмотра доступных команд
        """;

    String HELP_NAME = "/help";
    String HELP_DESCRIPTION = "список команд";

    String TRACK_NAME = "/track";
    String TRACK_DESCRIPTION = "начать отслеживать ссылку";

    String UNTRACK_NAME = "/untrack";
    String UNTRACK_DESCRIPTION = "удалить ссылку из отслеживаемых";

    String LIST_NAME = "/list";
    String LIST_DESCRIPTION = "вывести список отслеживаемых ссылок";

    String TAGLIST_NAME = "/tag";
    String TAGLIST_DESCRIPTION = "просмотр тэгов и ссылок по тэгам";
    String TAGLIST_GREATING =
            "Введите [all] для просмотра всех тэгов или конктретный тэг для просмотра всех привязванных ссылок";

    String SETMODE_NAME = "/setmode";
    String SETMODE_DESCRIPTION = "настроить время отправки уведомлений";
    String SETMODE_SUCCESS = "новый режим успешно установлен";

    String EMPTY_LIST = "список отслеживаемых ссылок пуст";

    String UPDATE_MESSAGE = "Обновление по вашей ссылке";
}
