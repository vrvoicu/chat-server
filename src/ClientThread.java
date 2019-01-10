import socketmessage.SocketMessage;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

public class ClientThread implements Runnable{

    private Thread me;
    private boolean run = true;

    private Socket socket;

    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    private String email;

    public ClientThread(Socket socket){
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

                    this.email = map.get("email");

                }
                else if(socketMessage.getRequestType() == SocketMessage.REQUEST_TYPE_FRIENDS_LIST){


                }
                else if(socketMessage.getRequestType() == SocketMessage.REQUEST_TYPE_SEND_CHAT_MESSAGE){

                    map = socketMessage.getMap();

                    System.out.println(map.get("message"));
                }

            }
            catch (Exception ex){

            }
        }

    }

    public void sendMessage(String message){

        SocketMessage socketMessage = new SocketMessage();
        socketMessage.setResponseType(SocketMessage.RESPONSE_TYPE_SEND_CHAT_MESSAGE);
        socketMessage.getMap().put("message", message);

        try {
            oos.writeObject(socketMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getEmail() {
        return email;
    }
}
