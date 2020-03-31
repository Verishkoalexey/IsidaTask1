package by.isida.task1.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class PublisherReceivedMessagesHandler implements Runnable {
    private InputStream server;

    public PublisherReceivedMessagesHandler(InputStream server) {
        this.server = server;
    }

    public void run() {
        // получать новости от сервера и распечатывать на экране
        Scanner s = new Scanner(server);
        String tmp = "";
        while (s.hasNextLine()) {
            tmp = s.nextLine();
            if (tmp.charAt(0) == '[') {
                tmp = tmp.substring(1, tmp.length()-1);
                System.out.println(
                        "\nUSERS LIST: " +
                                new ArrayList<String>(Arrays.asList(tmp.split(","))) + "\n"
                );
            }
        }
        s.close();
    }
}
