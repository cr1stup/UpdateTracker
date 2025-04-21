package backend.academy.bot.handler.state.model;

import lombok.Getter;

@Getter
public enum Mode {
    IMMEDIATE("immediate", "отправка уведомлений сразу, для установки введите immediate"),
    DAILY("daily", "отправка уведомлений в заданное время, для установки введите daily <hh:mm>");

    private final String modeName;
    private final String description;

    Mode(String modeName, String description) {
        this.modeName = modeName;
        this.description = description;
    }

    public static String getInstructionsOfInputMode() {
        StringBuilder instructions = new StringBuilder();

        instructions.append("Режимы отправки уведомлений:").append("%n");
        for (Mode mode : Mode.values()) {
            instructions.append(mode.modeName()).append(" - ").append(mode.description()).append("%n");
        }

        return instructions.toString().formatted();
    }
}
