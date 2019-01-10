public class Main {

    static Server server;

    public static void main(String args[]){

        server = new Server();
        server.handleConnections();

    }

}
