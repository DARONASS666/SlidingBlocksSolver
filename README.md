# SlidingBlocksSolver
a program written in Java to take in a file as input , construct a board and solve it as Chinese sliding blocks with Depth First Search, Breadth First Search and A* Search algorithms available to test.

New Version includes some basic visual results after a solution is found, to track between all the steps from the beginning to the end to visualize the given algorithm.
Later versions may include more such as constructing your own board on the JFrame yourself and add more functionality such as saving and loading solution etc.


AUTHOR: DARON ASSADOURIAN

B L O C K   C L A S S   W R I T E - UP

import com.sun.jdi.InvalidTypeException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;


public class Block {

    private static final int UP = 1;
    private static final int RIGHT = 2;
    private static final int DOWN = 3;
    private static final int LEFT = 4;
    private coordinates upperLeft;
    private coordinates size;
    private coordinates bottomRight;
    private int myNumber;
    private coordinates myBoardSize;
    private HashMap<Integer, Block> AllBlocks; //a collection of all the blocks that have a unique number


    public Block(coordinates size, coordinates upperLeft, int myNumber, coordinates myBoardSize) throws InvalidTypeException {
        TAKES SIZE OF BLOCK , POSITION AND A UNIQUE NUMBER.
        BASIC CONSTRUCTOR , CHECKS IF EVERYTHING IS VALID , INITIALIZATION OF DATA STRUCTURES ARE DONE HERE
        EXCEPTIONS ARE THROWN FOR BAD INPUT
    }
    public coordinates getUpperLeft()
    public coordinates getSize()
    public coordinates getMyBoardSize()
    public coordinates getBottomRight()
    public int getMyNumber()
    public HashMap<Integer, Block> getPieces()
    public void initializeAllBlocks()
    //LOOK FOR BLOCK IN HASHMAP OF BLOCKS AND RETURN IT, IF ITS NOT THERE THEN PUT IT THEN RETURN IT
    public Block getBlock(int height, int width, int xPos, int yPos)
    public Block getBlock(int height, int width, coordinates pos)
    private void updateCoordinates(coordinates newCoordinates)
    private void updateBottomRight()
    // CHECK WHAT DIRECTION THE BLOCK HAS TO MOVE IN ( UP DOWN LEFT RIGHT) AND ATTEMPT TO MOVE IT AND UPDATE EMPTY
        AND OCCUPIED SPACES AND RETURN TRUE IF MOVED FALSE IF NOT
    public boolean moveBlock(int direction, HashSet<Block> blocks, HashSet<coordinates> availableSpaces)
    public Block moveBlock(coordinates direction, LinkedList<coordinates> occupy, HashSet<coordinates> emptyspaces)
    public coordinates oneUp()
    public coordinates oneRight()
    public coordinates oneDown()
    public coordinates oneLeft()


    //CHECK IF UP DOWN LEFT RIGHT OF A BLOCK IS EMPTY , LOOPS OVER THE HEIGHT AND WIDTH TO BE SURE
    private boolean checkIfEmpty(int direction, HashSet<coordinates> empty)
    //checks if the spaces required for movement are actually empty
    //(should) should be the Pair[] generated from shouldBeEmpty()
    //(is) should be (Board.getSpaces()), the (Board)'s empty spaces
    public boolean canMove(coordinates[] direction, HashSet<coordinates> spaces){
    coordinates posRelativeToBlock(int direction)
    void removeCoordinates(HashSet<coordinates> empty)
    void addCoordinates(HashSet<coordinates> empty)
    public String toString()
    Block deepClone()
    public boolean equals(Object other)
    public boolean sameInfo(Object other)
    public int hashCode()



C O O R D I N A T E S   C L A S S

import com.sun.jdi.InvalidTypeException;

public class coordinates {
    public final static coordinates
            UP = new coordinates(0,-1),
            DOWN = new coordinates(0,1),
            LEFT = new coordinates(-1,0),
            RIGHT = new coordinates(1,0);

    public final static coordinates[] DIRS = { UP, DOWN, LEFT, RIGHT };
    private int row; // represents the row at which something starts
    private int col; // represents the column at which something starts
   public coordinates(int r , int c)
    public coordinates addCoordinates(Object obj)
    //makes sure that a size of a given block is not a 0 x 0
    public boolean isValidSize()
    int manhattanDist(coordinates lhs , coordinates rhs)
    double EuclideanDist(coordinates lhs , coordinates rhs)
    public boolean equals(Object obj)
   @Override public int hashCode()
   @Override
   public String toString()
    public void setRow(int row)
    public void setCol(int col)
    public boolean isValidPosition()
    public int getRow()
    public int getCol()
    public coordinates mult(int m)
    public void updateCoordinates(int r , int c)
    public void updateCoordinates(coordinates other)


B O A R D   C L A S S
public class Board implements Comparable<Board> {
    //should have multiple blocks in itself , contain the, some way
    //should have a reference to previous board
    private static final int UP = 1;
    private static final int RIGHT = 2;
    private static final int DOWN = 3;
    private static final int LEFT = 4;
    static Board goal = null;

    private Block blockMoved; // reference to block that was last moved
    private coordinates size; // size of the overall board
    private Board parent; //previous Board in configuration
    private coordinates blockMovedDir; //indicates which direction the last block was moved in
    private HashMap<Integer, Block> AllBlocks; //a collection of all the blocks that have a unique number
    private HashSet<Block> blocks; // Hash set of blocks coming in from solver
    private HashSet<coordinates> emptyspaces;
    private int priority;

    public int getPrior()
    public Board getParent()
    public coordinates getBlockMovedDir()
    public Block getBlockMoved()
    public HashSet<coordinates> getEmptyspaces()
    public HashMap<Integer, Block> getAllBlocks()
    public HashSet<Block> getBlocks()
    public coordinates getSize()
    public Board(HashSet<Block> configuration, HashSet<coordinates> empty ,  coordinates size )
    public boolean isSolved(Board goalBoard)
    //sets info to this if was not there , only fires if it wasnt previously set
    public void previousInstance(Board prev, Block moved, coordinates direction)
    public Board moveOneBlock(Block findMe, coordinates destination)
    public Block getBlock(int height, int width, int row, int col , int myNumber)
    public Block getBlock(int height, int width, coordinates pos , int number)
    //used for debugging , print board
    public void printBoard()
    //we have to check if boards are the same. we check the size and we check if all the blocks in there are in the
    //same spots
    public boolean equals(Object other)
    public Board deepClone()
    public int hashCode()
    public int getPriority()
    // @Override
    public int compareTo(Board other)
    //the lower priority the better
    public void getCost(Board goal)
    private int Cost(LinkedList<Block> checkMe, Block other)


S O L V E R   C L A S S

    private int rows, columns; //height and width of the board
    private HashSet<Board> visitedBoards; // keeps track of all previous board configurations
    private Board initialBoard; // initial state of the board
    private Board finalBoard; // final state of the board as in we need to have this at the end
    private HashSet<coordinates> emptyspaces; //to see where the empty spaces are
    public Solver(File initFile , File goalFile)
    //read the file line by line , make all the blocks and throw them in to intial configuration,
    // and take the line where it represents the goal and put that in a end goal configuration
    //initialize the height and width of the board(s) , set up all the blocks and throw them in a hashset of blocks

    public HashSet<Block> readFileAndInitialize(File file , String type)
    public HashSet<coordinates> calculateEmptySpaces(HashSet<Block> currentBoard)
    public Board solve()
    public Board DepthFirstSearch(Board currentBoard  , int recursiveLevel)
    public Board Solve()
    public void printPath(Board b)
    public static void printDataOnBoard(Board board)
    public Board BreadthFirstSearch(Board init)


    a description of your data structures for the tray and blocks;
    a list of the operations on blocks, boards, and the collection of boards seen earlier in the solution search;
    a list of alternative strategies for selecting moves and alternatives for representing blocks, boards, and the
    collection of already-seen boards;
    analysis of the advantages and disadvantages of each alternative in these lists;
    a description of the alternatives you adopted and of evidence you collected that support your choices.

    coordinates basically is a class that has two parts , an x component and a y component. they are limited
    to be minimum for size is (1,1)
    and minimum for position is (0,0)
    validation and updating and getters and print and every helper functions are provided

    block class represents a single block. all the blocks that are made remain the same amount and this individual block
    has a specific size and position in general , and given a hashSet and hashMap , the block updates its own coordinates
    and reinserts itself in those. there are some debugging options and printing functions for the block. the move block
    function is the function that handles all these changes and updates but a block class doesn't know anything about another
    block. The operations are basically done in constant time because there is no deep copy or iteration happening.

    Board Class represents a single board that has a hashSet and a hashMap of block as well as a hashSet of empty spaces
    all of these represent the same board and they have to stay consistent. Moving a block in this board is a little
    more comlicated than just updating a blocks info , we need to make sure to give a block correct hashset of blocks
    and hashset of coordinates so the appropriate changes take place , we also have to check if the movement is possible
    we also have to check if there arent blocks or directions that have bad input are possible. This block also
    holds a reference to the goal board and can generate a cost based on the manhattan distance from it to the goal
    configuration. The board also keeps track of empty spaces.The time complexity of operations in board are minimal
    but setting up hashsets and hashmaps and generating costs and deep clonin board consume the most time from this
    class. contains might be time consuming with o(n) (n being number of blocks) because it compares each block in the
    other board with this board.

    Solver Class has the most interesting features because it uses smaller pieces and makes sense out of it. the original
    approach to solving the problem was a depth first search , for each recursive move in the depth first function ,
    the possible moves are left up right down for a block. Every move was exhausted until a solution was found. Time consuming
    and inefficient for larger boards wasn't the main issue , for the larger ones , the search tree was so large
    that Depth first search would crash because of stack overflow. Another method was needed to fix this issue so i switched
    to breadth first search with a linked list to avoid recursion and ultimately stack overflow. Breadth first search
    more or less reduced the search time by 30% in larger ones and in some rare cases by 80%. But even a faster approach
    was done and this was converting the breadth first search to a A* search algorithm that relies on generating cost
    and finding minimum cost and pushing that into a priority queue. 99% of the different boards that were solved by A*
    were substantially faster than depth first and faster than breadth first. To avoid infinite loops in all 3 search
    algorithms, visited configurations were added to a hashset of visited board and they were dodged and not visited the
    second time. There could be some more initialization and minimization of data sturctures used to reduce the amount
    of copying and redundancy to ultimately execute the algorithm faster, although all the tests were passed in less
    than the required time to do so.
