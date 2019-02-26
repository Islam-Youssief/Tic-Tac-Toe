package network;

import model.PlayerDB;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import model.Player;
import static network.ServerSession.onlinePlayers;

/**
 *
 * @author ISLAM
 */
public class Server {
    // return all the players from db
    public  static HashMap<String,Player> allPlayers = PlayerDB.getAllPlayers();
    private int portNumber;
    private ServerSocket serverSocket;
    private Socket socket;
    public boolean running = false;

    public boolean startServer(int portNumber) throws IOException{
        serverSocket = new ServerSocket(portNumber);
        this.portNumber = portNumber;
        running = true;
        if(running)
            startCommunication();
        return running;
    }
    public void stopServer(){
        running = false;
        Handler notification = new Handler(Handler.HandType.TERM);
        for(Map.Entry<String, ServerSession> session : onlinePlayers.entrySet()){
            session.getValue().sendRequest(notification);
        }
        try{
            serverSocket.close();
        }catch(IOException ioex){
        }
    }
    private void startCommunication(){
        new Thread(()->{
            while(running){
                try{
                    socket = serverSocket.accept();
                    new ServerSession(socket);
                }catch(IOException ioex){
                    System.out.println("Server is closed : cannot accept connections anymore ..");
                }
            }
        }).start();
    }
}
