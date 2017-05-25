package player;

import java.util.Scanner;
import java.lang.Math;
import java.util.ArrayList;

import world.World;

/**
 * Monte Carlo guess player (task C).
 * Please implement this class.
 *
 * @author Youhan, Jeffrey
 */
public class MonteCarloGuessPlayer  implements Player{

    private World world;
    private ArrayList<Guess> unmadeGuesses = new ArrayList<Guess>();
    private int[][] shipConfigs;

    /**
     * @description perform any initialisation operations to start
     * @param world this player's world instance
     * @return void
     **/
    @Override
    public void initialisePlayer(World world) {
        this.world = world;
        shipConfigs = new int[world.numRow][world.numColumn];

        // Initialise number of ship configurations for each square
        for (int i = 0; i < world.numColumn; i++) {
            for (int j = 0; j < world.numRow; j++) {
                int totalConfigs = 0;
                // Calculate possible configurations for each ship
                for (World.ShipLocation sl : world.shipLocations) {

                    // Calculate configurations in each axis separately
                    int horizontalConfigs = calcConfigs(world.numRow, j, sl.ship.len());
                    int verticalCOnfigs = calcConfigs(world.numColumn, i, sl.ship.len());

                    // Calculate total
                    int configs = horizontalConfigs + verticalCOnfigs;
                    totalConfigs += configs;

                }
                shipConfigs[j][i] = totalConfigs;
            }
        }

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
        // To be implemented.

        // dummy return
        return null;
    } // end of makeGuess()


    @Override
    public void update(Guess guess, Answer answer) {
        // To be implemented.
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
     * @description Calculate the possible configurations of one ship in one axis
     * @param space the size of the row/column in question
     * @param position the postion of the ship in the row/column
     * @param size the size of the ship
     * @return int the number of configurations
     **/
    private int calcConfigs(int space, int position, int size) {

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

} // end of class MonteCarloGuessPlayer
