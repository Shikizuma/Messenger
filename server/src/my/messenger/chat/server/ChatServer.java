package my.messenger.chat.server;

import my.messenger.network.TCPConnection;
import my.messenger.network.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TCPConnectionListener  { // реализация интерфейса TCPConnectionListener c помощью implements

    public static void main(String[] args){
        new ChatServer();
    }

    private final ArrayList<TCPConnection> connections = new ArrayList<>(); //список соединений

    private ChatServer(){
        System.out.println("Server start..");
        try(ServerSocket serverSocket = new ServerSocket(302)) {
            while(true) { // создаем бесконечный цикл для принятия соединения
                try {
                    new TCPConnection(this, serverSocket.accept());
                } catch (IOException e) {
                    System.out.println("TCPConnection exception: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);  //добавляем соед в список
        sendToAllConnections("Client logged in" + tcpConnection);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {
        sendToAllConnections(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection); // удаляем из списка
        sendToAllConnections("Client disconnected" + tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection exception: " + e);
    }

    private void sendToAllConnections(String value) { //
        System.out.println(value); // смотрим что приходит
        final int cnt = connections.size(); // получаем размер списка
        for (int i = 0; i < cnt; i++) { // проходим по всем соединениям в списке
            connections.get(i).sendString(value); // отправляем всем сообщение
        }
    }
}
