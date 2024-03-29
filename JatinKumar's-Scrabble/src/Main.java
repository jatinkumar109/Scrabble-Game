// This applcation is used to show a simple version of the game scrabble.
// It has been created for the purposes of SYSC 3110 - Software Design.
//
// @author: Merek Clement (101138443)
// @author: Kathan Patel (101146368)
// @author: Jatin Kumar (101092120)
// @author: Faiaz Ahsan (101120268)
//
// While this repo is public, please do not copy this code for your own assignments.

// Imports

// Constants
// PUT CONSTANTS HERE

import Developer.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Main {
    // Managers and utils
    private static DictionaryManager _dictionary;
    private static BoardManager _board;
    private static Utils _devUtils;
    private static PlayerManager _playerManager;

    // Game setup
    private static int _gameHeight = 24; // <-- Character height
    private static int _gameWidth = 24; // <-- Character wide
    private static int _playerCount = 2;
    private static boolean _devMode = false;
    private static boolean _generatePlayers = false;
    private static ArrayList<String> _aiNames = new ArrayList<String>();

    /**
     * The following setup method is used to setup a game instance
     * @return {boolean} - Returns true if the game was setup successfully
     */
    private static boolean setup() {
        // Setup dev utils
        _devUtils = new Utils(_devMode);

        // Setup the board manager
        _board = new BoardManager(_devUtils);
        _devUtils.printDev("Building gameboard as: " + _gameWidth + "x" + _gameHeight);
        _board.buildBoard(_gameWidth, _gameHeight);

        // Get players
        ArrayList<Player> requestedPlayers = setupPlayers();
        if (requestedPlayers.size() > 0){
            _playerManager = new PlayerManager(requestedPlayers);
        } else {
            System.out.println("[ERROR] Cannot create a game of 0 players. Please give a number through -players=x");
            System.exit(1);
        }


        // Initalize the DictionaryManager
        _dictionary = new DictionaryManager(_devUtils);
        _devUtils.printDev("DictionaryManager loaded with " + _dictionary.getWords().size() + " words");
        return testSetup();
    }

    /**
     * setupPlayers is for setting up the player manager with the requested players.
     * @return {ArrayList<Player>} The arraylist containing the players
     */
    private static ArrayList<Player> setupPlayers(){
        int created = 0;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        ArrayList<Player> players = new ArrayList<Player>();

        // Reading data using readLine
        while (created < 1){
            try {
                System.out.println("Please input a name for Player " + (created + 1));
                if (_generatePlayers){
                    if (created == 0){
                        _aiNames = Utils.makeList();
                    } else if (created >= 10) {
                        throw new UnsupportedOperationException("Cannot pre seed more than 10.");
                    }
                    String name = _aiNames.get(created);
                    System.out.println(name);
                    players.add(new Player(name));
                } else {
                    String name = reader.readLine();
                    players.add(new Player(name));
                }
                created++;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        int aiCount = 1;
        while(_playerCount > created)
        {
            String name = _aiNames.get(created);
             System.out.println(name);
              players.add(new Player(name));
              created++;
        }

        _devUtils.printDev("Created list of " + (created) + " players");
        return players;
    }

    /**
     * Stubbed test function to validate the setup.
     * @return
     */
    private static boolean testSetup() {
        // TODO: Stubbed for now until testing phase in later iterations.
        return true;
    }

    /**
     * The main method for the application.
     * @param args {String[]} - The arguments passed to the application.
     *              -dev -> This is used to enable dev mode.
     */
    public static void main(String[] args) {
        // Runtime args
        if (args.length > 0) {
            for(int i = 0; i < args.length; ++i) {
                if (args[i].toString().equals("-dev")) {
                    _devMode = true;
                }
                if (args[i].toString().contains("-height=")) {
                    args[i] = args[i].replace("-height=", "");
                    _gameHeight = Integer.parseInt(args[i]);
                }
                if (args[i].toString().contains("-width=")) {
                    args[i] = args[i].replace("-width=", "");
                    _gameWidth = Integer.parseInt(args[i]);
                }
                if (args[i].toString().contains("-players=")) {
                    args[i] = args[i].replace("-players=", "");
                    _playerCount = Integer.parseInt(args[i]);
                }
                if (args[i].toString().contains("-generatePlayers")) {
                    _generatePlayers = true;
                }
            }
        }

        // Setup game
        setup();

        // Launch the game
        Game game = new Game(_dictionary, _board, _devUtils, _playerManager);
        game.mainLoop();
    }
}
