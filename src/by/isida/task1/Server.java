package by.isida.task1;

import by.isida.task1.model.User;
import by.isida.task1.service.UserHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {
    private int port;
    private List<User> clients;
    private ServerSocket server;

    public static void main(String[] args) throws IOException {
        new Server(12345).run();
    }

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<User>();
    }

    public void run() throws IOException {
        server = new ServerSocket(port) {
            protected void finalize() throws IOException {
                this.close();
            }
        };
        System.out.println("Port 12345 is now open.");

        while (true) {
            // принимает нового клиента
            Socket client = server.accept();

            // получить псевдоним newUser
            String nickname = (new Scanner( client.getInputStream() )).nextLine();
            nickname = nickname.replace(",", ""); //  ',' использовать для сериализации
            nickname = nickname.replace(" ", "_");
            System.out.println("New Client: \"" + nickname + "\"\n\t     Host:" + client.getInetAddress().getHostAddress());

            // Создать нового пользователя
            User newUser = new User(client);

            // добавить сообщение newUser в список
            this.clients.add(newUser);

            // Сообщение приветствия
            newUser.getOutStream().println("<H2>Свежие новости!</H2> ");

            // создать новый поток для обработки входящих сообщений newUser
            new Thread(new UserHandler(this, newUser)).start();
        }
    }

    // удалить пользователя из списка
    public void removeUser(User user){
        this.clients.remove(user);
    }

    // отправить входящие сообщения всем пользователям
    public void broadcastNews(String msg, User userSender) {
        for (User client : this.clients) {
            client.getOutStream().println("<span> " + msg+"</span>");
        }
    }

    // Отправить сообщение пользователю
    public void sendMessageToUser(String msg, User userSender, String user){
        boolean find = false;
        for (User client : this.clients) {
            if (client != userSender) {
                find = true;
                userSender.getOutStream().println(userSender.toString() + " -> " + client.toString() +": " + msg);
                client.getOutStream().println(
                        "(<b>Private</b>)" + userSender.toString() + "<span>: " + msg+"</span>");
            }
        }
        if (!find) {
            userSender.getOutStream().println(userSender.toString() + " -> (<b>no one!</b>): " + msg);
        }
    }
}
