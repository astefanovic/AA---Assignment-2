package player;

import java.util.Scanner;
import java.lang.Math;
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
        Answer answer = new Answer();

        // Loop over each ship
        for (sl : world.shipLocations) {
            // Loop over each coordinate the ship occupies
            for (c : sl.coordinates) {
                // Check if the guess matches the coordinate
                if (guess.row == c.row && guess.column == c.column) {
                    answer.isHit = true;
                    if (/* TODO: check if ship has been sunk */) {
                        answer.shipSunk = sl.ship;
                    }
                    return answer;
                }
            }
        }

        return answer;
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
