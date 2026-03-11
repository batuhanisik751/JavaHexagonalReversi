# Reversi Feature Roadmap

## Current State Summary

Hexagonal Reversi with MVC architecture, 6 AI strategies (including alpha-beta pruning),
JavaFX GUI (with legacy Swing support), LAN network multiplayer, game history, save/load,
undo, sound effects, 3 visual themes, and comprehensive test suite (150 tests).
Java 21, Maven, dependencies: JavaFX 21 + JUnit 4.13.2.

---

## Phase 1 — MVP Features (COMPLETE)

### 1.1 Score Display on GUI — DONE

- Show each player's piece count live on the game window.

### 1.2 Current Turn Indicator — DONE

- Display whose turn it is in each player's window.

### 1.3 Expand Test Coverage — DONE

- Added tests for edge cases, AI legality validation, persistence, history, and network.
- 150 tests across 8 test classes.

### 1.4 Game Over Screen — DONE

- Display winner, final scores, and option to restart.

### 1.5 Move Highlighting / Last Move Indicator — DONE

- Highlight the most recently placed piece and flipped pieces.

### 1.6 Undo Last Move — DONE

- Undo support with proper edge case handling (undo after pass, game over, etc.).

---

## Phase 2 — Additional Features (COMPLETE)

### 2.1 Game History / Move Log — DONE

- `GameHistory` and `MoveRecord` classes track all moves.
- Scrollable history panel in the UI (`FxHistoryPanel`).

### 2.2 Save / Load Game — DONE

- Plain-text `.reversi` format (not Java serialization).
- `GameSaver` and `GameLoader` with `LoadResult` wrapper.

### 2.3 Difficulty Levels for AI — DONE

- Easy = `AsManyPiecesAsPossible`, Medium = `CornersFirst`, Hard = `AlphaBetaMiniMax`.
- Selectable from the setup dialog.

### 2.4 Pre-Game Setup Screen — DONE

- JavaFX setup dialog (`FxSetupDialog`) for board size, player types, AI difficulty, themes.
- Command-line support retained as fallback.

### 2.5 Sound Effects — DONE

- `SoundManager` for move, flip, pass, and game over audio.

### 2.6 Board Themes / Visual Polish — DONE

- `FxTheme` interface with 3 implementations: Classic (green), Dark, High Contrast.

### 2.7 Alpha-Beta Pruning for MiniMax — DONE

- `AlphaBetaMiniMax` strategy (depth 3). Original `MiniMax` kept as reference.

### 2.8 Network Multiplayer (LAN) — DONE

- Client-server model using TCP sockets.
- Text-based protocol (`MessageParser`).
- Host or join from the setup dialog.

---

## Phase 3 — Future Ideas (Not Started)

Potential features for future development. Not prioritized yet.

- **Online multiplayer** — Play over the internet (requires matchmaking server)
- **Game replay** — Step through a completed game move by move
- **Board size variants** — Non-hexagonal boards (square, triangular)
- **Tournament mode** — Round-robin AI tournaments with leaderboard
- **Move timer** — Time limits per turn
- **Accessibility** — Screen reader support, keyboard-only navigation
- **Localization** — Multi-language UI support

---

## Mistakes to Avoid

1. **Don't put UI state in the model.** Score labels, highlights, and animations belong in the view or controller.
2. **Don't modify working strategies.** Create new strategy classes instead.
3. **Don't use Java serialization for save/load.** Use the plain-text `.reversi` format.
4. **Don't block the UI thread.** Use background threads or timers for delays/AI thinking.
5. **Don't skip testing undo edge cases.** Undo after pass, opponent's turn, game over — all need tests.
6. **Don't add "Play Again" by mutating the existing model.** Create a fresh model instance.
7. **Don't introduce external dependencies casually.** Keep the project lean.
