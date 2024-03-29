import Developer.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Math;
import java.util.ArrayList;

public class Game {
    // Managers and utils
    private DictionaryManager _dictionary;
    private BoardManager _board;
    private Utils _utils;
    private PlayerManager _players;

    private boolean _gameInProgress = false;
    // Instance variables
    private int _maxScore = 0;

    // Constructor (Requires the utils and managers)
    public Game(DictionaryManager dm, BoardManager bm, Utils u, PlayerManager pm) {
        this._dictionary = dm;
        this._board = bm;
        this._utils = u;
        this._players = pm;
    }

    public boolean isRunning(){
        return this._gameInProgress;
    }

    public void terminate(){
        this._gameInProgress = false;
    }

    private boolean setProgress(boolean state){
        this._gameInProgress = state;
        return this._gameInProgress;
    }
    private void welcome() {
        System.out.print("Welcome to the game:\n");
        this._players.getPlayers().forEach((player) -> {
            System.out.println(player.name + " [" + player.get_id() + "]");
        });
    }

    private boolean requestStart() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        if (this._utils.isDev()) {
            return true;
        }
        System.out.println("Instructions, to make a move, input the starting position followed by the direction. And finally the word to input");
        System.out.println("Example: [1,1,down]Jump");
        System.out.println("Example: [10,15,right]Sky");
        System.out.println("At any point you may stop the game with \"exit\"");
        System.out.println("Are you ready to begin? (y/n)");
        String res = reader.readLine();

        System.out.println("How many points to win (1 correct is 1 point)?");
        String points = reader.readLine();
        this._maxScore = Integer.parseInt(points);

        if (res.equals("y")){
            return true;
        } else {
            return false;
        }
    }

    public boolean mainLoop() {
        this.welcome();
        try {
            if (!this.requestStart()) {
                return false;
            } else {
                this.setProgress(true);

                // TODO: Allow CLI args to be passed through.
                this._board.buildBoard(24, 24);
                this._board.initalizeIndex();
            }

            while (this._gameInProgress) {
                Player next = this._players.getNextMove();
                System.out.println("It is " + next.name + "'s turn");
                this._board.viewBoard();
                String move = "";
                if(next.getNumber() == 0){
                System.out.println("Please enter your move ([TOP#,LEFT#,DIRECTION]WORD): ");
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                move = reader.readLine();
                }
                else
                {
                	String inpDirection = "";
                	int direction = (int)(Math.random() * (4 - 1 + 1) + 1); //generate random number between 1 and 4
                    switch(direction)
                    {
                    	case 1:
                                    inpDirection = "up";
                                    break;
                        case 2:
                                    inpDirection = "down";
                                    break;
                        case 3:
                                    inpDirection = "left";
                                    break;
                        case 4:
                                    inpDirection = "right";
                                    break;   
                    }
                    
                    ArrayList<String> words = new ArrayList<String>();
                    words = _dictionary.getWords();
                    int word = (int)(Math.random() * (words.size() + 1) ); 
                    String inpWord = words.get(word);
                    
                    int top = 0, left = 0;
                    if(inpDirection.equals("up")){
                        top = (int)(Math.random() * (_board._height - inpWord.length() + 1) + inpWord.length());
                        left = (int)(Math.random() * (_board._width + 1));
                    }
                    else if(inpDirection.equals("down")){
                        top = (int)(Math.random() * (_board._height + 1));
                        left = (int)(Math.random() * (_board._width + 1));
                    }
                    else if(inpDirection.equals("left")){
                        top = (int)(Math.random() * (_board._height + 1));
                        left = (int)(Math.random() * (_board._width - inpWord.length() + 1) + inpWord.length());
                    }
                    else{
                        top = (int)(Math.random() * (_board._height + 1));
                        left = (int)(Math.random() * (_board._width - inpWord.length() + 1) + inpWord.length());
                    }
                    
                    move = "[" + String.valueOf(top) + "," + String.valueOf(left) + "," + inpDirection + "]" + inpWord;
                }
                if (move.equals("exit")){
                    this._gameInProgress = false;
                    return false;
                }
                if (this._dictionary.isWord(move.split("]")[1])) {
                    if (this._board.validateMove(move)) {
                        this._players.getNextMove().setScore(1);
                        if (this._players.getNextMove().getScore() >= this._maxScore) {
                            this._gameInProgress = false;
                            System.out.println("Game over, " + this._players.getNextMove().name + " has won!");
                        }
                        this._players.getNextMove().setMove();
                    } else {
                        System.out.println("Invalid move, please try again");
                    }
                } else {
                    System.out.println("Invalid word, please try again");
                }
            }
            return true;
        } catch (Exception e) {
            System.out.println("[ERROR] Error during main loop or io input. Terminating...");
            return false;
        }
    }
}
