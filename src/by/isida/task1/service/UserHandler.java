package by.isida.task1.service;

import by.isida.task1.Server;
import by.isida.task1.model.User;

import java.util.Scanner;

public class UserHandler implements Runnable{
    private Server server;
    private User user;

    public UserHandler(Server server, User user) {
        this.server = server;
        this.user = user;
    }

    public void run() {
        String news;

        // когда появляется новая новость, показывается всем
        Scanner sc = new Scanner(this.user.getInputStream());
        while (sc.hasNextLine()) {
            news = sc.nextLine();
            // Управление личными сообщениями
            if (news.charAt(0) == '@'){
                if(news.contains(" ")){
                    System.out.println("private msg : " + news);
                    int firstSpace = news.indexOf(" ");
                    String userPrivate= news.substring(1, firstSpace);
                    server.sendMessageToUser(
                            news.substring(
                                    firstSpace+1, news.length()
                            ), user, userPrivate
                    );
                }
                // Управление изменениями
            }else{
                // обновить список пользователей
                server.broadcastNews(news, user);
            }
        }
        // конец Thread
        server.removeUser(user);
        sc.close();
    }
}
