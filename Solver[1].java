import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.awt.Color;
import java.util.List;
import java.util.Random;

public class Solver {

    private int rows, columns; //height and width of the board
    private HashSet<Board> visitedBoards; // keeps track of all previous board configurations
    private Board initialBoard; // initial state of the board
    private Board finalBoard; // final state of the board as in we need to have this at the end
    private HashSet<coordinates> emptyspaces; //to see where the empty spaces are
    private ArrayList<Board> solution; //when we find a solution, we instantiate this to use it for navigation purposes
    private ArrayList<coordinates> directions; // prints block number and direction for path to display them
    private ArrayList<Color> colors; //only used to color the blocks, after solution is found
    private JFrame frame; // this is the frame that we will use to display everything
    private JPanel sidebar; //this will be the sidebar that we will populate with informative text and even buttons
    private JPanel leftpanel;
    private JLabel label;
    private JPanel Panel;
    private JPanel oldPanel;
    private ArrayList<coordinates> solPair; // the 1st will represent the board number and
    // the second the direction as defined in the coordinates class
    private Integer solIndex; // this is the main index that will be used to navigate thru the solution
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

            if (temp != null && !visitedBoards.contains(temp)) {
                visitedBoards.add(temp);
                temp =  DepthFirstSearch(temp , recursiveLevel + 1);
                if(temp != null && temp.isSolved(this.finalBoard)) return temp;
            }
            temp = currentBoard.moveOneBlock(block, block.oneRight());

            if (temp != null && !visitedBoards.contains(temp)) {
                visitedBoards.add(temp);
                temp =  DepthFirstSearch(temp , recursiveLevel + 1);
                if(temp != null && temp.isSolved(this.finalBoard)) return temp;
                }
            temp = currentBoard.moveOneBlock(block, block.oneDown());

            if (temp != null && !visitedBoards.contains(temp)) {
                visitedBoards.add(temp);
                temp =  DepthFirstSearch(temp , recursiveLevel + 1);
                if(temp != null && temp.isSolved(this.finalBoard)) return temp;
                    }
            temp = currentBoard.moveOneBlock(block, block.oneLeft());

            if (temp != null && !visitedBoards.contains(temp)) {
                visitedBoards.add(temp);
                if(temp!=null) {
                    temp = DepthFirstSearch(temp, recursiveLevel + 1);
                }if(temp != null && temp.isSolved(this.finalBoard)) return temp;
                    }
            }
        return null;
    }

    public Boolean Solve(char flag) throws Exception {
        long start = System.nanoTime();

        Board b = null;
        if (flag == 'd')
            b = DepthFirstSearch(initialBoard,0);
        else if (flag == 'a' || flag == 'b')
             b = BreadthFirstSearch(initialBoard, flag);
        else{
            System.out.println("Invalid selection, please select between a, b and d");
            return false;
        }

        long finish = System.nanoTime();
        long timeElapsed = finish - start;
        if (b == null){
            System.out.println("SOLUTION NOT FOUND");
            return false;
        }
        else System.out.println("SOLUTION FOUND");
        printPath(b , timeElapsed / 1000000);
        buildSolution(b);
        return true;
    }

    public void printPath(Board b , long time){
        boolean debug = false;
        int count = 0 ;
        System.out.println("PRINTING PATH OF SOLVED BOARD");
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
    public Board BreadthFirstSearch(Board init, char flag) throws Exception{
       if(init.isSolved(this.finalBoard))
           return init;

        Queue<Board> solveMe = null;

        if( flag == 'b')
           solveMe = new LinkedList<>();
       if(flag == 'a') {
           solveMe = new PriorityQueue<>();
       }
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

    public void buildSolution(Board finalBoard) throws Exception{
        //setup the environment to display using swing
        //final board coming in is already solved
    if(!finalBoard.isSolved(this.finalBoard)) throw new Exception("board is not solved");

        oldPanel = null;

        frame = new JFrame(gc);
        frame.setTitle("Sliding Blocks Solver");
        frame.setMinimumSize(new Dimension(700, 700));
        frame.setPreferredSize(new Dimension(1500, 1500));
        frame.setLocation(200, 200);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addKeyListener(new MyListener());
        frame.setResizable(false);

        GridBagConstraints c = new GridBagConstraints();

        Dimension pSize = new Dimension(250, 1500);
        Dimension mSize = new Dimension(150, 600);

        label = new JLabel();
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setPreferredSize(new Dimension(130, 80));
        label.setMinimumSize(new Dimension(140, 80));


        sidebar = new JPanel();
        sidebar.setPreferredSize(pSize);
        sidebar.setMinimumSize(mSize);
        sidebar.setBackground(new Color(182, 104, 161));

        sidebar.add(label);

        leftpanel = new JPanel();
        leftpanel.setBackground(new Color(12, 14, 61));
        leftpanel.setPreferredSize(pSize);
        leftpanel.setMinimumSize(mSize);

        Panel = new JPanel();
        Panel.setLayout(new GridBagLayout());
        Panel.setBackground(new Color(12, 14, 61));
        Panel.addKeyListener(new MyListener());

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.8;
        c.gridx = 0;
        c.gridy = 0;
        Panel.add(leftpanel, c);

        c.weightx = 0.2;
        c.gridx = 1;
        c.gridy = 0;
        Panel.add(sidebar, c);


        frame.add(Panel);


        this.solution = new ArrayList<>();
        solution.add(finalBoard);

        colors = new ArrayList<>();

        directions = new ArrayList<>();

        solIndex = 0;

        Random rand = new Random();
        rand.setSeed(346544);
        for (int i = 1; i < finalBoard.getBlocks().size() + 1 ; i++){
            int r = rand.nextInt(256), g = rand.nextInt(256), b = rand.nextInt(256);

            if (r == g && r == b && r > 120 ){
                r = (int)(0.8*r);
                g = (int)(0.8*g);
                b = (int)(0.8*b);
            }
            colors.add(new Color(r,g,b));
        }


        while (finalBoard.getParent() != null){
            solution.add(finalBoard.getParent());
            finalBoard = finalBoard.getParent();
        }

        Collections.reverse(solution);

        for( int i = 1 ; i < solution.size() ; i++){
            directions.add(difference(solution.get(i - 1), solution.get(i)));
        }

        displayFinalBoard();
        updateSideBar(label);
        displaySolutionHelper(finalBoard);


        //now we have the ArrayList of boards , with the final solution as the first element and the initial as the last
        // displaySolutionHelper(solution , frame);
    }

    /**
     * iterate through each block and see which ones are different between the two boards, then return the number of
     * the block and the direction that it moved in in the form of coordinates(blocknumber,direction)
      * @param src this is where we start from
     * @param dest this is where we go
     * @return a pair where the first number is the block number that is being moved, the second is the direction
     * according to the directions in coordinates
     */
    public coordinates difference(Board src, Board dest) throws Exception {
        Iterator srcIter = src.getBlocks().iterator();
        Iterator destIter = dest.getBlocks().iterator();

        List<Block> srcSorted = new ArrayList<>();
        List<Block> destSorted = new ArrayList<>();

        while (srcIter.hasNext()) {
            Block currentSrc = (Block) srcIter.next();
            Block currentDest = (Block) destIter.next();
            srcSorted.add(currentSrc);
            destSorted.add(currentDest);
        }

        srcSorted.sort(Comparator.comparing(Block::getMyNumber));
        destSorted.sort(Comparator.comparing(Block::getMyNumber));

        for (int i = 0 ; i < srcSorted.size(); i++ ){
            Block srcBlock = srcSorted.get(i);
            Block destBlock = destSorted.get(i);
            if (!srcBlock.equals(destBlock)){
                //find out which direction u need to go in
                if(srcBlock.oneUp().equals(destBlock.getUpperLeft())){
                    return new coordinates(srcBlock.getMyNumber(), 0);
                }
                if(srcBlock.oneDown().equals(destBlock.getUpperLeft())){
                    return new coordinates(srcBlock.getMyNumber(), 1);
                }
                if(srcBlock.oneLeft().equals(destBlock.getUpperLeft())){
                    return new coordinates(srcBlock.getMyNumber(), 2);
                }
                if(srcBlock.oneRight().equals(destBlock.getUpperLeft())){
                    return new coordinates(srcBlock.getMyNumber(), 3);
                }
            }
        }
        return null;
    }

    //this is gonna be similar to the one below, probably because of bad design
    public void displayFinalBoard() throws Exception{
        Board startHere = solution.get(solution.size()-1).deepClone();
        int row = startHere.getSize().getRow();
        int col = startHere.getSize().getCol();

        ArrayList<JPanel> list = new ArrayList<>();

        JPanel Panel = new JPanel();
        Panel.setLayout(new GridLayout(row, col));
        Panel.setMinimumSize(new Dimension(100,100));
        Panel.setPreferredSize(new Dimension(100,100));

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                JPanel panel = new JPanel();
                panel.setLayout(new GridLayout(1, 1, 1, 1));
                JButton component = new JButton();
                component.setEnabled(false);
                panel.add(component, 0, 0);
                list.add(panel);
                Panel.add(panel);
            }
        }

        Iterator iterator = startHere.getBlocks().iterator();

        while (iterator.hasNext()) {
            Block currentBlock = (Block) iterator.next();
            int currentNumber = currentBlock.getMyNumber();
            for (int i = currentBlock.getUpperLeft().getRow(); i < currentBlock.getBottomRight().getRow(); i++) {
                for (int j = currentBlock.getUpperLeft().getCol(); j < currentBlock.getBottomRight().getCol(); j++) {
                    JPanel tempPanel = list.get(col * i + j);
                    JButton tempButton = (JButton) tempPanel.getComponent(0);
                    tempButton.setBackground(colors.get(currentNumber -1));
                }
            }
        }

        sidebar.add(Panel);
        frame.setVisible(true);

    }

    public void displaySolutionHelper(Board currentBoard) {
        Board startHere = currentBoard;
        int row = startHere.getSize().getRow();
        int col = startHere.getSize().getCol();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(row, col));
        mainPanel.setMinimumSize(new Dimension(200,200));
        mainPanel.setPreferredSize(new Dimension(300,300));

        if (oldPanel == null) {
            updateLeftPanel(mainPanel);
        }
        ArrayList<JPanel> list = new ArrayList<>();


        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                JPanel panel = new JPanel();
                panel.setLayout(new GridLayout(1, 1, 1, 1));
                JButton component = new JButton();
                component.setEnabled(false);
                panel.add(component, 0, 0);
                list.add(panel);
                mainPanel.add(panel);
            }
        }

        //starting from starthere which is the board at the end of the linked list
        // this is the iterator of its blocks
        Iterator iterator = startHere.getBlocks().iterator();

        while (iterator.hasNext()) {
            Block currentBlock = (Block) iterator.next();
            int currentNumber = currentBlock.getMyNumber();

            for (int i = currentBlock.getUpperLeft().getRow(); i < currentBlock.getBottomRight().getRow(); i++) {
                for (int j = currentBlock.getUpperLeft().getCol(); j < currentBlock.getBottomRight().getCol(); j++) {
                    JPanel tempPanel = list.get(col * i + j);
                    JButton tempButton = (JButton) tempPanel.getComponent(0);
//                    tempButton.setText("" + colors.get(currentNumber - 1) );
                    tempButton.setBackground(colors.get(currentNumber -1));
                }
            }
        }

        if (oldPanel != null) {
            leftpanel.removeAll();
            updateSideBar(label);
            updateLeftPanel(mainPanel);

        }
        oldPanel = mainPanel;
        frame.setVisible(true);

    }

    public void updateSideBar(JLabel text){
        String output = "<html>";
        if(solIndex < solution.size() - 1) {
            int number = directions.get(solIndex).getRow();
            Color c = colors.get(number - 1);
            output += "Right Arrow: Move " + "<font color=rgb(" + c.getRed() + ","+  c.getGreen() + ","+   c.getBlue()+ ") >"+ number  + " </font>";
            if(directions.get(solIndex).getCol() == 0)
                output += " up";
            if(directions.get(solIndex).getCol() == 1)
                output += " down";
            if(directions.get(solIndex).getCol() == 2)
                output += " left";
            if(directions.get(solIndex).getCol() == 3)
                output += " right";
        }
        output += "<br/> ";
        if (solIndex > 0){
            int number = directions.get(solIndex - 1).getRow();
            Color c = colors.get(number - 1);
            output += "Left Arrow: Move " + "<font color=rgb(" + c.getRed() + ","+  c.getGreen() + ","+   c.getBlue()+ ") >"+ number  + " </font>";

//            output += " Left Arrow: Move " + directions.get(solIndex - 1).getRow();
            if(directions.get(solIndex - 1).getCol() == 0)
                output += " down";
            if(directions.get(solIndex - 1).getCol() == 1)
                output += " up";
            if(directions.get(solIndex - 1).getCol() == 2)
                output += " right";
            if(directions.get(solIndex - 1).getCol() == 3)
                output += " left";
        }
        output += "</html>";
        text.setText(output);

    }
    public void updateLeftPanel(JPanel mainPanel){
        JPanel top = new JPanel();
        JLabel text = new JLabel();
        if (solIndex == 0)
            text.setText("Initial Board Configuration - Board " + 1 + " of " + solution.size());
        else if (solIndex == solution.size() - 1)
            text.setText("Final Board Configuration - Board " + solution.size() + " of " + solution.size());
        else
            text.setText(" Board Configuration: " + (solIndex + 1) + " of " + solution.size());


        top.setBackground(new Color(120, 70, 61));
        top.setPreferredSize( new Dimension(300,30));
        top.add(text);

        JPanel gap = new JPanel();
        gap.setBackground(new Color(12, 14, 61));
        gap.setPreferredSize( new Dimension(300,40));

        leftpanel.add(top);
        leftpanel.add(gap);
        leftpanel.add(mainPanel);

    }
    //this will be fed with an index, change the solutions index to that and refresh the frame
    public void updateSolution(){
        updateSideBar(label);
        displaySolutionHelper(solution.get(solIndex));
    }
    class MyListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {

            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                System.out.println("Right key typed");
            }
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                System.out.println("Left key typed");
            }

        }
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                //increment not by one but by 25 percent
                if (solIndex < (int)(solution.size()*9.0/10.0) ){
                    solIndex += (int)(solution.size()/10.0);
                    updateSolution();
                }
                else
                    System.out.println("Cannot Increment by " + (solution.size()/4.0));
            }
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                //increment not by one but by 25 percent
                if (solIndex > (int)(solution.size()/10.0) ){
                    solIndex -= (int)(solution.size()/10.0);
                    updateSolution();
                }
                else
                    System.out.println("Cannot Decrement by " + (solution.size()/4.0));
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                if (solIndex < solution.size() - 1){
                    solIndex++;
                    updateSolution();
                }
                else
                    System.out.println("end point of the steps reached");
            }
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                if (0 < solIndex ){
                    solIndex--;
                    updateSolution();
                }
                else
                    System.out.println("start point of the steps reached");
            }

        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
//                System.out.println("Right key Released");
            }
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
//                System.out.println("Left key Released");
            }
        }
    }
    public static void main(String args[])throws java.lang.Exception{
        File init = new File(args[0]);
        File goal = new File(args[1]);
        Solver s = new Solver(init , goal);
        System.out.println("INITIAL BOARD");
        s.initialBoard.printBoard();
        System.out.println("========================");
        System.out.println("FINAL BOARD");
        s.finalBoard.printBoard();
        System.out.println("========================");
        Boolean solved =  s.Solve('a');
//        s.displaySolutionHelper(solution)
        System.out.println(s.difference(s.solution.get(0) , s.solution.get(1)));

        if (solved == true)
            s.displaySolutionHelper(s.solution.get(s.solIndex));

    }
}
