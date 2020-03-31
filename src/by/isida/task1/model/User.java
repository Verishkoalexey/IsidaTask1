package by.isida.task1.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;

public class User {
    private static int nbUser = 0;
    private int userId;
    private PrintStream streamOut;
    private InputStream streamIn;
    private Socket client;

    // конструктор
    public User(Socket client) throws IOException {
        this.streamOut = new PrintStream(client.getOutputStream());
        this.streamIn = client.getInputStream();
        this.client = client;
        this.userId = nbUser;
        nbUser += 1;
    }

    public PrintStream getOutStream(){
        return this.streamOut;
    }

    public InputStream getInputStream(){
        return this.streamIn;
    }
}
