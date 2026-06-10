package client;

import java.util.Scanner;

public class InputValidator {
    private final Scanner scanner;

    public InputValidator(Scanner scanner) {    // ссылка на Scanner из CommandReader
        this.scanner = scanner;
    }

    public String readString(String field, boolean canBeNull, boolean nonEmpty) {
        while (true) {
            System.out.print("Введите " + field + ": ");
            String value = scanner.nextLine().trim();
            if (value.isEmpty() && canBeNull) return null;
            if (nonEmpty && value.isEmpty()) {
                System.out.println("Поле не может быть пустым");
                continue;
            }
            return value;
        }
    }

    public Double readDouble(String field, boolean required) {
        while (true) {
            System.out.print("Введите " + field + " (double): ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty() && !required) return null;
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Введите правльное число типа double");
            }
        }
    }

    public int readInt(String field) {
        while (true) {
            System.out.print("Введите " + field + " (int): ");
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Введите корректное целое число");
            }
        }
    }

    public float readFloat(String field, boolean positive, float min) {
        while (true) {
            System.out.print("Введите " + field + ": ");
            try {
                float value = Float.parseFloat(scanner.nextLine().trim());
                if (positive && value <= min) {
                    System.out.println("Значение должно быть больше " + min);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Введите корректное число");
            }
        }
    }

    public long readLongRange(String field, long min, long max) {
        while (true) {
            System.out.print("Введите " + field + " (" + min + "-" + max + "): ");
            try {
                long value = Long.parseLong(scanner.nextLine().trim());
                if (value < min || value > max) {
                    System.out.println("Значение должно быть в диапазоне " + min + "-" + max);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Введите корректное число");
            }
        }
    }

    public <T extends Enum<T>> T readEnum(String field, Class<T> enumClass) {
        while (true) {
            System.out.println("Доступные значения: " +
                    java.util.Arrays.stream(enumClass.getEnumConstants())
                            .map(Enum::name)
                            .reduce((a, b) -> a + ", " + b).orElse(""));
            System.out.print("Введите " + field + ": ");
            String input = scanner.nextLine().trim().toUpperCase();
            try {
                return Enum.valueOf(enumClass, input);
            } catch (IllegalArgumentException e) {
                System.out.println("Неверное значение");
            }
        }
    }
}