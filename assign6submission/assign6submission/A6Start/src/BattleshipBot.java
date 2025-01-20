import battleship.*;
import java.awt.Point;
import java.util.*;

/**
 * We Ngoc Bui, 000899963 and Hamza Saleh, 000887384  declare that all the following code is our own work and
 * we did not share our code with anyone else
 */
public class BattleshipBot implements BattleShipBot {
    private BattleShip2 battleShip;
    private Random random;
    private Set<Point> shotsFired;
    private boolean isTargetingMode;
    private CellState[][] board; // Keep track of the state of each cell on the board
    private int[][] probabilityMap; // Keep track of the probability of each cell containing a ship
    private int lastSunkShipsCount; // Keep track of the number of ships sunk in the previous turn

    private Point lastDirection;
    private Point oppositeDirection;

    private Set<Point> targetQueue;
    private Point reusablePoint;



    /**
     * When the game starts, initialize the bot by setting up the board and probability map
     */
    @Override
    public void initialize(BattleShip2 b) {
        battleShip = b;
        random = new Random(0xAAAAAAAA);
        shotsFired = new HashSet<>();
        isTargetingMode = false; // Start in hunting/targeting mode
        reusablePoint = new Point();

        targetQueue = new HashSet<>();
        board = new CellState[b.BOARD_SIZE][b.BOARD_SIZE];  // Initialize the board
        probabilityMap = new int[b.BOARD_SIZE][b.BOARD_SIZE]; // Initialize the probability map
        for (int i = 0; i < b.BOARD_SIZE; i++) {
            for (int j = 0; j < b.BOARD_SIZE; j++) {
                board[i][j] = CellState.Empty;
                probabilityMap[i][j] = 0;
            }
        }
        lastSunkShipsCount = 0;
        updateProbabilityMap();
    }


    /**
     * Fire a shot at the next target
     */
    @Override
    public void fireShot() {
        updateProbabilityMap(); // Update the probability map before each shot
        Point shot = selectNextShot();
        if (shot == null) {
            return;
        }

        reusablePoint.setLocation(shot.x, shot.y);  // Reuse the same Point object to avoid creating new objects
        boolean hit = battleShip.shoot(reusablePoint);   // Fire a shot
        shotsFired.add(new Point(reusablePoint.x, reusablePoint.y));  // Keep track of the shots fired
        board[reusablePoint.x][reusablePoint.y] = hit ? CellState.Hit : CellState.Miss;

        if (hit) {  // If the shot hit a ship
            updateTargetQueue(shot);
            isTargetingMode = true;  // Switch to targeting mode
            if (battleShip.numberOfShipsSunk() > lastSunkShipsCount) {  // If a ship was sunk
                lastSunkShipsCount = battleShip.numberOfShipsSunk();  // Update the number of sunk ships
                isTargetingMode = false; // Switch back to shooting mode
                targetQueue.clear();
            }
        } else if (isTargetingMode && targetQueue.isEmpty()) {
            isTargetingMode = false;
        }
    }

    /**
     * Select the next shot based on the current mode
     * @return the next shot
     */
    private Point selectNextShot() {
        if (isTargetingMode && !targetQueue.isEmpty()) { // If in targeting mode and there are targets in the queue
            Iterator<Point> iterator = targetQueue.iterator();
            Point nextTarget = iterator.next();
            iterator.remove();
            return nextTarget;
        } else {

            return findNextHuntingShot(); // return the best shot based on the probability map
        }
    }

    /**
     * Find the next best shot based on the probability map
     * @return the next best shot
     */
    private Point findNextHuntingShot() {
        Point bestShot = null;
        int maxProbability = 0;
        for (int x = 0; x < battleShip.BOARD_SIZE; x++) {
            for (int y = 0; y < battleShip.BOARD_SIZE; y++) {
                reusablePoint.setLocation(x, y);
                if (probabilityMap[x][y] > maxProbability && !shotsFired.contains(reusablePoint)) {  // If the cell has not been shot at
                    maxProbability = probabilityMap[x][y];
                    bestShot = new Point(reusablePoint.x, reusablePoint.y); // Update the best shot
                }
            }
        }
        return bestShot;
    }


    /**
     * Update the probability map based on the remaining ship sizes
     */
    private void updateProbabilityMap() {
        for (int i = 0; i < battleShip.BOARD_SIZE; i++) {
            for (int j = 0; j < battleShip.BOARD_SIZE; j++) {
                if (board[i][j] == CellState.Empty) {  // If the cell is empty
                    probabilityMap[i][j] = 0; // Reset the probability to 0
                }
            }
        }

        // Calculate probabilities based on remaining ship sizes
        for (int shipSize : battleShip.getShipSizes()) {
            for (int x = 0; x < battleShip.BOARD_SIZE; x++) {
                for (int y = 0; y < battleShip.BOARD_SIZE; y++) {
                    // Check horizontally and vertically
                    if (board[x][y] == CellState.Empty) {
                        if (canPlaceShip(x, y, shipSize, true)) {  // Check if the ship can be placed horizontally
                            increaseProbability(x, y, shipSize, true);
                        }
                        if (canPlaceShip(x, y, shipSize, false)) { // Check if the ship can be placed vertically
                            increaseProbability(x, y, shipSize, false);
                        }
                    }
                }
            }
        }
    }

    /**
     * Check if a ship of a given size can be placed at a given position
     * @param x the x-coordinate of the position
     * @param y the y-coordinate of the position
     * @param shipSize the size of the ship
     * @param horizontal the orientation of the ship
     * @return true if the ship can be placed, false otherwise
     */
    private boolean canPlaceShip(int x, int y, int shipSize, boolean horizontal) {
        if (horizontal) {
            if (x + shipSize > battleShip.BOARD_SIZE) return false;
            for (int i = 0; i < shipSize; i++) { // Check if the ship can be placed horizontally
                if (board[x + i][y] != CellState.Empty) return false;
            }
        } else {
            if (y + shipSize > battleShip.BOARD_SIZE) return false;
            for (int i = 0; i < shipSize; i++) { // Check if the ship can be placed vertically
                if (board[x][y + i] != CellState.Empty) return false;
            }
        }
        return true;
    }

    /**
     * Increase the probability of a cell containing a ship
     * @param x the x-coordinate of the cell
     * @param y the y-coordinate of the cell
     * @param shipSize the size of the ship
     * @param horizontal the orientation of the ship
     */
    private void increaseProbability(int x, int y, int shipSize, boolean horizontal) {
        if (horizontal) {
            for (int i = 0; i < shipSize; i++) {
                probabilityMap[x + i][y]++; // Increase the probability of the cell containing a ship
            }
        } else {
            for (int i = 0; i < shipSize; i++) {
                probabilityMap[x][y + i]++;
            }
        }
    }

    /***
     * Update the target queue based on the last hit
     * @param lastHit the last hit
     */
    private void updateTargetQueue(Point lastHit) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};  // Check the 4 directions around the last hit
        for (int[] direction : directions) {
            int newX = lastHit.x + direction[0];
            int newY = lastHit.y + direction[1];
            if (newX >= 0 && newX < battleShip.BOARD_SIZE && newY >= 0 && newY < battleShip.BOARD_SIZE) { // Check if the new position is within the board
                Point nextTarget = new Point(newX, newY);
                if (board[newX][newY] == CellState.Empty) {
                    targetQueue.add(nextTarget);
                }
            }
        }
    }

    /**
     * Authorship of the solution - must return names of all students that contributed to
     * the solution
     * @return names of the authors of the solution
     */

    @Override
    public String getAuthors() {
        return "Ngoc Bui, Hamza Saleh";
    }
}
