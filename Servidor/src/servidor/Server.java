package Servidor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

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
                try (FileInputStream fis = new FileInputStream(imageFile); 
                     BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream())) {
                    fis.read(imageBytes);
                    bos.write(imageBytes);
                    bos.flush(); // Garante que a imagem foi enviada completamente
                }

                System.out.println("Imagem enviada, aguardando resposta...");

                // Receber JSON do cliente
                StringBuilder receivedData = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        receivedData.append(line);
                    }
                }

                System.out.println("Objetos detectados recebidos: " + receivedData.toString());

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
