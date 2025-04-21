package backend.academy.scrapper.service.model;

import backend.academy.scrapper.exception.UnknownModeException;
import lombok.Getter;

@Getter
public enum Mode {
    IMMEDIATE("immediate"),
    DAILY("daily");

    private final String modeName;

    Mode(String modeName) {
        this.modeName = modeName;
    }

    public static Mode fromString(String modeName) {
        for (Mode mode : Mode.values()) {
            if (mode.modeName.equalsIgnoreCase(modeName)) {
                return mode;
            }
        }
        throw new UnknownModeException();
    }
}
