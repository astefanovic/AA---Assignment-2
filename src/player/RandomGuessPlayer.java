package player;

import java.util.Scanner;
import java.lang.Math;
import java.util.ArrayList;
import world.World;

/**
 * Random guess player (task A).
 * Please implement this class.
 *
 * @author Youhan, Jeffrey
 */
public class RandomGuessPlayer implements Player{

    private World world;
    private ArrayList<Guess> unmadeGuesses = new ArrayList<Guess>();

    @Override
    public void initialisePlayer(World world) {
        // To be implemented.
        this.world = world;

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
                    if (isShipSunk(sl)) answer.shipSunk = sl.ship;
                    return answer;
                }
            }
        }

        return answer;
    } // end of getAnswer()

    @Override
    public Guess makeGuess() {
        // Generate random number
        int randIndex = (int)(Math.random() * (unmadeGuesses.size() - 1));
        // Remove that index from the unmade guesses and return it
        return unmadeGuesses.remove(randIndex);
    } // end of makeGuess()


    @Override
    public void update(Guess guess, Answer answer) {
        // To be implemented.
    } // end of update()


    @Override
    public boolean noRemainingShips() {
        // To be implemented.

        // dummy return
        return true;
    } // end of noRemainingShips()

    // Check if the ship has been sunk
    private boolean isShipSunk(World.ShipLocation sl) {
        // Loop over all of the ship's coordinates
        for (World.Coordinate c : sl.coordinates) {
            // Check if coordinate has been hit already
            if (!world.shots.contains(c)) {
                return false;
            }
        }
        return true;
    }

} // end of class RandomGuessPlayer
