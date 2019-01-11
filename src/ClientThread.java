import socketmessage.SocketMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class ClientThread implements Runnable{

    private Thread me;
    private boolean run = true;

    private Server server;

    private Socket socket;

    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    private String email;


    public ClientThread(Socket socket, Server server){

        this.server = server;

        this.socket = socket;

        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

        me = new Thread(this);
        me.start();

    }


    @Override
    public void run() {

        Map<String, String> map;

        while(this.run){
            try{

                Object obj = ois.readObject();

                SocketMessage socketMessage  = (SocketMessage) obj;

                if(socketMessage.getRequestType() == SocketMessage.REQUEST_TYPE_LOGIN) {

                    map = socketMessage.getMap();

                    String email = map.get("email");

                    if(!server.isEmailRegistered(email)) {

                        this.email = map.get("email");
                        sendLoginResponse(true);

                        System.out.println("user registered with id: " + email);

                        continue;

                    }

                    sendLoginResponse(false);

                }
                else if(socketMessage.getRequestType() == SocketMessage.REQUEST_TYPE_FRIENDS_LIST){

                    List<String> list = server.getClientsEmailsList(this.email);
//
                    sendFriendsListResponse(list);

                }
                else if(socketMessage.getRequestType() == SocketMessage.REQUEST_TYPE_SEND_CHAT_MESSAGE) {


                    map = socketMessage.getMap();

                    String to = map.get("to");
                    String message = map.get("message");

                    server.sendMessage(to, message);
                }

            }
            catch (IOException e){

                e.printStackTrace();

                stopThread();

                server.removeClientThread(this);

            } catch (ClassNotFoundException e) {

                e.printStackTrace();

            }
        }

    }

    public void sendMessage(String message){

        System.out.println("sendMessage");

        SocketMessage socketMessage = new SocketMessage();
        socketMessage.setResponseType(SocketMessage.RESPONSE_TYPE_SEND_CHAT_MESSAGE);
        socketMessage.getMap().put("message", message);

        sendSocketMessage(socketMessage);
    }

    public String getEmail() {
        return email;
    }

    private void sendLoginResponse(boolean success){

        SocketMessage socketMessage = new SocketMessage();

        socketMessage.setResponseType(SocketMessage.RESPONSE_TYPE_LOGIN);

        if(success) {

            socketMessage.getMap().put("result", "success");

        }

        if(!success){

            socketMessage.getMap().put("result", "fail");
            socketMessage.getMap().put("message", "Email already registered!");

        }

        sendSocketMessage(socketMessage);

        server.updateFriendsLists();

    }

    public void sendFriendsListResponse(List<String> list){

        SocketMessage socketMessage = new SocketMessage();

        socketMessage.setResponseType(SocketMessage.RESPONSE_TYPE_FRIENDS_LIST);

        String emails = "";

        for (String email: list) {
            emails += email + ";";
        }

        socketMessage.getMap().put("list", emails);

        sendSocketMessage(socketMessage);
    }

    private void sendSocketMessage(SocketMessage socketMessage){

        try {
            oos.writeObject(socketMessage);
        } catch (IOException e) {
            e.printStackTrace();

            stopThread();

            server.removeClientThread(this);
        }

    }

    private void stopThread(){
        run = false;
    }
}
