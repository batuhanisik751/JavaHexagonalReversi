# JavaHexagonalReversi

A Java implementation of Reversi (Othello) played on a hexagonal board, featuring a Swing-based GUI, multiple AI strategies, and full MVC architecture.

## About

Players take turns placing pieces (Black and White) on a hexagonal grid. A move is valid if it traps one or more of the opponent's pieces between the newly placed piece and an existing piece of the same color — all trapped pieces are flipped. The game ends when the board is full or neither player can move. The player with the most pieces wins.

## How to Build & Run

**Prerequisites:** Java 11+, Maven

```bash
# Build
mvn compile

# Run (examples)
mvn exec:java -Dexec.mainClass="cs3500.reversi.Reversi" -Dexec.args="4 human human"
mvn exec:java -Dexec.mainClass="cs3500.reversi.Reversi" -Dexec.args="4 human ai strategy1"
mvn exec:java -Dexec.mainClass="cs3500.reversi.Reversi" -Dexec.args="5 ai strategy1 ai strategy4"

# Run tests
mvn test
```

**Arguments:** `<boardSize> <player1> [strategy] <player2> [strategy]`

- Board size must be 3 or greater
- Player types: `human` or `ai`
- AI strategies: `strategy1` through `strategy4` (see below)

## Controls

- **Click** a hexagon to select it
- **Enter** to confirm your move
- **P** to pass your turn

## AI Strategies

| Strategy   | Name                   | Description                                              |
|------------|------------------------|----------------------------------------------------------|
| strategy1  | AsManyPiecesAsPossible | Greedy — picks the move that captures the most pieces    |
| strategy2  | AvoidNextToCorners     | Avoids spaces adjacent to corners (risky positions)      |
| strategy3  | CornersFirst           | Prioritizes corner moves (strongest positions)           |
| strategy4  | MiniMax                | Minimizes the opponent's best possible next-turn score   |

All strategies break ties by choosing the upper-left-most valid move.

## Project Structure

```
src/cs3500/reversi/
├── Reversi.java                  # Entry point
├── model/                        # Game state and rules
│   ├── IReversiModel.java        # Model interface
│   ├── ReversiModel.java         # Core game logic
│   ├── Player.java               # BLACK / WHITE enum
│   ├── Space.java                # Board cell
│   └── Coordinate.java           # Row/col pair
├── controller/                   # Input handling
│   ├── Controller.java           # Bridges view and model
│   ├── HumanPlayer.java          # Human input via view
│   ├── AIPlayer.java             # AI input via strategy
│   └── ViewListener.java         # View event interface
├── view/                         # Rendering
│   ├── ReversiGraphicsView.java  # Swing window
│   ├── ReversiPanel.java         # Hexagonal board renderer
│   └── TextView.java             # ASCII text view
└── strategy/                     # AI logic
    ├── IReversiStrategies.java   # Strategy interface
    ├── AsManyPiecesAsPossible.java
    ├── AvoidNextToCorners.java
    ├── CornersFirst.java
    └── MiniMax.java
```

## Design

- **MVC architecture** with clear separation between model, view, and controller
- **Strategy pattern** for interchangeable AI behaviors
- **Observer/Listener pattern** for view-to-controller communication
- Each player gets their own window and controller instance
