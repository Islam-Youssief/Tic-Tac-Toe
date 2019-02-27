package model;

/**
 *
 * @author ISLAM
 */
public class Player {
    private String first_name;
    private String last_name;
    private String login_name;
    private String password;
    private int total_points;
    private int playing_with;
    private String status;
//    private boolean is_busy;
    private String pic_path;
    

    public Player(){}
    
    
    public Player(String first_name, String last_name, String login_name,int total_points, String status,String password,
            int playing_with, String pic_path) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.login_name = login_name;
        this.password = password;
        this.total_points = total_points;
        this.playing_with = playing_with;
        this.status =status;
        this.pic_path =pic_path;
        
    }
  public Player(String first_name, String last_name, String login_name,int total_points, String status,String password,
             String pic_path) {
         this.first_name = first_name;
        this.last_name = last_name;
        this.login_name = login_name;
        this.password = password;
        this.total_points = total_points;
        this.status =status;
        this.pic_path =pic_path;
        
    }
  
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setLoginName(String username){
        this.login_name = username;
    }
    public String getLoginName(){
        return login_name;
    }
    public void setFname(String fname){
        this.first_name = fname;
    }
    public String getFname(){
        return first_name;
    }
    public void setLname(String lname){
        this.last_name = lname;
    }
    public String getLname(){
        return last_name;
    }
    public String getFullName(){
        return first_name+" "+last_name;
    }
    public void setPicPath(String picPath){
        this.pic_path = picPath;
    }
    public String getPicPath(){
        return pic_path;
    }
    public void setTotalPoints(int score){
        this.total_points = score;
    }
    public int getTotalPoints(){
        return total_points;
    }
    
    public void setStatus(String status){
        this.status = status;
        
    }
    
    public String getStatus(){
        return status;
    }
    public int getPlayingWithWhome(){
        return playing_with;
    }
    
    public void setPlayingWith(int playerid){
        this.playing_with = playerid;
    }
    
}
