package by.isida.task1.model;

import by.isida.task1.service.PublisherReceivedMessagesHandler;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class PublisherOrClient {
    private String host;
    private int port;

    public PublisherOrClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() throws UnknownHostException, IOException {
        // подключить к серверу
        Socket publisherOrClient = new Socket(host, port);
        System.out.println("Successfully connected to server!");

        // Получить выходной поток Socket (куда пользователь отправляет свое сообщение)
        PrintStream output = new PrintStream(publisherOrClient.getOutputStream());

        // создать новый поток для обработки сообщений сервера
        new Thread(new PublisherReceivedMessagesHandler(publisherOrClient.getInputStream())).start();

        // читать сообщения с клавиатуры и отправлять на сервер
        System.out.println("Messages: \n");

        // закрываем ресурсы
        output.close();
        publisherOrClient.close();
    }
}
