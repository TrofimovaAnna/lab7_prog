package shared.utility;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SerializationUtil {
    private static final int MAX_CHUNK_SIZE = 4096;

    public static byte[] toBytes(Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.close();
        return baos.toByteArray();
    }

    public static Object fromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object obj = ois.readObject();
        ois.close();
        return obj;
    }

    public static void writeWithChunking(SocketChannel channel, byte[] data) throws IOException {
        int totalChunks = (data.length + MAX_CHUNK_SIZE - 1) / MAX_CHUNK_SIZE;

        ByteBuffer header = ByteBuffer.allocate(4);
        header.putInt(totalChunks);
        header.flip();
        while (header.hasRemaining()) channel.write(header);

        for (int i = 0; i < totalChunks; i++) {
            int start = i * MAX_CHUNK_SIZE;
            int end = Math.min(start + MAX_CHUNK_SIZE, data.length);
            int chunkSize = end - start;

            ByteBuffer chunkHeader = ByteBuffer.allocate(8);
            chunkHeader.putInt(i);
            chunkHeader.putInt(chunkSize);
            chunkHeader.flip();
            while (chunkHeader.hasRemaining()) channel.write(chunkHeader);

            ByteBuffer chunkData = ByteBuffer.wrap(data, start, chunkSize);
            while (chunkData.hasRemaining()) channel.write(chunkData);
        }
        System.err.println("Байтовый пакет: " + data.length + " байт, используется " + totalChunks + " пакет");
    }

    public static byte[] readWithChunking(SocketChannel channel) throws IOException {
        ByteBuffer totalBuf = ByteBuffer.allocate(4);
        while (totalBuf.hasRemaining()) {
            int read = channel.read(totalBuf);
            if (read == -1) throw new IOException("Соединение закрыто");
            if (read == 0) { try { Thread.sleep(1); } catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new IOException("Прервано", e); } }
        }
        totalBuf.flip();
        int totalChunks = totalBuf.getInt();

        ByteArrayOutputStream result = new ByteArrayOutputStream();

        for (int i = 0; i < totalChunks; i++) {
            ByteBuffer chunkHeader = ByteBuffer.allocate(8);
            while (chunkHeader.hasRemaining()) {
                int read = channel.read(chunkHeader);
                if (read == -1) throw new IOException("Соединение закрыто");
                if (read == 0) { try { Thread.sleep(1); } catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new IOException("Прервано", e); } }
            }
            chunkHeader.flip();
            chunkHeader.getInt();
            int chunkSize = chunkHeader.getInt();

            ByteBuffer chunkData = ByteBuffer.allocate(chunkSize);
            while (chunkData.hasRemaining()) {
                int read = channel.read(chunkData);
                if (read == -1) throw new IOException("Соединение закрыто");
                if (read == 0) { try { Thread.sleep(10); } catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new IOException("Прервано", e); } }
            }
            result.write(chunkData.array());
        }
        return result.toByteArray();
    }
}