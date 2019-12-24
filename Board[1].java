import java.util.*;

import static java.lang.System.exit;
import static java.lang.System.setSecurityManager;

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
    private HashMap<Integer, Block> allBlocks; //a collection of all the blocks that have a unique number
    private HashSet<Block> blocks; // Hash set of blocks coming in from solver
    private HashSet<coordinates> emptySpaces;
    private int priority;

    public int getPrior(){ return priority; }

    public Board getParent() {
        return parent;
    }

    public coordinates getBlockMovedDir() {
        return blockMovedDir;
    }

    public Block getBlockMoved() {
        return blockMoved;
    }

    public HashSet<coordinates> getEmptyspaces() {
        return emptySpaces;
    }

    public HashMap<Integer, Block> getAllBlocks() {
        return allBlocks;
    }

    public HashSet<Block> getBlocks() {
        return blocks;
    }

    public coordinates getSize() {
        return size;
    }

    /**
     * general constructor of board. Allocates all needed information in memory and copies over from hashSets to
     * private variable hashSets.
     * @param configuration the given hashSet of blocks that represent the blocks in the board
     * @param empty the given hashSet of coordinates that represent all the empty spaces in the board
     * @param size represents the size of the board
     * @throws Exception
     */
    public Board(HashSet<Block> configuration, HashSet<coordinates> empty ,  coordinates size ) throws Exception {
        blocks = new HashSet<>(1000);
        emptySpaces = new HashSet<>(1000);
        allBlocks = new HashMap<>(1000);

        this.size = new coordinates(size);

        Iterator it = configuration.iterator();

        while (it.hasNext()) {
            Block b = (Block) it.next();
            Block addme = new Block(b.getSize(), b.getUpperLeft(), b.getMyNumber(), b.getMyBoardSize());
            blocks.add(addme);
            allBlocks.put(addme.hashCode() , addme);
        }

        Iterator iterator = empty.iterator();
        while (iterator.hasNext()) {
            coordinates c = (coordinates) iterator.next();
            coordinates addme = new coordinates(c.getRow(), c.getCol());
            emptySpaces.add(addme);
        }
    }

    /**
     * iterates through the blocks of the goal board and checks to see if the current board contains every single
     * block of the goal board
     * @param goalBoard it is the final configuration board , the goal board
     * @return true if the current board equals goal board , false otherwise
     */
    public boolean isSolved(Board goalBoard) {
        Iterator it = goalBoard.blocks.iterator();
        while (it.hasNext()) {
            Block current = (Block) it.next();
            if (!blocks.contains(current)) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param prev reference to the previous board
     * @param moved reference to the newly moved board
     * @param direction reference to the direction that the last block was moved
     */
    //sets info to this if was not there , only fires if it wasnt previously set
    public void previousInstance(Board prev, Block moved, coordinates direction) {
        if (parent == null && blockMoved == null && blockMovedDir == null) {
            parent = prev;
            blockMoved = moved;
            blockMovedDir = direction;
        }
    }

    /**
     * find the block and take the desired destination and move the block in that direction.update empty spaces and
     * blocks if movement is successful. validate all positions and destination position before moving
     * @param findMe this is the block that will be used in the moving process
     * @param destination this is the destination coordinate where we want to move our block
     * @return new board if movement is successful , null otherwise
     * @throws Exception
     */
    public Board moveOneBlock(Block findMe, coordinates destination) throws Exception {
        boolean debug = false;
        Board returnMe = this.deepClone();
        if(!returnMe.equals(this)) {
            if (debug)
            System.out.println("CLONE DOESNT EQUAL ORIGINAL");
        }
        returnMe.parent = this;
        Block moveThisBlock = returnMe.getBlock(findMe.getSize().getRow() ,
                findMe.getSize().getCol() , findMe.getUpperLeft() , findMe.hashCode());

        if(!returnMe.allBlocks.containsValue(moveThisBlock)) System.out.println("Allblocks of returnme doesnt contain block");

        if (destination.isValidPosition() && destination.getCol() < returnMe.size.getCol() &&
                destination.getRow() < returnMe.size.getRow()) {
            //check if this block is found in the collection of blocks in this board
            if (returnMe.blocks.contains(moveThisBlock)) {
                boolean success = false;
                //4 cases : up down left right
                if (moveThisBlock.oneUp().equals(destination)) {
                    success = moveThisBlock.moveBlock(UP, returnMe.blocks, returnMe.emptySpaces , returnMe.allBlocks);
                } else if (moveThisBlock.oneDown().equals(destination)) {
                    success = moveThisBlock.moveBlock(DOWN, returnMe.blocks, returnMe.emptySpaces , returnMe.allBlocks);
                } else if (moveThisBlock.oneLeft().equals(destination)) {
                    success = moveThisBlock.moveBlock(LEFT, returnMe.blocks, returnMe.emptySpaces , returnMe.allBlocks);
                } else if (moveThisBlock.oneRight().equals(destination)) {
                    success = moveThisBlock.moveBlock(RIGHT, returnMe.blocks, returnMe.emptySpaces , returnMe.allBlocks);
                }
                if (success) {
                    returnMe.blockMoved = moveThisBlock;
                    returnMe.blockMovedDir = destination;
                } else {
                    return null;
                }
            }
            return returnMe;
        }
        return null;
    }

    /**
     * uses the info of the block and looks for the Block in the Board
     * @param height however many the block has
     * @param width however many columns the block has
     * @param row represents the upperLeft coordinate's row of the block.
     * @param col represents the upperLeft coordinate's column of the block
     * @param myNumber the number that the block was assigned in the HashMap
     * @return the block if its is found , null otherwise
     * @throws Exception
     */
    public Block getBlock(int height, int width, int row, int col , int myNumber) throws Exception {

        Block toReturn = allBlocks.get(myNumber);

        if (toReturn == null) {
            throw new Exception("messed up bud");
        }
        return toReturn;
    }

    /**
     * uses the info of the block and looks for the Block in the Board
     * @param height however many the block has
     * @param width however many columns the block has
     * @param pos represents the upperLeft coordinates of the block.
     * @param myNumber the number that the block was assigned in the HashMap
     * @return the block if its is found , null otherwise
     * @throws Exception
     */
    public Block getBlock(int height, int width, coordinates pos , int myNumber) throws Exception {
        return getBlock(height, width, pos.getRow(), pos.getCol() , myNumber);
    }
    //used for debugging , print board

    /**
     * print the board in a double for loop and a 2 dimensional array. Each block coordinate will print a number
     * if there is an empty space ,  print null. Use Block's number that was assigned to print in each box of the array
     */
    public void printBoard() {
        String[][] board = new String[size.getRow()][size.getCol()];
        Iterator iterator = this.blocks.iterator();

        while (iterator.hasNext()) {
            Block currentBlock = (Block) iterator.next();
            int currentNumber = currentBlock.getMyNumber();

            for (int i = currentBlock.getUpperLeft().getRow(); i < currentBlock.getBottomRight().getRow(); i++) {
                for (int j = currentBlock.getUpperLeft().getCol(); j < currentBlock.getBottomRight().getCol(); j++) {
                    board[i][j] = currentNumber + "";
                    if (currentNumber % 10 == 1 && currentNumber % 100 != 11) board[i][j] += "st";
                    else if (currentNumber % 10 == 2) board[i][j] += "nd";
                    else if (currentNumber % 10 == 3) board[i][j] += "rd";
                    else board[i][j] += "th";
                }
            }
        }
        for (int i = 0; i < this.size.getRow(); i++) {
            for (int j = 0; j < this.size.getCol(); j++) {
                System.out.print("[" + board[i][j] + "] ");
            }
            System.out.println();
        }
    }

    //we have to check if boards are the same. we check the size and we check if all the blocks in there are in the
    //same spots

    /**
     * equals compares every single Hashable and potentially dynamic part of 2 Boards to ensure that the two are
     * identical if the method returns true.
     * @param other is a Board that will be compared with this Board
     * @return true if this Board is equal to other Board , false otherwise
     */
    public boolean equals(Object other) {
        boolean debug = false;
        boolean debugBoard = false;
        int error = 0;
        if(debug) {
        System.out.println("equals function debug mode:on , checking equality between (this and other): " + this.hashCode() + " and " + other.hashCode());
        }
        if (!(other instanceof Board)) {
        if (debug)    System.out.println("ERROR: OTHER NOT INSTANCE OF THIS");
            return false;
        }
        if (((Board) other).blocks.size() != blocks.size() ){
            error++;
            if (debug) System.out.println("ERROR : BLOCKS.SIZES DONT MATCH!!");

        }
        if  (((Board) other).allBlocks.size() != allBlocks.size() ){
            error++;
            if (debug) System.out.println("ERROR : ALLBLOCKS.SIZES DONT MATCH!!");

               }
        if(((Board) other).emptySpaces.size() != emptySpaces.size()) {
            error++;
            if (debug) System.out.println("ERROR : EMPTYSPACES.SIZES DONT MATCH!!");
        }
        for (Block b : ((Board) other).blocks) {
            if (!blocks.contains(b)) {
                error++;
                if (debug) System.out.println("ERROR : HASHSET OF BLOCKS DONT MATCH");
            }
        }
        for (coordinates c : ((Board) other).emptySpaces) {
            if (!emptySpaces.contains(c)) {
                error++;
                if (debug)  System.out.println("ERROR : EMPTY SPACES IN EQUALS HAVE MISMATCH");
            }
        }
        for (Block bb : ((Board) other).allBlocks.values()) {
            if (!allBlocks.values().contains(bb)) {
                error++;
                if (debug) {
                    System.out.println("ERROR : ALLBLOCKS IN EQUALS HAVE MISMATCH");
                }
            }
        }
        if(this.hashCode() != other.hashCode()){
            error++;
            if(debug) System.out.print("ERROR : THIS AND OTHER HAVE DIFFERENT HASHCODES");
        }
        if(debug){
            if(error == 0) System.out.println("NO ERRORS WERE FOUND");
            else {
                System.out.println("FOUND " + error + " DIFFERENT ERRORS");
                if(debugBoard){
                    System.out.println("Printing this then other back to back to view where the mismatch is");
                    System.out.println("Now Printing data on this Board...");
                    Solver.printDataOnBoard(this);
                    System.out.println("Now Printing data on other Board...");
                    Solver.printDataOnBoard((Board)other);
                    exit(666);
                }
            }
        }
        return error == 0 ;
    }

    // ERROR EXPLANATION: DEEPCLONE GIVING DIFFERENT HASHCODE FOR BLOCK 5 , AFTER IT WAS MOVED.
    //WHEN I COPY OVER ALLBLOCKS FROM THIS TO THE NEW ONE , THEY ARE NOT EQUAL
    //THE PIECE I MOVE BEFORE I CLONE THIS IS 5 , I THINK ITS CLONING THE ALREADY MOVED PIECE IN SOLVER
    //IN THAT CONTEXT IT WOULDNT MAKE SENSE

    /**
     * makes sure to copy every single detail over to a new Board with memory allocated in a new address.
     * @return a Board that is identical to this Board
     * @throws Exception
     */
    public Board deepClone() throws Exception {
        HashSet<Block> Blocks = new HashSet<>();
        HashSet<coordinates> Spaces = new HashSet<>();

        Iterator it = blocks.iterator();
        while (it.hasNext()) {
            Block b = (Block) it.next();
            Block addMe = new Block(b.getSize(), b.getUpperLeft(), b.getMyNumber(), b.getMyBoardSize());
            Blocks.add(addMe);
        }

        Iterator iterator = emptySpaces.iterator();
        while (iterator.hasNext()) {
            coordinates c = (coordinates) iterator.next();
            coordinates addMe = new coordinates(c.getRow(), c.getCol());
            Spaces.add(addMe);
        }
            coordinates size = new coordinates(this.size);
            return new Board(Blocks , Spaces , size );
            //return new Board(this.blocks , this.emptySpaces , this.size);
    }

    /**
     * HashCode is the multiplication of all the HashCodes of the Blocks in this Board
     * @return HashCode of this Block
     */
    @Override
    public int hashCode() {
        int hash = 1;
        for (Block block : this.blocks ) {
            hash *= block.hashCode();
        }
        return hash;
    }

    /**
     * if priority isn't 0 to begin with , update it then return it
     * @return the cost/priority from current Board to goal Board
     */
    public int getPriority(){
        if(this.priority != 0 ) {
            this.getCost(goal);
        }
        return this.priority;
    }

    /**
     * this method is necessary in order to be able to sort Boards in the priority queue based on their priority
     * @param other Board that is being compared with this
     * @return the difference between the priorities
     */
    public int compareTo(Board other){
        return this.getPriority() - other.getPriority();
    }

    //the lower priority the better

    /**
     *
     * @param goal
     */
    public void getCost(Board goal){
        if(this.priority != 0 ) System.out.println("somethings wrong , priority was not 0");
        int temp = 0;
        LinkedList<Block> checkMe = new LinkedList<>();    //changes maybe needed
        //PriorityQueue<Block> checkMe = new PriorityQueue<>();
        for( Block current : this.blocks){
            checkMe.add(current);
        }

        for(Block current: goal.getBlocks()){
            if(this.blocks.contains(current))
                checkMe.remove(current);
            else
                temp += this.Cost(checkMe, current);
        }
        priority = temp;
    }

    /**
     *
     * @param checkMe
     * @param other
     * @return
     */
    private int Cost(LinkedList<Block> checkMe, Block other) {
        int returnMe = Integer.MAX_VALUE;
        int currentMin;

        Block closest = null;
        for (Block b : checkMe) {
            if (b.getSize().getRow() != other.getSize().getRow() || b.getSize().getCol() != other.getSize().getCol())
                continue;

            currentMin = other.getUpperLeft().manhattanDist(b.getUpperLeft(), other.getUpperLeft());

            if (currentMin < returnMe) {
                returnMe = currentMin;
                closest = b;
            }
        }
        if (returnMe == Integer.MAX_VALUE)
            System.out.println("returning max value , goal board doesnt exist");
        checkMe.remove(closest);
        return returnMe;
    }

}