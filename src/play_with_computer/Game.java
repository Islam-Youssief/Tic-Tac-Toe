package play_with_computer;

/**
 *
 * @author Elsamman
 */
public class Game {
    //enum for cell status should be kept in assets
    //private enum Cell{ X, O};
    private final String player1;
    private final String player2;
    //"currentGame" will store game status
    private final String[][] currentGame ;
    public  int incMove;
    public boolean Horizontal = true, Vertical = true, DiagonalOne = true, DiagonalTwo = true;
    //class constructor initiates new current game table with 3*3 dimensions
    public Game(String player1,String player2){
        this.incMove = 0;
        this.player1=player1;
        this.player2=player2;
        currentGame = new String[3][3];
        DiagonalOne = true;
    }
    public String getPlayer1() {
        return player1;
    }
    public String getPlayer2() {
        return player2;
    }
    //method to handle move request takes 3 params enum , position in game table x,y
    public boolean validateMove(String player,int x,int y){
        if(currentGame[x][y] == null){
            currentGame[x][y] = player;
            //checkForWin(player,x,y);
            return true;
        }else{
            return false;
        }
    }
    //method to check win horizontal, vertical and diagonal    
    public String checkForWin(String player,int x,int y){
        for(int i = 0; i < 3; i++){
            //horizontal check
            if(currentGame[x][i]!=player){
                Horizontal = false;
            }
            //vertical check
            if(currentGame[i][y]!=player){
                Vertical = false;
            }
        }
        //diagonals check
        if( x==y || x==2-y ){
            for(int i = 0; i < 3; i++){
                if(currentGame[i][i]!=player)
                    DiagonalOne = false;
                if(currentGame[i][2-i]!=player)
                    DiagonalTwo = false;
            }
        }else{
            DiagonalOne = false;
            DiagonalTwo = false;
        }
        if(Horizontal || Vertical || DiagonalOne || DiagonalTwo){
            return "win";
        }else if (incMove==8) {
            return "draw";
        }else{
            Horizontal = Vertical = DiagonalOne = DiagonalTwo = true;
            incMove++;
            return "gameOn";
        }
    }
}
