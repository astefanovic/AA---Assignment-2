package player;

import java.util.Scanner;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Stack;
import ship.*;
import world.World;

/**
 * Monte Carlo guess player (task C).
 * Please implement this class.
 *
 * @author Youhan, Jeffrey
 */
public class MonteCarloGuessPlayer  implements Player{

    private enum Mode {
        HUNT, TARGET
    }

    private World world;
    // Do we need this?
    private ArrayList<Guess> unmadeGuesses = new ArrayList<Guess>();
    private ArrayList<Guess> madeGuesses = new ArrayList<Guess>();
    private ArrayList<Ship> enemyShips = new ArrayList<Ship>();
    private int[][] shipConfigs;
    private Mode mode = Mode.HUNT;
    // Used as a Queue to order the next guesses, enqueues at the end of the list, dequeues at the start
    private ArrayList<Guess> nextGuess = new ArrayList<Guess>();
    private Stack targetingStack = new Stack();

    /**
     * @description perform any initialisation operations to start
     * @param world this player's world instance
     * @return void
     **/
    @Override
    public void initialisePlayer(World world) {
        this.world = world;
        shipConfigs = new int[world.numRow][world.numColumn];

        // Initialise the other players ships
        for(World.ShipLocation sl : world.shipLocations)
        {
            enemyShips.add(sl.ship);
        }
        
        calcShipConfigs();

        // Initialise unmade guesses
        for (int i = 0; i < world.numRow; i++) {
            for (int j = 0; j < world.numColumn; j++) {
                Guess g = new Guess();
                g.row = i;
                g.column = j;
                unmadeGuesses.add(g);
            }
        }

        
    } // end of initialisePlayer()

    /**
     * @description Determine whether a ship was hit and also if it was sunk.
     * @param guess The opponent's guess
     * @return Answer an object stating whether a hit was made and what ship was sunk (if any)
     **/
    @Override
    public Answer getAnswer(Guess guess) {
        Answer answer = new Answer();

        // Loop over each ship
        for (World.ShipLocation sl : world.shipLocations) {
            // Loop over each coordinate the ship occupies
            for (World.Coordinate c : sl.coordinates) {
                // Check if the guess matches the coordinate
                if (guess.row == c.row && guess.column == c.column) {
                    answer.isHit = true;
                    if (isShipSunk(sl))
                    {
                        answer.shipSunk = sl.ship;
                        for(int i = 0; i < world.shipLocations.size(); i++)
                        {
                            if(world.shipLocations.get(i).ship.name().equals(answer.shipSunk.name()))
                                world.shipLocations.remove(i);
                        }
                    }
                    return answer;
                }
            }
        }

        return answer;
    } // end of getAnswer()

    /**
     * @description Make a guess for a location to aim at on opponent's board
     * @return Guess the location to aim at
     **/
    @Override
    public Guess makeGuess() {
        Guess guess = null;

        // Check which mode we are in
        if (mode == Mode.HUNT) {
            guess = huntGuess();
        } else if (mode == Mode.TARGET) {
            guess = targetGuess();
        }

        //throw new Exception("not finished");
        // dummy return
        return guess;
    } // end of makeGuess()


    @Override
    public void update(Guess guess, Answer answer) {
        // Update mode
        if (answer.isHit || !nextGuess.isEmpty()) {
            mode = Mode.TARGET;
        } else {
            mode = Mode.HUNT;
            return;
        }
        
        // NOTE: can't just use calcShipConfigs below...
        // must adjust it to account for previous shots!!!!!!!!!!!!!!!!

        if (answer.shipSunk != null) {
            // Updating enemyShips
            int index = 0;
            for(; index < enemyShips.size(); index++)
            {
                if(answer.shipSunk.name().equals(enemyShips.get(index).name()))
                {
                    break;
                }
            }
            enemyShips.remove(index);

            calcShipConfigs();
            // Calculate best guesses and push items onto stack
        } else if (answer.isHit) {
            Guess west = new Guess();
            west.row = guess.row;
            west.column = guess.column - 1;
            // If the guess is made, in queue or out of bounds, dont add to queue
            if(!inMadeGuesses(west) && !inNextGuess(west) && west.column >= 0) nextGuess.add(west);

            Guess south = new Guess();
            south.row = guess.row - 1;
            south.column = guess.column;
            if(!inMadeGuesses(south) && !inNextGuess(south) && south.row >= 0) nextGuess.add(south);

            Guess east = new Guess();
            east.row = guess.row;
            east.column = guess.column + 1;
            if(!inMadeGuesses(east) && !inNextGuess(east) && east.column < world.numColumn) nextGuess.add(east);

            Guess north = new Guess();
            north.row = guess.row + 1;
            north.column = guess.column;
            if(!inMadeGuesses(north) && !inNextGuess(north) && north.row < world.numRow) nextGuess.add(north);
            // Modify ship config counts
            // Calculate best guesses and push items onto stack
            calcShipConfigs();
        } else {
            calcShipConfigs();
            // I dont think we need this, it does this automatically now when calcShipConfigs() runs
            //shipConfigs[guess.row][guess.column] = -1;

            // Modify ship config counts
            // Calculate best guesses and push items onto stack
        }

        //updateShipConfigurations(guess, answer);

        //throw new Exception("not implemented");
    } // end of update()

    /**
     * @description Check if there are no remaining ships on the board
     * @return boolean whether there are no remaining ships
     **/
    @Override
    public boolean noRemainingShips() {
        if(world.shipLocations.isEmpty()) return true;
        return false;
    } // end of noRemainingShips()

    /* ---------- Private Methods ---------- */

    /**
     * @description Make guess whilst in hunting mode
     * @return Guess the guess to be made
     **/
    private Guess huntGuess() {
        // Find cell with highest number of possible configurations
        Guess bestGuess = new Guess();
        int bestCell = shipConfigs[0][0];
        for (int i = 0; i < world.numColumn; i++) {
            for (int j = 0; j < world.numRow; j++) {
                System.out.print(shipConfigs[i][j]);
                // If this cell is better, update bestGuess and bestCell
                if (shipConfigs[i][j] > bestCell) {
                    bestCell = shipConfigs[i][j];
                    bestGuess.row = i;
                    bestGuess.column = j;
                }
            }
        }
        System.out.println(bestGuess);
        madeGuesses.add(bestGuess);
        return bestGuess;
    }

    /**
     * @description Make guess whilst in targeting mode
     * @return Guess the guess to be made
     **/
    private Guess targetGuess() {
        // From Part B
        Guess g = nextGuess.get(0);
        // Adds the guess to the madeGuesses list
        madeGuesses.add(g);
        // Removing the guess made from the unmadeGuesses list
        for(int i = 0; i < unmadeGuesses.size(); i++)
        {
            if(g.row == unmadeGuesses.get(i).row && g.column == unmadeGuesses.get(i).column)
                unmadeGuesses.remove(i);
        }
        // Dequeuing and making that guess
        return nextGuess.remove(0);
    }

    /**
     * @description Check if a ship has been sunk
     * @param World.ShipLocation a ShipLocation object (obtained from the World)
     * @return boolean whether the ship has been sunk
     **/
    private boolean isShipSunk(World.ShipLocation sl) {
        // Loop over all of the ship's coordinates
        for (World.Coordinate c : sl.coordinates) {
            if(!world.shots.contains(c)) return false;
        }
        return true;
    }

    /**
     * @description Calculate all possible ship configuration counts
     * @return void
     **/
    private void calcShipConfigs() {
        // Initialise number of ship configurations for each square
        for (int i = 0; i < world.numColumn; i++) {
            for (int j = 0; j < world.numRow; j++) {
                int totalConfigs = 0;
                // Calculate possible configurations for each ship
                for (Ship s : enemyShips) {

                    //System.out.print(s.toString());
                    // Calculate configurations in each axis separately
                    // Scans the current row for any hit cells
                    int horizontalEndingIndex = 0;
                    int horizontalStartingIndex = 0;
                    // Scanning to the right of the current cell
                    for(int k = j; k < world.numColumn; k++)
                    {
                        horizontalEndingIndex = k;
                        Guess g = new Guess();
                        g.row = i;
                        g.column = k;
                        if(inMadeGuesses(g)) break;
                    }
                    // Scanning to the left of the current cell
                    for(int k = j; k >= 0; k--)
                    {
                        horizontalStartingIndex = k;
                        Guess g = new Guess();
                        g.row = i;
                        g.column = k;
                        if(inMadeGuesses(g)) break;
                    }

                    int horizontalConfigs = calc1DShipConfig(horizontalStartingIndex, horizontalEndingIndex, j, s.len());
                    System.out.println("Horizontal : " + s.name() + horizontalConfigs);

                    // Scans the current column for any hit cells
                    int verticalEndingIndex = 0;
                    int verticalStartingIndex = 0;
                    // Scanning above the current cell
                    for(int k = i; k < world.numRow; k++)
                    {
                        verticalEndingIndex = k;
                        Guess g = new Guess();
                        g.row = k;
                        g.column = j;
                        if(inMadeGuesses(g)) break;
                    }
                    // Scanning below the current cell
                    for(int k = i; k >= 0; k--)
                    {
                        verticalStartingIndex = k;
                        Guess g = new Guess();
                        g.row = k;
                        g.column = j;
                        if(inMadeGuesses(g)) break;
                    }

                    int verticalConfigs = calc1DShipConfig(verticalStartingIndex, verticalEndingIndex, i, s.len());
                    System.out.println("Vertical: " + s.name() + verticalConfigs);

                    // Calculate total
                    int configs = horizontalConfigs + verticalConfigs;
                    totalConfigs += configs;

                }
                shipConfigs[i][j] = totalConfigs;
            }
        }
    }

    /**
     * @description Calculate the possible configurations of one ship in one axis
     * @param startingIndex space between the ship position and the next hit or end of row
     * @param endingIndex space between the ship position and the next hit or end of row
     * @param position the postion of the ship in the row/column
     * @param size the size of the ship
     * @return int the number of configurations
     **/
    private int calc1DShipConfig(int startingIndex, int endingIndex, int position, int size) {

        int space = endingIndex - startingIndex;
        position = position - startingIndex;
        int longSide;
        int shortSide;
        int configs;

        // Calculate room on either side of target square
        if (position > ((space - 1) / 2)) {
            longSide = position;
            shortSide = (space - 1) - position;
        } else {
            longSide = (space - 1) - position;
            shortSide = position;
        }

        // Calculate number of configs based on size and position in row/column
        if (space > size) {
            // If the row/column is longer than the ship, calculate configurations
            if ((longSide + 1) >= size) {
                configs = shortSide + 1;
            } else {
                configs = shortSide;
            }
        } else if (space == size) {
            // If the row/column is the same size as the ship, there is 1 configuration
            configs = 1;
        } else {
            // If the row/column is shorter than the ship, there are no configurations
            configs = 0;
        }

        // Make sure number of configurations isn't greater than the size of ship
        if (configs > size) configs = size;

        return configs;
    }

    // Checks if Guess g is in the arraylist madeGuesses
    private boolean inMadeGuesses(Guess g)
    {
        for(Guess current : madeGuesses)
        {
            if(g.row == current.row && g.column == current.column)
            {
                return true;
            }
        }

        return false;
    }
    
    //Checks if Guess g is in the arraylist nextGuess
    private boolean inNextGuess(Guess g)
    {
        for(Guess current : nextGuess)
        {
            if(g.row == current.row && g.column == current.column)
            {
                return true;
            }
        }
        
        return false;
    }

} // end of class MonteCarloGuessPlayer
