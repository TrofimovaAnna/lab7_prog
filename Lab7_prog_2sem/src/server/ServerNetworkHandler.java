package server;

import shared.network.Request;
import shared.network.Response;
import shared.utility.SerializationUtil;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerNetworkHandler {
    private static final Logger logger = LogManager.getLogger(ServerNetworkHandler.class);

    public ServerNetworkHandler(ServerSocketChannel channel) {}

    public Request readRequest(SocketChannel channel) {
        try {
            channel.configureBlocking(true);
            byte[] requestBytes = SerializationUtil.readWithChunking(channel);
            Object obj = SerializationUtil.fromBytes(requestBytes);
            if (obj instanceof Request) return (Request) obj;
            return null;
        } catch (Exception e) {
            logger.error("Ошибка чтения: {}", e.getMessage());
            return null;
        }
    }

    public boolean sendResponse(SocketChannel channel, Response response) {
        try {
            channel.configureBlocking(true);
            byte[] responseBytes = SerializationUtil.toBytes(response);
            SerializationUtil.writeWithChunking(channel, responseBytes);
            return true;
        } catch (IOException e) {
            logger.error("Ошибка отправки: {}", e.getMessage());
            return false;
        }
    }
}
