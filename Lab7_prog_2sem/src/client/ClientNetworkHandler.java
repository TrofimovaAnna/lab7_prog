package client;

import shared.network.Request;
import shared.network.Response;
import shared.network.NetworkConstants;
import shared.utility.SerializationUtil;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class ClientNetworkHandler {

    public Response sendRequest(Request request) throws IOException {
        SocketChannel channel = null;
        try {
            channel = SocketChannel.open();
            channel.connect(new InetSocketAddress(NetworkConstants.SERVER_HOST, NetworkConstants.PORT));
            channel.configureBlocking(true);

            System.out.println("Клиент подключен к серверу");

            byte[] requestBytes = SerializationUtil.toBytes(request);
            SerializationUtil.writeWithChunking(channel, requestBytes);

            byte[] responseBytes = SerializationUtil.readWithChunking(channel);
            return (Response) SerializationUtil.fromBytes(responseBytes);

        } catch (ConnectException e) {
            System.err.println("Не удалось подключиться к серверу");
            throw e;
        } catch (ClassNotFoundException e) {
            throw new IOException("Ошибка десериализации", e);
        } finally {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
        }
    }
}
