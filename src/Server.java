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
            ss = new ServerSocket(4321);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleConnections(){
        try {

            while(true) {

                Socket socket = ss.accept();
                clients.add(new ClientThread(socket));

                System.out.println("connected");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<ClientThread> getClients() {
        return clients;
    }

    public ClientThread getClientByEmail(String email){
        for(ClientThread clientThread: clients)
            if(clientThread.getEmail().equals(email))
                return clientThread;

        return null;
    }
}
