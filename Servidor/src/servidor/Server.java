package Servidor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Server {

    public static void main(String[] args) {
        int port = 5000;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor aguardando conexão...");

            try (Socket socket = serverSocket.accept()) {
                System.out.println("Cliente conectado.");

                // Enviar imagem para o cliente
                File imageFile = new File("camera.jpg");

                // Converte a imagem para bytes e envia
                byte[] imageBytes = new byte[(int) imageFile.length()];
                try (FileInputStream fis = new FileInputStream(imageFile); BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream())) {
                    fis.read(imageBytes);
                    bos.write(imageBytes);
                    bos.flush(); // Garante que a imagem foi enviada completamente
                }

                System.out.println("Imagem enviada, aguardando resposta...");

                // Ler os primeiros 4 bytes para saber o tamanho do JSON
                InputStream inputStream = socket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);
                int jsonLength = dataInputStream.readInt(); // Lê 4 bytes indicando o tamanho

                // Agora ler exatamente 'jsonLength' bytes do JSON
                byte[] jsonData = new byte[jsonLength];
                dataInputStream.readFully(jsonData); // Garante que lemos tudo

                String receivedJson = new String(jsonData, StandardCharsets.UTF_8);
                System.out.println("Objetos detectados recebidos: " + receivedJson);

                socket.close();
                System.out.println("Conexão encerrada com o cliente.");
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
