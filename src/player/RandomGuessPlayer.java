package player;

import java.util.Scanner;
import world.World;

/**
 * Random guess player (task A).
 * Please implement this class.
 *
 * @author Youhan, Jeffrey
 */
public class RandomGuessPlayer implements Player{

    private world;

    @Override
    public void initialisePlayer(World world) {
        // To be implemented.
        this.world = world;
    } // end of initialisePlayer()

    @Override
    public Answer getAnswer(Guess guess) {
        // To be implemented.

        // dummy return
        return null;
    } // end of getAnswer()


    @Override
    public Guess makeGuess() {
        int guessRow;
        int guessColumn;
        boolean foundGuess;

        // Keep making guesses until we find one that hasn't already been made
        while(!foundGuess) {
            foundGuess = true;

            guessRow = (int)(Math.random() * world.numRow);
            guessColumn = (int)(Math.random() * world.numColumn);

            // Search for random coordinates in shots already taken
            for (c : world.shots) {
                if (c.row == guessRow && c.column == guessColumn) {
                    foundGuess = false;
                    break;
                }
            }
        }

        Guess guess = new Guess();
        guess.row = guessRow;
        guess.column = guessColumn;
        return guess;
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

} // end of class RandomGuessPlayer
