package player;

import java.util.Scanner;
import world.World;
import java.util.ArrayList;

/**
 * Greedy guess player (task B).
 * Please implement this class.
 *
 * @author Youhan, Jeffrey
 */
public class GreedyGuessPlayer implements Player{

    private World world;
    private ArrayList<Guess> unmadeGuesses = new ArrayList<Guess>();
    private boolean targetingMode = false;
    // Used as a Queue to order the next guesses
    private ArrayList<Guess> nextGuess = new ArrayList<Guess>();

    @Override
    public void initialisePlayer(World world) {
        this.world = world;

        // Initialise unmade guesses
        for (int i = 0; i < world.numRow; i++) {
            for (int j = 0; j < world.numColumn; j++) {
                // Guesses tiles that arent adjacent (only checks diagonals)
                if((i%2 == 0 && j%2 == 0) || (i%2 == 1 && j%2 == 1))
                {
                    Guess g = new Guess();
                    g.row = i;
                    g.column = j;
                    unmadeGuesses.add(g);
                }
            }
        }
    }

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


    @Override
    public Guess makeGuess() {
        if(!targetingMode)
        {
            // Generate random number
            int randIndex = (int)(Math.random() * (unmadeGuesses.size() - 1));
            // Remove that index from the unmade guesses and return it
            return unmadeGuesses.remove(randIndex);
        }
        else
        {
            
        }
    } // end of makeGuess()


    @Override
    public void update(Guess guess, Answer answer) {
        if(answer.isHit) 
        {
            targetingMode = true;
            //queue(new int[])
        }
            
    } // end of update()


    @Override
    public boolean noRemainingShips() {
        if(world.shipLocations.isEmpty()) return true;
        return false;
    } // end of noRemainingShips()

    // Check if the ship has been sunk
    private boolean isShipSunk(World.ShipLocation sl) {
        // Loop over all of the ship's coordinates
        for (World.Coordinate c : sl.coordinates) {
            if(!world.shots.contains(c)) return false;
        }
        return true;
    }
        
    private boolean queue(Guess g)
    {
        if(g.row < world.numRow && g.column < world.numRow && g.row >= 0 && g.column >= 0)
        {
            nextGuess.add(g);
        }
    }
    
    private Guess dequeue()
    {
        return nextGuess.remove(nextGuess.size() - 1);
    }

} // end of class GreedyGuessPlayer
