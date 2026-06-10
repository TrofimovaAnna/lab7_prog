package server;

import shared.command.CommandContext;
import shared.network.NetworkConstants;
import shared.network.Request;
import shared.network.Response;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerApp {
    private static final Logger logger = LogManager.getLogger(ServerApp.class);

    private static final ExecutorService READ_POOL = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static final ExecutorService SEND_POOL = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите пароль от базы данных (studs): ");
        String dbPassword = scanner.nextLine().trim();

        if (dbPassword.isEmpty()) {
            logger.error("Пароль не может быть пустым. Сервер остановлен.");
            scanner.close();
            return;
        }

        try {
            logger.info("Подключение к базе данных...");

            DatabaseManager db = new DatabaseManager(dbPassword);
            CollectionManager cm = new CollectionManager(db);
            HistoryManager hm = new HistoryManager();
            CommandContext context = new CommandContext(cm, hm);

            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress(NetworkConstants.PORT));
            serverChannel.configureBlocking(false);

            ServerNetworkHandler network = new ServerNetworkHandler(serverChannel);
            RequestHandler handler = new RequestHandler(context, db);

            logger.info("Сервер запущен на порту {}", NetworkConstants.PORT);

            while (true) {
                try {
                    SocketChannel clientChannel = serverChannel.accept();
                    if (clientChannel == null) {
                        Thread.sleep(50);
                        continue;
                    }
                    logger.info("Подключение: {}", clientChannel.getRemoteAddress());

                    READ_POOL.submit(() -> {
                        try {
                            Request request = network.readRequest(clientChannel);
                            if (request == null) {
                                clientChannel.close();
                                return;
                            }
                            logger.info("Запрос: {}", request.getCommand().getType());

                            new Thread(() -> {
                                Response response = handler.handle(request);
                                logger.info("Ответ: {}", response.isSuccess() ? "Успешно" : "Ошибка");

                                SEND_POOL.submit(() -> {
                                    try {
                                        network.sendResponse(clientChannel, response);
                                        clientChannel.close();
                                    } catch (IOException ignored) {}
                                });
                            }).start();

                        } catch (Exception e) {
                            logger.error("Ошибка чтения", e);
                            try { clientChannel.close(); } catch (IOException ignored) {}
                        }
                    });

                } catch (IOException e) {
                    logger.error("Ошибка сервера", e);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

        } catch (Exception e) {
            logger.error("Критическая ошибка сервера: {}", e.getMessage());
            logger.error("Проверьте: пароль от БД и активен ли SSH");
        } finally {
            READ_POOL.shutdown();
            SEND_POOL.shutdown();
            scanner.close();
        }
    }
}