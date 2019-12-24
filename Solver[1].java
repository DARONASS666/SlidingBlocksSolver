import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.*;

public class Solver {

    private int rows, columns; //height and width of the board
    private HashSet<Board> visitedBoards; // keeps track of all previous board configurations
    private Board initialBoard; // initial state of the board
    private Board finalBoard; // final state of the board as in we need to have this at the end
    private HashSet<coordinates> emptyspaces; //to see where the empty spaces are
    static GraphicsConfiguration gc; //for displaying with swing

    /**
     *
     * @param initFile
     * @param goalFile
     * @throws Exception
     */
    public Solver(File initFile , File goalFile)throws Exception {
    //read the file line by line , make all the blocks and throw them in to intial configuration,
    // and take the line where it represents the goal and put that in a end goal configuration
        HashSet<Block> init = readFileAndInitialize(initFile , "initial");
        HashSet<Block> goal = readFileAndInitialize(goalFile , "goal");
        emptyspaces = calculateEmptySpaces(goal);

        //figure out where the empty spaces are and throw them into a hashset as well
        this.finalBoard = new Board(goal , emptyspaces, new coordinates(rows,columns));
        emptyspaces = calculateEmptySpaces(init);
        this.initialBoard = new Board(init,emptyspaces, new coordinates(rows,columns));

        Board.goal = finalBoard;
        visitedBoards = new HashSet<>(1000);
    }

    /**
     *
     * @param file
     * @param type
     * @return
     * @throws Exception
     */
    //initialize the height and width of the board(s) , set up all the blocks and throw them in a hashset of blocks
    public HashSet<Block> readFileAndInitialize(File file , String type) throws Exception{

        Path filePath = file.toPath();
        Scanner scanner = new Scanner(filePath);
        int count = 1;

        if( type.equals("initial")) {
            this.rows = scanner.nextInt();
            this.columns = scanner.nextInt();
            scanner.nextLine();
        }

        HashSet<Block> Blocks = new HashSet<>();
        while (scanner.hasNextLine()) {
            //length , width , row of upper , col of upper
           String parseMe =  scanner.nextLine();

            int[] numbers = Arrays.stream(parseMe.split(" ")).mapToInt(Integer::parseInt).toArray();
            //check empty if those coordinates are available and if successful remove coordinates from empty
            Blocks.add(new Block(new coordinates(numbers[0] ,
                    numbers[1]) , new coordinates(numbers[2] , numbers[3]) ,count , new coordinates(rows , columns)));
            count++;
        }
        return Blocks;
    }

    /**
     *
     * @param currentBoard
     * @return
     */
    public HashSet<coordinates> calculateEmptySpaces(HashSet<Block> currentBoard){
    HashSet<coordinates> empty = new HashSet<>();

    boolean [][] taken = new boolean[rows][columns];
        Iterator iterator = currentBoard.iterator();
        while (iterator.hasNext()){
            Block block = (Block)iterator.next();
            for (int i = 0  ; i < block.getSize().getRow(); i++){
                for (int j = 0 ; j < block.getSize().getCol(); j++){
                    taken[ block.getUpperLeft().getRow() + i][block.getUpperLeft().getCol() + j] = true;
                }
            }
        }
        for (int i = 0 ; i < rows ; i++){
            for (int j = 0 ; j < columns ; j++){
                if(taken[i][j] == false){
                    empty.add(new coordinates(i , j));
                }
            }
        }
    return empty;
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public Board solve() throws Exception{
        long start = System.nanoTime();
        Board b = DepthFirstSearch(initialBoard , 0);
        long finish = System.nanoTime();
        long timeElapsed = finish - start;
        if(b == null)  System.out.println("SOLUTION NOT FOUND");
            else System.out.println("SOLUTION FOUND");
        printPath(b,timeElapsed);
        return b;
    }

    /**
     *
     * @param currentBoard
     * @param recursiveLevel
     * @return
     * @throws Exception
     */
    public Board DepthFirstSearch(Board currentBoard  , int recursiveLevel) throws  Exception{
        boolean debug = false;
        if (currentBoard != null && currentBoard.isSolved(this.finalBoard)) {
            return currentBoard;
        }
        if(currentBoard == null) return null;

        Iterator it = currentBoard.getBlocks().iterator();

        while (it.hasNext()) {
            Block block = (Block) it.next();
           if(debug) {
               System.out.println("working with block:" + block.getMyNumber() + "  , recursive depth " + recursiveLevel);
               System.out.println("current Board before making any changes");
               printDataOnBoard(currentBoard);
           }
            Board temp;
            temp = currentBoard.moveOneBlock(block, block.oneUp());

          if(debug) {
              System.out.println("current Board AFTER making  changes");
              printDataOnBoard(currentBoard);
              System.out.println("temp = currentboard.move AFTER making  changes");
              if (temp != null)
                  printDataOnBoard(temp);
          }
            if (temp != null && !visitedBoards.contains(temp)) {
                visitedBoards.add(temp);
                temp =  DepthFirstSearch(temp , recursiveLevel + 1);
                if(temp != null && temp.isSolved(this.finalBoard)) return temp;
            }
            temp = currentBoard.moveOneBlock(block, block.oneRight());
          if(debug) {
              System.out.println("current Board AFTER making  changes");
              printDataOnBoard(currentBoard);
              System.out.println("temp = currentboard.move AFTER making  changes");
              if (temp != null)
                  printDataOnBoard(temp);
          }
            if (temp != null && !visitedBoards.contains(temp)) {
                visitedBoards.add(temp);
                temp =  DepthFirstSearch(temp , recursiveLevel + 1);
                if(temp != null && temp.isSolved(this.finalBoard)) return temp;
                }
            temp = currentBoard.moveOneBlock(block, block.oneDown());

          if(debug) {
              System.out.println("current Board AFTER making  changes");
              printDataOnBoard(currentBoard);
              System.out.println("temp = currentboard.move AFTER making  changes");
              if (temp != null)
                  printDataOnBoard(temp);
          }
            if (temp != null && !visitedBoards.contains(temp)) {
                visitedBoards.add(temp);
                temp =  DepthFirstSearch(temp , recursiveLevel + 1);
                if(temp != null && temp.isSolved(this.finalBoard)) return temp;
                    }
            temp = currentBoard.moveOneBlock(block, block.oneLeft());
          if(debug) {
              System.out.println("current+ Board AFTER making  changes");
              printDataOnBoard(currentBoard);
              System.out.println("temp = currentboard.move AFTER making  changes");
              if (temp != null)
                  printDataOnBoard(temp);
          }
            if (temp != null && !visitedBoards.contains(temp)) {
                visitedBoards.add(temp);
                if(temp!=null) {
                    temp = DepthFirstSearch(temp, recursiveLevel + 1);
                }if(temp != null && temp.isSolved(this.finalBoard)) return temp;
                    }
            }
            if(debug)
            System.out.println("exhausted every move before returning null");
        return null;
    }

    public Board Solve() throws Exception{
        long start = System.nanoTime();
        Board b = BreadthFirstSearch(initialBoard);
        long finish = System.nanoTime();
        long timeElapsed = finish - start;
        if(b == null)  System.out.println("SOLUTION NOT FOUND");
        else System.out.println("SOLUTION FOUND");
        printPath(b , timeElapsed / 1000000);
        displaySolution(b);
        return b;
    }

    public void printPath(Board b , long time){
        boolean debug = false;
        int count = 0 ;
        System.out.println("PRINTING PATH +OF SOLVED BOARD");
        if(b != null){
            while (b.getParent() != null){
                if(debug) System.out.println("AT STEP: " + count);
                b.printBoard();
                System.out.println("====================");
                b = b.getParent();
                count++;
            }
            if(debug) System.out.println("AT STEP: " + count);
            b.printBoard();
            count++;
            System.out.println("====================");
            System.out.println(count + " STEPS");
            System.out.println("TIME ELAPSED TO FIND SOLUTION:" + time + " milliseconds");
            System.out.println("DONE");
        }
    }

    public static void printDataOnBoard(Board board){
       System.out.println("===============================================");
        board.printBoard();
       Iterator it =  board.getBlocks().iterator();
        System.out.println("________________________________________________");
        System.out.println("printing Hashset<Block> blocks of this Board");
       while (it.hasNext()){
           Block temp = (Block) it.next();
           System.out.println("printing info on block: " + temp.getMyNumber());
           System.out.print("position: " + temp.getUpperLeft() + " size: " + temp.getSize());
           System.out.print(" HashCode: " + temp.hashCode());
           System.out.println();
       }
        Iterator iter =  board.getAllBlocks().values().iterator();
        System.out.println("_______________+_________________________________");
        System.out.println("printing HashMap<Integer,Block> AllBlocks of this Board");
        while (iter.hasNext()){
            Block temp = (Block) iter.next();
            System.out.println("printing info on block: " + temp.getMyNumber());
            System.out.print("position: " + temp.getUpperLeft() + " size: " + temp.getSize());
            System.out.print(" HashCode: " + temp.hashCode());
            System.out.println();
        }
        System.out.println("________________________________________________");
        System.out.println("printing HashSet<coordinates> emptyspaces of this Board");
        Iterator ite =  board.getEmptyspaces().iterator();
        while (ite.hasNext()){
            coordinates temp = (coordinates) ite.next();
            System.out.println("printing info on emptyspace " + temp.toString());
            System.out.print(" HashCode: " + temp.hashCode());
            System.out.println();
        }
        System.out.println("===============================================");
    }
    public Board BreadthFirstSearch(Board init) throws Exception{
       if(init.isSolved(this.finalBoard))
           return init;

        //LinkedList<Board> solveMe = new LinkedList<>();
        PriorityQueue<Board> solveMe = new PriorityQueue<>();

        solveMe.add(init);
        visitedBoards.add(init);
        Board current, board ;

        while(!solveMe.isEmpty()){

            current = solveMe.poll();
            for(Block b: current.getBlocks()){

                for(coordinates drctn: coordinates.DIRS){
                   // System.out.println("moving block " + b.getMyNumber() + " to " + b.getUpperLeft().addCoordinates(drctn) );
                    board = current.moveOneBlock(b, b.getUpperLeft().addCoordinates(drctn));

                    //if null, the move was illegal
                    if(board == null)
                        continue;

                    //checks to see if the (Board) was seen before
                    if(!visitedBoards.contains(board)){
                        if(board == null) System.out.println("looking at null");
                        visitedBoards.add(board);
                        solveMe.add(board);

                        //link to the parent
                        board.previousInstance(current, b, b.getUpperLeft().addCoordinates(drctn));
                    }
                    //check if (board) contains (goal)
                    if(board.isSolved(finalBoard)){
                        return board;
                    }
                   // board.printBoard();
                   // System.out.println();
                }
            }

        }
        return null;
    }

    public void displaySolution(Board finalBoard) throws Exception{
        //setup the environment to display using swing
        //final board coming in is already solved
    if(!finalBoard.isSolved(this.finalBoard)) throw new Exception("board is not solved");

    JFrame frame= new JFrame(gc);
        frame.setTitle("Sliding Blocks Solver");
        frame.setSize(600, 600);
        frame.setLocation(200, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    ArrayList<Board> doubly = new ArrayList<>();
    doubly.add(finalBoard);

    while (finalBoard.getParent() != null){
        doubly.add(finalBoard.getParent());
        finalBoard = finalBoard.getParent();
    }
    //now we have the ArrayList of boards , with the final solution as the first element and the initial as the last
        displaySolutionHelper(doubly , frame);

    }
    public void displaySolutionHelper(ArrayList<Board> doubly , JFrame frame) {
        Board startHere = doubly.get(doubly.size() - 1);
        int row = startHere.getSize().getRow();
        int col = startHere.getSize().getCol();
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(row, col));
        frame.add(mainPanel);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                JPanel panel = new JPanel();
                panel.setLayout(new GridLayout(1, 1, 1, 1));
                JButton component = new JButton("");
                panel.add(component, 0, 0);
                mainPanel.add(panel);
            }
        }

        String[][] board = new String[startHere.getSize().getRow()][startHere.getSize().getCol()];
        Iterator iterator = startHere.getBlocks().iterator();

        while (iterator.hasNext()) {
            Block currentBlock = (Block) iterator.next();
            int currentNumber = currentBlock.getMyNumber();

            for (int i = currentBlock.getUpperLeft().getRow(); i < currentBlock.getBottomRight().getRow(); i++) {
                for (int j = currentBlock.getUpperLeft().getCol(); j < currentBlock.getBottomRight().getCol(); j++) {

                }
            }
        }
        frame.setVisible(true);

    }
    public static void main(String args[])throws Exception{
        File init = new File(args[0]);
        File goal = new File(args[1]);
        Solver s = new Solver(init , goal);
        System.out.println("INITIAL BOARD");
        s.initialBoard.printBoard();
        System.out.println("========================");
        System.out.println("FINAL BOARD");
        s.finalBoard.printBoard();
        System.out.println("========================");
            Board b =  s.Solve();

    }
}