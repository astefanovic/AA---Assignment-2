package player;

import java.util.Scanner;
import world.World;
import java.util.ArrayList;
import java.util.Stack;
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
    private Stack<Guess> nextGuess = new Stack<Guess>();
    private ArrayList<Guess> madeGuesses = new ArrayList<Guess>();

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
        // If the stack is empty, not in targeting mode
        if(nextGuess.empty())
        {
            // Generate random number
            int randIndex = (int)(Math.random() * (unmadeGuesses.size() - 1));
            // Add the guess to the made guesses list
            madeGuesses.add(unmadeGuesses.get(randIndex));
            // Remove that index from the unmade guesses and return it
            return unmadeGuesses.remove(randIndex);
        }
        else
        {
            Guess g = nextGuess.peek();
            // Adds the guess to the madeGuesses list
            madeGuesses.add(g);
            // Removing the guess made from the unmadeGuesses list
            for(int i = 0; i < unmadeGuesses.size(); i++)
            {
                if(g.row == unmadeGuesses.get(i).row && g.column == unmadeGuesses.get(i).column)
                    unmadeGuesses.remove(i);
            }
            // Popping off the stack and making that guess
            return nextGuess.pop();
        }
    } // end of makeGuess()


    @Override
    public void update(Guess guess, Answer answer) {
        // If the guess hit, add all the surrounding cells to the stack
        if(answer.isHit) 
        {            
            Guess west = new Guess();
            west.row = guess.row;
            west.column = guess.column - 1;
            // If the guess is made or out of bounds, dont add to stack
            if(!inMadeGuesses(west) && west.column >= 0) nextGuess.push(west);
            
            Guess south = new Guess();
            south.row = guess.row - 1;
            south.column = guess.column;
            if(!inMadeGuesses(south) && south.row >= 0) nextGuess.push(south);
            
            Guess east = new Guess();
            east.row = guess.row;
            east.column = guess.column + 1;
            if(!inMadeGuesses(east) && east.column < world.numColumn) nextGuess.push(east);
            
            Guess north = new Guess();
            north.row = guess.row + 1;
            north.column = guess.column;
            if(!inMadeGuesses(north) && north.row < world.numRow) nextGuess.push(north);
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

} // end of class GreedyGuessPlayer
