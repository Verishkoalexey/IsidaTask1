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


public class PublisherGui extends Thread{

  final JTextPane jtextFilDiscu = new JTextPane();
  final JTextField jtextInputChat = new JTextField();
  private String oldMsg = "";
  private Thread read;
  private String name;
  BufferedReader input;
  PrintWriter output;
  Socket server;

  public PublisherGui() {
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

    // Поле ввода новой новости
    jtextInputChat.setBounds(0, 350, 400, 50);
    jtextInputChat.setFont(font);
    jtextInputChat.setMargin(new Insets(6, 6, 6, 6));
    final JScrollPane jtextInputChatSP = new JScrollPane(jtextInputChat);
    jtextInputChatSP.setBounds(25, 350, 650, 50);

    // Кнопка публиковать
    final JButton jsbtn = new JButton("Publish");
    jsbtn.setFont(font);
    jsbtn.setBounds(575, 410, 100, 35);

    jtextInputChat.addKeyListener(new KeyAdapter() {
      // send message on Enter
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          publishNews();
        }

        // Получить последнее сообщение
        if (e.getKeyCode() == KeyEvent.VK_UP) {
          String currentMessage = jtextInputChat.getText().trim();
          jtextInputChat.setText(oldMsg);
          oldMsg = currentMessage;
        }

        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
          String currentMessage = jtextInputChat.getText().trim();
          jtextInputChat.setText(oldMsg);
          oldMsg = currentMessage;
        }
      }
    });

    // Нажатие на кнопку публиковать
    jsbtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        publishNews();
      }
    });

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
          read = new Read();
          read.start();
          jfr.remove(jcbtn);
          jfr.add(jsbtn);
          jfr.add(jtextInputChatSP);
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

  // отправка новостей
  public void publishNews() {
    try {
      String message = jtextInputChat.getText().trim();
      if (message.equals("")) {
        return;
      }
      this.oldMsg = message;
      output.println(message);
      jtextInputChat.requestFocus();
      jtextInputChat.setText(null);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage());
      System.exit(0);
    }
  }

  public static void main(String[] args) throws Exception {
    PublisherGui publisher = new PublisherGui();

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
