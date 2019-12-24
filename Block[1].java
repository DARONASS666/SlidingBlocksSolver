import com.sun.jdi.InvalidTypeException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

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

    //DEBUG VARIABLES AND RUNTIME OF FUNCTIONS
    //debug controllers
    private static boolean DEBUG1 = false, DEBUG2 = false, DEBUG3 = false;
    public static boolean getBLOCK1(){ return DEBUG1; }
    public static void setDebug(boolean one, boolean two, boolean three){
        DEBUG1 = one; DEBUG2 = two; DEBUG3 = three;
        if(DEBUG1)
            setCount(0);
        if(DEBUG3)
            resetTimes();
    }
    private static int BlockCount;
    public static void setCount(int count){ BlockCount = count; }
    public static int getCount(){ return BlockCount; }
    public static void printCount(){
        if(DEBUG1)
            System.out.println("Number of Blocks created: " + BlockCount);
    }

    private static long start;
    private static long EmptyTime, canMoveTime, moveTime;
    public static void resetTimes(){ EmptyTime = 0; canMoveTime = 0; moveTime = 0; }
    public static long getShouldBeEmpty(){ return EmptyTime; }
    public static long getCanMove(){ return canMoveTime; }
    public static long getMove(){ return moveTime; }
    public static void printTimes() {
        if (DEBUG3) {
            System.out.println("\nTotal runtimes of relevant methods in (Block): ");
            System.out.println("shouldBeEmpty(): " + EmptyTime / 1000000 + "\ncanMove(): "
                    + canMoveTime / 1000000 + "\nmove(): " + moveTime / 1000000);
        }
    }

    /**
     * default constructor of Block. makes all important assignments and allocates necessary space with
     * necessary validations
     * @param size represents size of block that this block is assigned to.
     * @param upperLeft represents upperLeft coordinates of the Block
     * @param myNumber represents number that belongs to this block in HasMap of integers and Blocks
     * @param myBoardSize represents Board size that this Block is in.
     * @throws InvalidTypeException If there is a Type mismatch during assignment
     */
    public Block(coordinates size, coordinates upperLeft, int myNumber, coordinates myBoardSize)
            throws InvalidTypeException {

        if(!myBoardSize.isValidSize()) throw new IndexOutOfBoundsException("Board size is inValid");
        if (!size.isValidSize()) throw new IndexOutOfBoundsException("Invalid size of Block: "
                + size.getRow() + " x " + size.getCol());
        this.size = new coordinates(size);
        if(this.size.getRow() > myBoardSize.getRow() || this.size.getCol() > myBoardSize.getCol())
            throw new IndexOutOfBoundsException ("Board size is smaller than this Block");

        if (upperLeft.isValidPosition()) {
            this.myNumber = myNumber;
            this.upperLeft = new coordinates(upperLeft);
            this.bottomRight = this.upperLeft.addCoordinates(this.size);
            this.myBoardSize = new coordinates(myBoardSize);
        } else throw new IndexOutOfBoundsException("Invalid inputs given to upperleft");
    }

    /**
     * @return upperLeft coordinates for this Block
     */
    public coordinates getUpperLeft() {
        return upperLeft;
    }

    /**
     * @return size as coordinates for this Block
     */
    public coordinates getSize() { return size; }

    /**
     * @return the size of the Board that this Block belongs to
     */
    public coordinates getMyBoardSize() {
        return myBoardSize;
    }

    /**
     * @return bottomRight coordinates of this Block
     */
    public coordinates getBottomRight() {
        return bottomRight;
    }

    /**
     * @return the number that this Block was assigned
     */
    public int getMyNumber() {
        return myNumber;
    }

    /**
     *takes new coordinates and assigns this Blocks row and column to those and updates BottomRight afterwards
     * @param newCoordinates new coordinates that this Block will use as its position
     */
    private void updateCoordinates(coordinates newCoordinates) {
        this.upperLeft.setRow(newCoordinates.getRow());
        this.upperLeft.setCol(newCoordinates.getCol());
        this.updateBottomRight();
    }

    /**
     * This updates BottomRight and should only fire when upperLeft coordinates are updated, although no problem
     * occurs when it is called by accident. Assertion is for validation of coordinates
     */
    private void updateBottomRight() {
        this.bottomRight.setRow(this.upperLeft.getRow() + this.size.getRow());
        this.bottomRight.setCol(this.upperLeft.getCol() + this.size.getCol());

        assert (this.bottomRight.isValidPosition() && this.bottomRight.getRow() <
                this.myBoardSize.getRow() && this.bottomRight.getCol() < this.myBoardSize.getCol());
        assert (this.bottomRight.getCol() == this.upperLeft.getCol() + this.size.getCol()
        && this.bottomRight.getRow() == this.upperLeft.getRow() + this.size.getRow());
    }

    /**
     * Most important method of this class. Creates a copy of this Block. removes the copy from the HashSet Blocks.
     * removes the copy from the HashMap.removes the Key from the KeySet as well and removes coordinates from the
     * empty spaces. proceeds to make movement if possible, and updates the changes on the copy.Re-adds the coordinates
     * to the empty spaces and re-adds to the HashSet of Blocks and HashMaps of Blocks
     * @param direction direction that the Block should be moved in
     * @param blocks HashSet of Blocks
     * @param availableSpaces the HashSet of empty spaces
     * @param allBlocks the HashMap of Blocks
     * @return true if Block was moved successfully , false otherwise
     * @throws Exception
     */
    public boolean moveBlock(int direction, HashSet<Block> blocks, HashSet<coordinates> availableSpaces
            , HashMap<Integer , Block> allBlocks) throws Exception {
        if(!checkIfEmpty(direction , availableSpaces)) return false;
        Block b = new Block(this.size, this.upperLeft, this.getMyNumber(), this.myBoardSize);
        blocks.remove(b);
        allBlocks.remove(b.hashCode() , b);
        removeKeyFromHashMap(allBlocks , b.hashCode());
        b.removeCoordinates(availableSpaces);

        if (direction == UP) {
                b.updateCoordinates(b.oneUp());
        } else if (direction == RIGHT) {
                b.updateCoordinates(b.oneRight());
        } else if (direction == DOWN) {
                b.updateCoordinates(b.oneDown());
        } else if (direction == LEFT) {
                b.updateCoordinates(b.oneLeft());
        }
        b.addCoordinates(availableSpaces);
        blocks.add(b);
        allBlocks.put(b.hashCode() , b);
        return  true;
    }

    /**
     * makes sure to not go out of Bounds, if there is a risk , returns same coordinates
     * @return the one up coordinates from where upperLeft of this Block is
     */
    public coordinates oneUp() throws Exception{
//        int riskyNumber = this.upperLeft.getRow() - 1;
//        if (riskyNumber >= 0) {
//            return new coordinates(riskyNumber, this.upperLeft.getCol());
//        } else
//            return this.upperLeft;
        if(this.upperLeft.getRow() > 0)
            return this.upperLeft.addCoordinates(coordinates.UP);
        return this.upperLeft;
    }

    /**
     * makes sure to not go out of Bounds, if there is a risk , returns same coordinates
     * @return the right coordinates from where upperLeft of this Block is
     */
    public coordinates oneRight() throws Exception{
//        int riskyNumber = this.bottomRight.getCol() + 1;
//        if (riskyNumber <= this.myBoardSize.getCol()) {
//            return new coordinates(this.upperLeft.getRow(), this.upperLeft.getCol() + 1);
//        } else
//            return this.upperLeft;
        if(this.bottomRight.getCol() < myBoardSize.getCol())
            return this.upperLeft.addCoordinates(coordinates.RIGHT);
        return this.upperLeft;
    }

    /**
     * makes sure to not go out of Bounds, if there is a risk , returns same coordinates
     * @return the one down coordinates from where upperLeft of this Block is
     */
    public coordinates oneDown() throws Exception{
//        int riskyNumber = this.bottomRight.getRow() + 1;
//        if (riskyNumber <= this.myBoardSize.getRow()) {
//            return new coordinates(this.upperLeft.getRow() + 1, this.upperLeft.getCol());
//        } else
//            return this.upperLeft;
        if(this.bottomRight.getRow() < this.myBoardSize.getRow())
            return this.upperLeft.addCoordinates(coordinates.DOWN);
        return this.upperLeft;
    }

    /**
     * makes sure to not go out of Bounds, if there is a risk , returns same coordinates
     * @return the left coordinates from where upperLeft of this Block is
     */
    public coordinates oneLeft() throws Exception{
//        int riskyNumber = this.upperLeft.getCol() - 1;
//        if (riskyNumber >= 0) {
//            return new coordinates(this.upperLeft.getRow(), riskyNumber);
//        }
//        return this.upperLeft;
        if(this.upperLeft.getCol() > 0)
            return this.upperLeft.addCoordinates(coordinates.LEFT);
        return this.upperLeft;
    }

    /**
     * iterates through the KeySet of the HashMap and looks to see if the HashCode is there and if it is, removes it
     * from the KeySet.
     * @param allBlocks Maps an integer to a Block because each Block is assigned an integer
     * @param hash represents the HashCode that is to be removed the Map
     * @return true if Key is Successfully removed , false otherwise
     */
    public boolean removeKeyFromHashMap(HashMap<Integer , Block> allBlocks , int hash){
        int size = allBlocks.size();
        Iterator<Integer> iterator = allBlocks.keySet().iterator();
        while(iterator.hasNext()) {
            Integer integer = iterator.next();
            if(integer == hash) {
            iterator.remove();
            }
        }
        return allBlocks.keySet().size() < size;
    }

    /**
     * if the hashSet doesn't contain all the coordinates to move to then this means its occupied.This method
     * checks the direction and checks if its possible for the current block to be moved there by using a for loop
     * and checking if the HasSet 'empty' has empty coordinates in the location where the block would be if it was moved
     * @param direction identifies left right up down
     * @param empty   hashSet of available spaces to go to
     * @return false if you cant move this ( block ) to the desired direction. true if u can
     */
    private boolean checkIfEmpty(int direction, HashSet<coordinates> empty) {
        coordinates c;
        if (direction == UP) {
            for (int i = 0; i < this.size.getCol(); i++) {
                c = new coordinates(this.upperLeft.getRow() - 1, this.upperLeft.getCol() + i);
                if (!empty.contains(c)) {
                    return false;
                }
            }
        } else if (direction == RIGHT) {
            for (int i = 0; i < this.size.getRow(); i++) {
                c = new coordinates(this.upperLeft.getRow() + i, this.bottomRight.getCol());
                if (!empty.contains(c)) {
                    return false;
                }
            }
        } else if (direction == DOWN) {
            for (int i = 0; i < this.size.getCol(); i++) {
                c = new coordinates(this.bottomRight.getRow(), this.upperLeft.getCol() + i);
                if (!empty.contains(c)) {
                    return false;
                }
            }
        } else if (direction == LEFT) {
            for (int i = 0; i < this.size.getRow(); i++) {
                c = new coordinates(this.upperLeft.getRow() + i, this.upperLeft.getCol() - 1);
                if (!empty.contains(c)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * use he size and position of this Block to basically find them in the HasSet and add coordinates into the HashSet
     * this would mean that we are removing the coordinates where the Block takes up space so by adding new coordinates
     * we are freeing space in the HasSet technically. This is the opposite of the addCoordinates method.
     * @param empty represents the HashSet of all the empty spaces in a Board that represents same Board as the one
     *               that contains all the Blocks that this Block is found in
     */
    void removeCoordinates(HashSet<coordinates> empty){
        for (int i = 0 ; i < this.size.getRow() ; i++){
            for (int j = 0 ; j < this.size.getCol() ; j++){
               empty.add(new coordinates(this.upperLeft.getRow() + i , this.upperLeft.getCol() + j));
            }
        }
    }

    /**
     * Use the size and position of this given Block and at each available position (as a coordinate) add coordinates
     * of the Block by removing the empty coordinates from the HasSet. This is the opposite of removeCoordinates
     * @param empty represents the HashSet of all the empty spaces in a Board that represents same Board as the one
     *              that contains all the Blocks that this Block is found in
     */
    void addCoordinates(HashSet<coordinates> empty){
        for (int i = 0 ; i < this.size.getRow() ; i++){
            for (int j = 0 ; j < this.size.getCol() ; j++){
                empty.remove(new coordinates(this.upperLeft.getRow() + i , this.upperLeft.getCol() + j));
            }
        }
    }

    /**
     * a Block is uniquely identified by its size and upperLeft coordinates , since all pieces are rectangular, and
     * we make sense of a block by returning those as a string
     * @return all the important position information in a string
     */
    @Override
    public String toString() {
        return this.size.getRow() + " " + this.size.getCol() + " " + this.upperLeft;
    }

    /**
     *
     * This function makes a deepClone of a Block
     * @return a new Block with same characteristics as this Block
     * @throws Exception left unused
     */
    Block deepClone() throws Exception{
        return new Block(this.size , this.upperLeft , this.getMyNumber() , this.myBoardSize);
    }

    /**
     * compares position and size, as well as HashCode. Notice how two identical Blocks that are not in the same
     * position are two separate entities and therefore have different HashCodes, unlike coordinates where two having
     * same row and col are equal.
     * @param other block that will be compared with this block
     * @return true if they are equal , false otherwise
     */
    @Override
    public boolean equals(Object other){
        if(!(other instanceof Block)){
            return false;
        }
        Block b = (Block) other;
        return this.hashCode() == b.hashCode() && this.size.equals(b.size)
        && this.upperLeft.equals(b.upperLeft) && this.bottomRight.equals(b.bottomRight);
    }

    /**
     * determines unique hashCode based on position of block and size of block
     * @return hashCode that was created
     */
    @Override
    public int hashCode() {
        return (int) (Math.pow( size.getCol(),3)
                + size.getRow()*3 + Math.pow(upperLeft.getRow() , 3) + Math.pow(upperLeft.getCol() , 5));
    }
}
//997*2+1