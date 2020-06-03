package my.messenger.chat.client;

import my.messenger.network.TCPConnection;
import my.messenger.network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {

    private static final String IP_ = "192.168.19.105";
    private static final int PORT = 302;
    private static final int WIDTH = 500;
    private static final int HEIGHT = 300;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() { //поток edt, чтоб можно было обращаться к элементам swing
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }

    private final JTextArea log = new JTextArea(); // главное поле
    private final JTextField nickName = new JTextField("user"); // поле имя
    private final JTextField fInput = new JTextField(); // поле ввода

    private TCPConnection connection;

    private ClientWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null) ; // устанавливаем по середине
        //setAlwaysOnTop(true);
        log.setEditable(false); // не даем писать в гланое поле
        log.setLineWrap(true); // перенос слов в главном поле
        fInput.addActionListener(this);
        add(log, BorderLayout.CENTER); // указываем куда поставить наши поля
        add(fInput, BorderLayout.SOUTH);
        add(nickName, BorderLayout.NORTH);

        setVisible(true);
        try {
            connection = new TCPConnection(this, IP_, PORT);
        } catch (Exception e) {
            printM("Connection interrupted:" + e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) { // ловим нажатие enter для отправки сообщений
        String msg = fInput.getText(); // получаем текст из поля
        if(msg.equals("")) return;
        fInput.setText(null);
        connection.sendString(nickName.getText() + ": " + msg);
    }


    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printM("Connection already done!");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        printM(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printM("Disconnect connection..");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printM("Connection interrupted:" + e);
    }

    private synchronized void printM(String msg) { //метод которій пишет в главное поле
        SwingUtilities.invokeLater(new Runnable() { // добоваляем интерфейс runnable для того что бы он мог вызваться из разных потоков
            @Override
            public void run() {
                log.append(msg + "\n"); // добавляем строчку
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }
}
