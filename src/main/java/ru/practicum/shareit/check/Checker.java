package ru.practicum.shareit.check;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Checker {
    public static void checkFromAndSize(int from, int size) {
        if (from < 0 || size <= 0) {
            throw new IllegalArgumentException("Валидация значений для пагинации не пройдена.");
        }
    }
}
