# JavaHexagonalReversi

A Java implementation of Reversi (Othello) played on a hexagonal board, featuring a JavaFX GUI, multiple AI strategies, LAN multiplayer, game history, save/load, undo, and full MVC architecture.

## About

Players take turns placing pieces (Black and White) on a hexagonal grid. A move is valid if it traps one or more of the opponent's pieces between the newly placed piece and an existing piece of the same color — all trapped pieces are flipped. The game ends when the board is full or neither player can move. The player with the most pieces wins.

## How to Build & Run

**Prerequisites:** Java 21+, Maven

```bash
# Build
mvn compile

# Run (JavaFX — default)
mvn javafx:run

# Run (CLI with arguments)
mvn exec:java -Dexec.mainClass="cs3500.reversi.Reversi" -Dexec.args="4 human human"
mvn exec:java -Dexec.mainClass="cs3500.reversi.Reversi" -Dexec.args="4 human ai strategy1"
mvn exec:java -Dexec.mainClass="cs3500.reversi.Reversi" -Dexec.args="5 ai strategy1 ai strategy4"

# Run (legacy Swing GUI)
mvn exec:java -Dexec.mainClass="cs3500.reversi.Reversi" -Dexec.args="--swing 4 human human"

# Run tests
mvn test
```

**CLI Arguments:** `[--swing] <boardSize> <player1> [strategy] <player2> [strategy]`

- Board size must be 3 or greater
- Player types: `human` or `ai`
- AI strategies: `strategy1` through `strategy4`, or `easy`, `medium`, `hard` (see below)
- `--swing` flag uses the legacy Swing UI instead of JavaFX

## Features

- **Hexagonal board** with configurable size (3–7+)
- **JavaFX GUI** with setup dialog, or legacy Swing GUI via `--swing`
- **6 AI strategies** ranging from greedy to alpha-beta pruning
- **LAN multiplayer** — host or join games over local network
- **Game history** — scrollable move log panel
- **Save / Load** — persist and resume games (plain-text `.reversi` format)
- **Undo** — take back your last move
- **3 visual themes** — Classic (green), Dark, and High Contrast
- **Sound effects** — audio feedback for moves, flips, and game events
- **Score display** and turn indicator
- **Move highlighting** — see the last move and flipped pieces

## Controls

- **Click** a hexagon to select it
- **Enter** to confirm your move
- **P** to pass your turn
- **Ctrl+Z** to undo your last move

## AI Strategies

| Strategy   | Difficulty | Name                   | Description                                              |
|------------|------------|------------------------|----------------------------------------------------------|
| strategy1  | Easy       | AsManyPiecesAsPossible | Greedy — picks the move that captures the most pieces    |
| strategy2  | —          | AvoidNextToCorners     | Avoids spaces adjacent to corners (risky positions)      |
| strategy3  | Medium     | CornersFirst           | Prioritizes corner moves (strongest positions)           |
| strategy4  | —          | MiniMax                | Minimizes the opponent's best possible next-turn score   |
| —          | —          | DeepMiniMax            | MiniMax with deeper search                               |
| —          | Hard       | AlphaBetaMiniMax       | Alpha-beta pruning for faster, deeper search (depth 3)   |

All strategies break ties by choosing the upper-left-most valid move.

## Project Structure

```
src/main/java/cs3500/reversi/
├── Reversi.java                  # CLI entry point
├── ReversiApp.java               # JavaFX entry point
├── model/                        # Game state and rules
│   ├── IReversiModel.java        # Model interface (read-write)
│   ├── IReadOnlyReversiModel.java# Model interface (read-only)
│   ├── ReversiModel.java         # Core game logic
│   ├── Player.java               # BLACK / WHITE enum
│   ├── Space.java                # Board cell
│   ├── ISpace.java               # Space interface
│   └── Coordinate.java           # Row/col pair
├── controller/                   # Input handling
│   ├── Controller.java           # Main game controller
│   ├── NetworkController.java    # Network game controller
│   ├── HumanPlayer.java          # Human input via view
│   ├── AIPlayer.java             # AI input via strategy
│   ├── NetworkPlayer.java        # Remote player via network
│   ├── PlayerType.java           # Player type interface
│   └── ViewListener.java         # View event interface
├── view/                         # Rendering (JavaFX)
│   ├── FxReversiView.java        # Main JavaFX window
│   ├── FxReversiCanvas.java      # Hexagonal board renderer
│   ├── FxSetupDialog.java        # Pre-game setup dialog
│   ├── FxHistoryPanel.java       # Move history display
│   ├── FxHexagon.java            # Hexagon shape helper
│   ├── IGraphicsView.java        # Graphics view interface
│   ├── FxTheme.java              # Theme interface
│   ├── FxClassicTheme.java       # Green theme
│   ├── FxDarkTheme.java          # Dark theme
│   ├── FxHighContrastTheme.java  # High contrast theme
│   ├── ITextView.java            # Text view interface
│   ├── TextView.java             # ASCII text view
│   └── legacy/                   # Swing UI (deprecated)
├── strategy/                     # AI logic
│   ├── IReversiStrategies.java   # Strategy interface
│   ├── AsManyPiecesAsPossible.java
│   ├── AvoidNextToCorners.java
│   ├── CornersFirst.java
│   ├── MiniMax.java
│   ├── DeepMiniMax.java
│   ├── AlphaBetaMiniMax.java
│   └── StrategyUtils.java        # Shared utilities
├── network/                      # LAN multiplayer
│   ├── ReversiServer.java        # Game server (host)
│   ├── ReversiClient.java        # Game client (join)
│   ├── ClientConnection.java     # Connection handler
│   ├── ClientListener.java       # Message listener
│   └── MessageParser.java        # Text-based protocol
├── history/                      # Move tracking
│   ├── GameHistory.java          # Ordered move log
│   └── MoveRecord.java           # Single move record
├── persistence/                  # Save / Load
│   ├── GameSaver.java            # Export to .reversi file
│   ├── GameLoader.java           # Import from .reversi file
│   └── LoadResult.java           # Load result wrapper
└── audio/                        # Sound effects
    └── SoundManager.java         # Audio playback

src/test/java/cs3500/reversi/    # Tests (150 tests across 8 test classes)
```

## Design

- **MVC architecture** with strict separation — model knows nothing about views or controllers
- **Read-only model interface** (`IReadOnlyReversiModel`) — views only get read-only access
- **Strategy pattern** for interchangeable AI behaviors
- **Observer/Listener pattern** for view-to-controller communication
- Each player gets their own window and controller instance
- Plain-text persistence format (no Java serialization)
- Text-based network protocol over TCP sockets

## Tech Stack

- **Java 21**
- **JavaFX 21** (primary UI)
- **Swing** (legacy UI, deprecated)
- **JUnit 4.13.2** (testing)
- **Maven** (build)
- No other external dependencies
