package client;

import shared.command.*;
import shared.network.Request;
import shared.network.Response;
import shared.utility.PasswordUtil;

import java.io.*;
import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class ClientApp {
    private static final Set<String> executedScripts = new HashSet<>();
    private static String CURRENT_USER = null;
    private static String CURRENT_HASH = null;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ClientNetworkHandler network = new ClientNetworkHandler();
        CommandReader reader = new CommandReader(scanner);

        System.out.print("Введите логин: ");
        CURRENT_USER = scanner.nextLine().trim();
        System.out.print("Введите пароль: ");
        CURRENT_HASH = PasswordUtil.hash(scanner.nextLine().trim());

        while (true) {
            System.out.print("\n> ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            try {
                if (input.toLowerCase().startsWith("execute_script")) {
                    String[] parts = input.split("\\s+", 2);
                    String fileName = parts.length > 1 ? parts[1].trim() : null;
                    executeScriptFile(fileName, scanner, network, reader);
                    continue;
                }

                Command command = reader.parseCommand(input);
                if (command == null) continue;

                if (command.getType() == CommandType.EXIT) {
                    Response response = network.sendRequest(new Request(command, CURRENT_USER, CURRENT_HASH));
                    if (response != null) System.out.println(response.getMessage());
                    break;
                }

                Response response = network.sendRequest(new Request(command, CURRENT_USER, CURRENT_HASH));
                if (response == null) {
                    System.out.println("Сервер недоступен.");
                    continue;
                }

                if (response.getMessage().contains("Неверный логин или пароль")) {
                    System.out.println("\nДанный пользователь не найден в базе.");
                    System.out.print("Зарегистрировать нового пользователя? (y/n): ");
                    String choice = scanner.nextLine().trim();

                    if (choice.equalsIgnoreCase("y")) {
                        // отправка команды регистрации с теми же данными, что введены при старте
                        Command regCmd = new RegisterCommand(CURRENT_USER, CURRENT_HASH);
                        Response regResp = network.sendRequest(new Request(regCmd, CURRENT_USER, CURRENT_HASH));
                        System.out.println(regResp.getMessage());

                        if (regResp.isSuccess()) {
                            System.out.println("Регистрация успешна! Повторите команду.");
                        }
                    } else {
                        System.out.println("Введите корректный логин/пароль или перезапустите клиент.");
                    }
                    continue;
                }

                if (response.isSuccess()) {
                    System.out.println(response.getMessage());
                    if (response.getData() != null) response.getData().forEach(System.out::println);
                } else {
                    System.err.println(response.getMessage());
                }

            } catch (ConnectException e) {
                System.err.println("Не удалось подключиться к серверу");
            } catch (IOException e) {
                System.err.println("Ошибка сети: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Ошибка: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private static void executeScriptFile(String fileName, Scanner consoleScanner,
                                          ClientNetworkHandler network, CommandReader consoleReader) {
        if (fileName == null || fileName.isEmpty()) {
            System.out.print("Введите имя файла: ");
            fileName = consoleScanner.nextLine().trim();
        }
        if (!fileName.endsWith(".txt")) fileName += ".txt";

        File file = new File(fileName);
        if (!file.exists()) {
            System.err.println("Файл не найден");
            return;
        }

        // проверка на рекурсию
        String absolutePath = file.getAbsolutePath();
        if (executedScripts.contains(absolutePath)) {
            System.err.println("Обнаружена рекурсия скрипта: " + fileName);
            return;
        }
        executedScripts.add(absolutePath);

        try (Scanner fileScanner = new Scanner(file, StandardCharsets.UTF_8)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (line.isEmpty()) continue;

                System.out.println("> " + line);

                if (line.toLowerCase().startsWith("execute_script")) {
                    String[] parts = line.split("\\s+", 2);
                    String nestedFile = parts.length > 1 ? parts[1].trim() : null;
                    executeScriptFile(nestedFile, consoleScanner, network, consoleReader);
                    continue;
                }

                CommandReader scriptReader = new CommandReader(fileScanner);
                Command cmd = scriptReader.parseCommand(line);
                if (cmd == null) continue;

                if (cmd.getType() == CommandType.EXIT) break;

                try {
                    Response response = network.sendRequest(new Request(cmd, CURRENT_USER, CURRENT_HASH));
                    if (response == null) {
                        System.err.println("Сервер недоступен. Скрипт прерван");
                        break;
                    }
                    if (response.isSuccess()) {
                        System.out.println(response.getMessage());
                        if (response.getData() != null) response.getData().forEach((String s) -> System.out.println(s));
                    } else {
                        System.err.println(response.getMessage());
                    }
                } catch (IOException e) {
                    System.out.println("Ошибка отправки: " + e.getMessage());
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        } finally {
            executedScripts.remove(absolutePath);
        }
    }
}