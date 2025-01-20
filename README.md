# Data Algorithms Assignment 6: Battleship Strategy

## Description
This assignment involves creating a Battleship bot that uses efficient data structures and algorithms to play a modified version of the Battleship game. The bot must optimize its strategy to sink all the opponent's ships with the fewest possible shots while adhering to the game's rules. The game is played on a 15x15 grid with 6 ships of varying sizes, and the bot competes against random ship placements.

The project includes:
1. Logic to track fired shots and avoid duplicates.
2. Algorithms to optimize shot selection and improve efficiency.
3. Simulation of 10,000 games to evaluate performance.

## Features
- Implements the `BattleShipBot` interface with the following methods:
  - **initialize(BattleShip2 battleship)**: Sets up the bot's state at the start of the game.
  - **getAuthors()**: Returns the author(s) of the solution.
  - **fireShot()**: Executes a single shot during the game.
- Supports advanced strategies:
  - Probabilistic targeting for higher efficiency.
  - Dynamic adaptation based on hit/miss outcomes.
  - Mapping to avoid duplicate shots and track ship locations.
- Measures bot performance:
  - Average shots per game over 10,000 simulations.
  - Execution time for 10,000 games.

## File Structure
- `src/`:
  - `Assignment6.java`: The entry point for the program. Handles the game simulation.
  - `BattleShip2.java`: API provided for interacting with the Battleship game.
  - `BattleShipBot.java`: Interface to be implemented by the custom bot.
  - `ExampleBot.java`: Starter template for developing the custom bot.
  - `YourBotName.java`: Your custom implementation of the bot.
- `README.md`: Documentation for the project.

## How to Run
1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/data-algorithms-assignment6.git
2.Open the project in IntelliJ IDEA:
Launch IntelliJ IDEA.
Click File > Open and select the project folder.
3.Replace ExampleBot with your bot class name (e.g., MyBot) in Assignment6.java.
Run the project:
4.Build and execute Assignment6.java to simulate 10,000 games.
