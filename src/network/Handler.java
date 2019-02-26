package network;

import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * @author ISLAM
 */
public class Handler implements Serializable{
    /***
     * handler of type success is used whenever there is success signal
     * Things like success in login, signup,send,reciving messages , ...
     */
    public static final String SUCCESS = "success";
    /***
     * failure type Handler is used whenever there is a fail in doing specific functions
     *
     */
    
    public static final String FAILURE = "failure";
    /***
     * Theses are the representation of the player status
     */
    public static final String ONLINESTATUS = "Online";
    public static final String OFFLINESTATUS = "Offline";
    public static final String BUSYSTATUS = "Busy";

 public static enum HandType{
    LOGIN,//used when login in to specify request then check authentication
    LOGOUT,//used when logout to make user offline plus refresh players table
    REGISTER,//used to specify request then check if user exists
    INIT,//Initalize connection with server (loginCase)
    TERM,//Terminate connection (ShutDownServerCase)
    NOTIFY,// Notification to server that status of player is online/offline/busy
    GAME_REQUEST,//request from source to destination
    GAME_RESPONSE,//response from destination to source
    AIGAME_REQUEST,//request to play with computer
    MOVE,//request of movement x and o
    GAME_OVER,//request of finishing the game 
    CHAT,//request of messages from players
    // can add any more requests easily here ..
    UNKNOWN
};
    private HandType type;
    private HashMap<String,String> data;
    
    public Handler(HandType type)
    {
        this.type = type;
        data = new HashMap<String,String>();
    }
    
    public Handler(HandType type, String key, String value)
    {
        this.type = type;
        data = new HashMap<String,String>();
        data.put(key, value);
    }
    
    public void setType(HandType type)
    {
        this.type = type;
    }
    public HandType getType()
    { 
        return type; 
    }
    
    public void setData(String key, String value)
    {
        data.put(key,value);
    }
    public String getData(String key){
        if(data.containsKey(key))
            return data.get(key);
        else
            return null;
    }
}
