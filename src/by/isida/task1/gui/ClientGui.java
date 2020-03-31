package by.isida.task1.gui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class ClientGui extends Thread {
    final JTextPane jtextFilDiscu = new JTextPane();
    private Thread read;
    private String name;
    BufferedReader input;
    PrintWriter output;
    Socket server;

    public ClientGui() {
        this.name = "";
        String fontfamily = "Arial, sans-serif";
        Font font = new Font(fontfamily, Font.PLAIN, 15);

        final JFrame jfr = new JFrame("News window");
        jfr.getContentPane().setLayout(null);
        jfr.setSize(700, 500);
        jfr.setResizable(false);
        jfr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Потоковый модуль
        jtextFilDiscu.setBounds(25, 25, 490, 320);
        jtextFilDiscu.setFont(font);
        jtextFilDiscu.setMargin(new Insets(6, 6, 6, 6));
        jtextFilDiscu.setEditable(false);
        JScrollPane jtextFilDiscuSP = new JScrollPane(jtextFilDiscu);
        jtextFilDiscuSP.setBounds(25, 25, 650, 320);

        jtextFilDiscu.setContentType("text/html");
        jtextFilDiscu.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);

        // Вид подключения
        final JTextField jtfName = new JTextField(this.name);
        final JButton jcbtn = new JButton("Connect");

        // Положение модулей
        jcbtn.setFont(font);
        jcbtn.setBounds(575, 380, 100, 40);

        // Цвет по умолчанию
        jtextFilDiscu.setBackground(Color.LIGHT_GRAY);

        // добавление элементов
        jfr.add(jcbtn);
        jfr.add(jtextFilDiscuSP);
        jfr.setVisible(true);

        // Подключение
        jcbtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    name = jtfName.getText();
                    server = new Socket("localhost", 12345);
                    input = new BufferedReader(new InputStreamReader(server.getInputStream()));
                    output = new PrintWriter(server.getOutputStream(), true);

                    // Сохранить nickname на сервер
                    output.println(name);

                    // создать новый Read Thread
                    read = new ClientGui.Read();
                    read.start();
                    jfr.remove(jcbtn);
                    jfr.revalidate();
                    jfr.repaint();
                    jtextFilDiscu.setBackground(Color.WHITE);
                } catch (Exception ex) {
                    appendToPane(jtextFilDiscu, "<span>Could not connect to Server</span>");
                    JOptionPane.showMessageDialog(jfr, ex.getMessage());
                }
            }

        });
    }

    public static void main(String[] args) throws Exception {
        ClientGui client = new ClientGui();
    }

    // читать новые новости
    class Read extends Thread {
        public void run() {
            String news;
            while(!Thread.currentThread().isInterrupted()){
                try {
                    news = input.readLine();
                    if(news != null){
                        if (news.charAt(0) == '[') {
                            news = news.substring(1, news.length()-1);
                            ArrayList<String> ListUser = new ArrayList<String>(
                                    Arrays.asList(news.split(", "))
                            );
                        }else{
                            appendToPane(jtextFilDiscu, news);
                        }
                    }
                }
                catch (IOException ex) {
                    System.err.println("Failed to parse incoming news");
                }
            }
        }
    }

    // отправить HTML на панель
    private void appendToPane(JTextPane tp, String msg){
        HTMLDocument doc = (HTMLDocument)tp.getDocument();
        HTMLEditorKit editorKit = (HTMLEditorKit)tp.getEditorKit();
        try {
            editorKit.insertHTML(doc, doc.getLength(), msg, 0, 0, null);
            tp.setCaretPosition(doc.getLength());
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
