/*
* Text-based Minesweeper game.
*/
package GamePackage;

// Imports
import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Nov 5 - Nov 13 2020
 * @author Ben Sandoval
 */
public class Minesweeper {
    // Instantiations
    final static Scanner keyboard = new Scanner(System.in);
    final static Random rand = new Random();
            
    // Game settings
    final static int NUM_MINES = 10;
    final static int NUM_FLAGS = NUM_MINES;
    final static int GRID_SIZE = 9;
    
    // Tile types
    final static String[] IMAGES = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "M", "F", "X", "·", ""};
    static int[][] gridImg = new int[GRID_SIZE][GRID_SIZE];
    final static int MINE_VAL = 9;
    final static int FLAG_VAL = 10;
    //final static int X_VAL = 11; removed feature
    final static int HIDDEN_VAL = 12;
    final static int SHOWN_VAL = 13;
    
    // Grid/tile logic
    final static int UPPER_BOUNDS = (GRID_SIZE * GRID_SIZE) - 1;
    static int[][] gridVal = new int[GRID_SIZE][GRID_SIZE];
    final static char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    final static int CHAR_CONVERSION_FACTOR = 65;
    final static int INT_CONVERSION_FACTOR = 48;
    static int userX;
    static int userY;
    
    // User input logic
    static String userInput;
    static boolean isInputValid;
    
    // Round logic
    static boolean isPlaying = true;
    static boolean hasWon;
    static boolean hasLost;
    static boolean flagPlacingMode;
    
    // Stats
    static int flagsRemaining;
        
    ////////////////////////////////Methods//////////////////////////////////////

    /**
     * Displays "Minesweeper" on start up
     */
    public static void displayTitle(){
        System.out.printf("\n%20s\n\n", "Minesweeper");
    }
    
    /**
     * Displays the rules and additional info on start up
     */
    public static void displayRules(){
        System.out.print
                        ("Presented to you is a field with mines hidden beneath the surface.\n"
                        +"Each dot represents one tile of land. Your task is to clean the\n"
                        +"field while avoiding the mines. The number on a tile indicates\n"
                        +"how many mines are adjacent to it.\n\n");
        
        System.out.print
                        ("- input NUMBERLETTER (e.g. 5E) to uncover a tile\n"
                        + "- input ! to enable/disable flag-placing mode.\n\n");
        
        System.out.print("There are " + NUM_MINES + " mines you must avoid. Good luck!\n\n");
    }
    
    /**
     * Resets game logic and stats for a fresh start on start up / replay
     */
    public static void resetLogicAndStats(){
        // Logic
        hasWon = false;
        hasLost = false;
        flagPlacingMode = false;
        
        // Stats
        flagsRemaining = NUM_FLAGS;
    }
    
    /**
     * Sets the grid values to null and hides their values on startup / replay
     */
    public static void resetGrid(){
        for (int x = 0; x < GRID_SIZE; x ++){
            for (int y = 0; y < GRID_SIZE; y ++){
                gridVal[x][y] = 0;
                gridImg[x][y] = HIDDEN_VAL;
            }
        }
        
    }

    /**
     * Randomly selects NUM_MINES unique tiles in the grid to contain a mine
     */
    public static void assignMinePositions(){
        // We use an ArrayList to take advantage of the Shuffle method from the Collections class
        // Shuffle() is essential in order to assign UNIQUE positions
        ArrayList<Integer> list = new ArrayList<Integer>();
        // Insert each tile to an index in ArrayList list
        for (int i = 0; i <= UPPER_BOUNDS ; i ++){
            list.add(i);
        }
        // Shuffling the list is the randomization aspect of the mine position assignment
        Collections.shuffle(list);
        // Looping through the first NUM_MINES indexed values in list ensures we only
        // have NUM_MINES mines
        for (int i = 0; i < NUM_MINES; i ++){
            // This is a unique random integer since list was shuffled
            int randomInt = list.get(i);
            // Received as array since 1 integer is being turned into 2
            int[] indexHolder = convertIntToIndex(randomInt);
            gridVal[indexHolder[0]][indexHolder[1]] = MINE_VAL;
            updateAdjTilesValue(indexHolder[0], indexHolder[1]);
        }
    }
    
    /**
     * Converts the index value of an integer when in a 1D array to a 2D array 
     * with boundaries defined as GRID_SIZE for both the row length and column length
     * 
     * @param intValue the index value of an integer in a 1D array
     * @return the integer's 2D array index
     */
    public static int[] convertIntToIndex(int intValue){
        int[] indexHolder = new int[2];
        indexHolder[1] = intValue % GRID_SIZE;
        indexHolder[0] = (intValue - indexHolder[1]) / GRID_SIZE;
        
        return indexHolder;
    }
    
    /**
     * In minesweeper, the number on a tile indicates how many adjacent mines
     * there are to it. This method calculates that value
     * 
     * @param mineX x-coordinate of the mine
     * @param mineY y-coordinate of the mine
     */
    public static void updateAdjTilesValue(int mineX, int mineY){
        // Scan through the grid
        for (int x = 0; x < GRID_SIZE; x ++){
            for (int y = 0; y < GRID_SIZE; y ++){
                // Two conditions must be met to increment tile value:
                // 1: It is adjacent to the mine
                // 2: It is not a mine
                if (isAdjTo(x, y, mineX, mineY) && !isMine(x, y)){
                    gridVal[x][y] ++;
                }
            }
        }
    }
    
    /**
     * Determines if two given tiles are adjacent to one another
     * 
     * @param x The original tile's x-coordinate
     * @param y The original tile's y-coordinate
     * @param checkX The tile you want to compare to, x-coordinate
     * @param checkY The tile you want to compare to, y-coordinate
     * @return whether they're adjacent or not
     */
    public static boolean isAdjTo(int x, int y, int checkX, int checkY){
        boolean adj = false;
        if (
            (checkY - 1 == y && checkX - 1 == x)  ||// 1. Above, left
            (checkY - 1 == y && checkX  == x)     ||// 2. Above
            (checkY - 1 == y && checkX + 1 == x)  ||// 3. Above, right
            (checkY == y && checkX - 1 == x)      ||// 4. Left
            (checkY == y && checkX + 1 == x)      ||// 5. Right
            (checkY + 1 == y && checkX - 1 == x)  ||// 6. Bottom left
            (checkY + 1 == y && checkX == x)      ||// 7. Bottom
            (checkY + 1 == y && checkX + 1 == x)    // 8. Bottom right
            ){
            adj = true;
        }
            
        return adj;
    }
    
    /**
     * Checks if the tile with given coordinates is a mine
     * 
     * @param x x-coordinate of tile
     * @param y y-coordinate of tile
     * @return whether the tile is a mine or not
     */
    public static boolean isMine(int x, int y){
        boolean isMine = false;
        if (gridVal[x][y] == MINE_VAL){
            isMine = true;
        }
        return isMine;
    }
    
    /**
     * Displays game stats.
     * Currently only shows the number of flags left to use, but if time permitted
     * I would have liked to also include wins & losses and difficulty (easy, med, hard)
     */
    public static void displayStats(){
        //System.out.print("----------------------------\n");
        System.out.printf("Flags: %d\n", flagsRemaining);
        System.out.print("----------------------------\n\n");
    }
    
    /**
     * Displays the row headers, column headers, and grid.
     */
    public static void drawGrid(){
        // Column header
        System.out.printf("%4s", ""); // Indent
        for (int col = 0; col < GRID_SIZE; col ++){
            // Assigns each index in a column to their corresponding letter in the alphabet
            System.out.print(alphabet[col]);
            System.out.print(" ");
        }
        System.out.println("\n");
        
        // Left-side row header
        for (int x = 0; x < GRID_SIZE; x ++){
            System.out.printf("%-4d", x);
            
            // Grid
            for (int y = 0; y < GRID_SIZE; y ++){
                //Perform an initial check to see if the tile has been revealed or not
                if (gridImg[x][y] == HIDDEN_VAL){
                    System.out.print(IMAGES[HIDDEN_VAL]);
                }
                // Or if there's a flag
                else if (gridImg[x][y] == FLAG_VAL){
                    System.out.print(IMAGES[FLAG_VAL]);
                }
                // If it's not hidden or doesn't have a flag then display its normal value
                // Mine
                else if (gridVal[x][y] == MINE_VAL){
                    System.out.print(IMAGES[MINE_VAL]);
                }
                // Regular tile
                else{
                    System.out.print(IMAGES[gridVal[x][y]]);
                }
                // Gaps in between
                System.out.print(" ");
            }
            
            // Right-side row header
            System.out.printf("%3d\n", x);
        }
        
        // Column footer
        System.out.printf("\n%4s", "");
        for (int col = 0; col < GRID_SIZE; col ++){
            System.out.print(alphabet[col]);
            System.out.print(" ");
        }
        System.out.println("\n");
    }
    
    /**
     * Sets all tiles to be visible (show their actual value) if the player loses.
     */
    public static void revealGrid(){
        for (int x = 0; x < GRID_SIZE; x ++){
            for (int y = 0; y < GRID_SIZE; y ++){
                gridImg[x][y] = SHOWN_VAL;
            }
        }
        drawGrid();
    }
    
    /**
     * Checks at the end of each turn if the player has won.
     * Condition being that all non-mine values have been manually revealed
     * 
     * @return whether the player has won or not
     */
    public static boolean checkHasWon(){
        // Cycle through the grid and look for all non-mine tiles
        for (int x = 0; x < GRID_SIZE; x ++){
            for (int y = 0; y < GRID_SIZE; y ++){
                if (gridVal[x][y] != MINE_VAL){
                    // If the loop finds that the tile is not revealed, 
                    // break the loop early & return false
                    if (gridImg[x][y] == HIDDEN_VAL){
                        return false;
                    }
                }
            }
        }
        // If the loop finds none of the defined scenario, return true
        return true;
    }
    
    /**
     * Checks at the end of each turn if the player has lost.
     * Condition being they've selected a mine
     * 
     * @return whether the player has lost or not
     */
    public static boolean checkHasLost(){
        boolean b = false;
        if (gridVal[userX][userY] == MINE_VAL){
            b = true;
        }
        return b;
    }
    
    /**
     * This is a recursive algorithm that continuously checks the descending adjacent tiles
     * of a given tile and reveals it until it reaches a tile that doesn't meet 
     * a criteria (0 and hidden). When this instance occurs, it returns to the 
     * next adjacent tile of the tile it was initially scanning and repeats this 
     * process until all "branches" of the initial tile have been scanned.
     * 
     * @param branchX x - coordinate of a branching tile
     * @param branchY y - coordinate of a branching tile
     */
    public static void cycleAdjAndCheckIf0(int branchX, int branchY){
        for (int x = branchX - 1; x <= branchX + 1; x++){
            for (int y = branchY - 1; y <= branchY + 1; y ++){
                // Check if x & y are in range. Otherwise, would go out of bounds & error.
                if (x >= 0 && x < GRID_SIZE && y >= 0 && y < GRID_SIZE){
                    // Ensure a flag isn't overridden
                    if (gridImg[x][y] != FLAG_VAL){
                        // 1.
                        if (gridVal[x][y] == 0 && gridImg[x][y] == HIDDEN_VAL){
                            gridImg[x][y] = SHOWN_VAL;
                            cycleAdjAndCheckIf0(x, y);
                        }
                        gridImg[x][y] = SHOWN_VAL;
                    }
                }
            }
        }
    }
    
    /**
     * Ensures progression is not made in main() unless the user's input is valid.
     * Initializes the input in different ways depending on which state the game is in.
     * Called whenever user input is required.
     */
    public static void initializeInput(){
        // Boolean + loop ensures we don't return back to main() unless input is valid
        isInputValid = false;
        while (!isInputValid){
        userInput = keyboard.next().toUpperCase();
            
            // There are two instances that input is required:
            // 1. During a round to indicate a tile reveal or enable/disable flag-placing mode
            if (!hasWon && !hasLost){
                // Three instances can occur during a round:
                // 1. User inputs a coordinate. Used for both tile reveal & flag placement
                
                // String matches() + regex used to ensure validity
                // Only returns true if input is 2 digits in length 
                // consisting of a digit then letter
                if (userInput.matches("^\\d[A-Z]$")){
                    // Converting char to int
                    userX = userInput.charAt(0) - INT_CONVERSION_FACTOR;
                    userY = userInput.toUpperCase().charAt(1) - CHAR_CONVERSION_FACTOR;
                    
                    // Next, ensure the digit/letter is in bounds
                    if (userX >= GRID_SIZE || userY >= GRID_SIZE){
                        System.out.println("Please enter a valid input!");
                    }
                    else{
                        if (gridImg[userX][userY] == SHOWN_VAL){
                            System.out.println("This tile is already revealed!");
                        }
                        else if ((gridImg[userX][userY] == FLAG_VAL) && !flagPlacingMode){
                            System.out.println("Cannot reveal a tile that contains a flag. Enable flag-placing mode and select the tile to remove the flag.");
                        }
                        else{
                            isInputValid = true;
                        }
                    }
                }
                
                // 2. User enables/disables flag-placing mode
                else if(userInput.equals("!")){
                    isInputValid = true;
                    if (flagPlacingMode){
                        flagPlacingMode = false;
                        System.out.println("Flag-placing mode has been disabled.");
                    }
                    else{
                        flagPlacingMode = true;
                        System.out.println("Flag-placing mode has been enabled.");
                    } 
                }
                // 3. Input is invalid
                else{
                    System.out.println("Please enter a valid input!");
                }
            }
            // 2. Game has ended (from a win/lose), prompted to play again
            else{
                if (userInput.equals("Y")){
                    isInputValid = true;
                }
                // Break out of main while loop in main()
                else if (userInput.equals("N")){
                    isInputValid = true;
                    isPlaying = false;
                }
                else{
                    System.out.println("Please enter a valid input!");
                }
            }
            System.out.println("");
        }
    }

    /**
     * Maintains game logic.
     * 
     * @param args
     */
    public static void main(String[] args) {
        
        displayTitle();
        displayRules();
        
        while(isPlaying){
            resetLogicAndStats();
            resetGrid();
            assignMinePositions();
            
            while (!hasWon && !hasLost){
                displayStats();
                drawGrid();    
                initializeInput();
                
                // Do not want the subsequent else if / else to run when ! is inputted.
                if (userInput.equals("!")){
                    // Don't want anything to happen in the turn
                }
                // Tile reveal mode
                else if (!flagPlacingMode){
                    
                    // Check if the player has won or lost. Either conditions
                    // break out of the nested while loop
                    
                    // Lost -- checks if player landed on a mine
                    if (checkHasLost()){
                        hasLost = true;
                    }
                    
                    // Normal instance
                    else{
                        // If the player lands on a 0, all adjacent tiles also with
                        // value 0 must appear
                        if (gridVal[userX][userY] == 0){
                            cycleAdjAndCheckIf0(userX, userY);
                        }
                        // If the player lands on a non - 0 integer tile, simply reveal it
                        else{
                           gridImg[userX][userY] = SHOWN_VAL;
                        }
                        
                        // Won -- checks if all non-mine values have been revealed
                        if (checkHasWon()){
                            hasWon = true;
                        }
                    }   
                }
                // Flag placing mode
                else{
                    // Remove a flag (user chose to place one on existing flag)
                    if (gridImg[userX][userY] == FLAG_VAL){
                        gridImg[userX][userY] = HIDDEN_VAL;
                        flagsRemaining ++;
                    }
                    // Insert flag
                    else{
                        gridImg[userX][userY] = FLAG_VAL;
                        flagsRemaining --;
                    }
                }
            }// End of nested while loop
            //At this point, the game has ended
            revealGrid();
            
            // Congratulate the user for winning
            if(hasWon){
                System.out.println("Congrats! You successfully avoided all the mines!");
            }
            // Tell the user they lost
            else{
                System.out.println("Sorry, you landed on a mine");
            }
            
            System.out.println("Would you like to play again? (Y/N)");
            initializeInput();
            
        }// End of main while loop. Only breaks out if userInput is "N"
        System.out.println("Thanks for playing! Goodbye.");
        
    }// End of main method
    
}// End of class