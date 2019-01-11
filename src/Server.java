import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private ServerSocket ss;
    private List<ClientThread> clients = new ArrayList<ClientThread>();

//    private ClientThread

    public Server(){
        try {
            ss = new ServerSocket(4322);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleConnections(){
        try {

            while(true) {

                Socket socket = ss.accept();
                clients.add(new ClientThread(socket, this));

                System.out.println("connected");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<ClientThread> getClients() {
        return clients;
    }

    private ClientThread getClientByEmail(String email){
        for(ClientThread clientThread: clients)
            if(clientThread.getEmail() != null && clientThread.getEmail().equals(email))
                return clientThread;

        return null;
    }

    public void removeClientThread(ClientThread clientThread){

        if(clients.contains(clientThread))
            clients.remove(clientThread);

    }

    public boolean isEmailRegistered(String email){
        ClientThread clientThread = getClientByEmail(email);

        return clientThread != null;
    }

    public void sendMessage(String to, String message){

        ClientThread  clientThread = getClientByEmail(to);

        System.out.println(to);
        System.out.println(clientThread);

        if(clientThread != null)
            clientThread.sendMessage(message);

    }

    public List<String> getClientsEmailsList(String exceptEmail){

        List<String> list = new ArrayList<>();

        for(ClientThread clientThread: clients) {
            if (clientThread.getEmail() != null && !clientThread.getEmail().equals(exceptEmail)) {
//                System.out.println(clientThread.getEmail() + " " + exceptEmail + " " + !clientThread.getEmail().equals(exceptEmail));
                list.add(clientThread.getEmail());
            }
        }

        return list;

    }

    public void updateFriendsLists(){
        for (ClientThread clientThread: clients) {
            List<String> list =  getClientsEmailsList(clientThread.getEmail());
            System.out.println(list.size());
            clientThread.sendFriendsListResponse(list);
        }
    }
}
