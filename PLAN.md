# Reversi Feature Roadmap

## Current State Summary

Working hexagonal Reversi with MVC architecture, 4 AI strategies, dual-window Swing GUI,
human/AI player support, and JUnit tests. Java 11, Maven, no external dependencies beyond JUnit.

---

## Phase 1 — MVP Features

These features strengthen the core game and are safe to add incrementally.

### 1.1 Score Display on GUI

- **Description:** Show each player's piece count live on the game window (e.g. top bar or side panel).
- **Value:** Players currently have no visible score — this is the most obvious missing UX element.
- **Prerequisites:** None.
- **Difficulty:** Low.
- **Risk:** Very low — purely additive UI change.
- **Approach:** Add a `JLabel` or painted text in `ReversiPanel`. Read scores from `IReadOnlyReversiModel.getScore()` on each repaint. No model changes needed.
- **Cleanup first?** No.

### 1.2 Current Turn Indicator

- **Description:** Display whose turn it is in each player's window (highlight "Your Turn" vs "Waiting").
- **Value:** Without this, players don't know when to act, especially in human-vs-human mode.
- **Prerequisites:** None (can be built alongside 1.1).
- **Difficulty:** Low.
- **Risk:** Very low.
- **Approach:** Query `model.getCurrentPlayer()` on repaint and render a status label. Reuse the same UI area as the score display.
- **Cleanup first?** No.

### 1.3 Expand Test Coverage

- **Description:** Add tests for edge cases — invalid moves on borders, consecutive passes leading to game over, AI strategy tie-breaking, full-board scenarios.
- **Value:** Safety net before adding any complex features. Current tests cover basics but not corner cases.
- **Prerequisites:** None.
- **Difficulty:** Low–Medium.
- **Risk:** None — tests don't change production code.
- **Approach:** Add new test classes or methods to the existing test files. Focus on model correctness first, then strategy correctness. Use `copyModel()` to test immutability guarantees.
- **Cleanup first?** No.

### 1.4 Game Over Screen

- **Description:** When the game ends, display the winner, final scores, and a "Play Again" or "Quit" option.
- **Value:** Currently the game shows a dialog and stops — a proper end screen makes the game feel complete.
- **Prerequisites:** Score display (1.1) helps but is not required.
- **Difficulty:** Low–Medium.
- **Risk:** Low. The tricky part is resetting game state cleanly for "Play Again".
- **Approach:** On `gameOver()`, show a result overlay or dialog. For "Play Again", construct a fresh `ReversiModel` and re-bind views/controllers. Do NOT try to mutate the existing model back to initial state.
- **Cleanup first?** No.

### 1.5 Move Highlighting / Last Move Indicator

- **Description:** Highlight the most recently placed piece and the pieces that were flipped.
- **Value:** Makes the game readable — without it, it's hard to tell what changed after a move.
- **Prerequisites:** Tests (1.3) give confidence the model is correct before adding view-layer tracking.
- **Difficulty:** Medium.
- **Risk:** Low–Medium. Requires tracking the last move result without polluting the model.
- **Approach:** After a successful `move()`, store the placed coordinate and flipped coordinates in the controller or a lightweight `MoveResult` object. Pass it to the view for highlight rendering. Do NOT add rendering state to the model.
- **Cleanup first?** No.

### 1.6 Undo Last Move

- **Description:** Allow a player to undo their last move (single undo, not full history).
- **Value:** Practical for learning — players can experiment without restarting.
- **Prerequisites:** Tests (1.3) to verify undo correctness. Move highlighting (1.5) shares the `MoveResult` pattern.
- **Difficulty:** Medium.
- **Risk:** Medium. Undo interacts with turn management and AI responses.
- **Approach:** Use `copyModel()` to snapshot the state before each move. Store the last snapshot. On undo, replace the current model with the snapshot and rebind views. Disable undo in AI-vs-AI mode. Only allow undoing your own move (not the opponent's).
- **Cleanup first?** No, but ensure `copyModel()` is a true deep copy (verify with tests first).

---

## Phase 2 — Additional Features

Build these after Phase 1 is solid. Ordered by safety and value.

### 2.1 Game History / Move Log

- **Description:** Record every move (player, coordinate, pieces flipped) in an ordered list. Display as a scrollable panel or export to text.
- **Value:** Enables replay, analysis, and is the foundation for save/load.
- **Prerequisites:** Move highlighting (1.5) — reuse the `MoveResult` concept.
- **Difficulty:** Medium.
- **Risk:** Low — read-only data collection, no model mutation.
- **Approach:** Create a `GameHistory` class that listens for moves (observer pattern or controller-level logging). Store a `List<MoveRecord>`. Render in a side panel using `JList` or `JTextArea`.
- **Cleanup first?** No.

### 2.2 Save / Load Game

- **Description:** Serialize game state to a file and reload it later.
- **Value:** Lets players pause and resume. Also useful for debugging specific board states.
- **Prerequisites:** Game history (2.1) makes this more valuable but is not required.
- **Difficulty:** Medium.
- **Risk:** Medium. Serialization bugs can corrupt state. File format must be stable.
- **Approach:** Use a simple JSON or plain-text format — NOT Java serialization (brittle across versions). Save: board state, current turn, board size, player types. Load: reconstruct model from saved data, then rebind views/controllers. Add a `JFileChooser` for file selection.
- **Cleanup first?** No.

### 2.3 Difficulty Levels for AI

- **Description:** Expose AI difficulty as Easy / Medium / Hard by controlling strategy selection and MiniMax depth.
- **Value:** Makes the game accessible to all skill levels.
- **Prerequisites:** None, but better after tests (1.3).
- **Difficulty:** Low–Medium.
- **Risk:** Low. Strategies are already pluggable.
- **Approach:** Easy = `AsManyPiecesAsPossible`, Medium = `CornersFirst`, Hard = `MiniMax` with higher depth. Add a pre-game selection dialog or command-line flag. Do NOT modify existing strategy classes — compose them or parameterize depth.
- **Cleanup first?** No.

### 2.4 Pre-Game Setup Screen

- **Description:** A startup dialog to choose board size, player types, AI difficulty, and player names — replacing command-line arguments.
- **Value:** Makes the game usable without terminal knowledge.
- **Prerequisites:** Difficulty levels (2.3) to have meaningful options.
- **Difficulty:** Medium.
- **Risk:** Low. Pure UI addition, no model changes.
- **Approach:** Build a `JDialog` with dropdowns and a "Start" button. On submit, construct the model/views/controllers the same way `Reversi.main()` does today. Keep command-line support as a fallback.
- **Cleanup first?** No.

### 2.5 Sound Effects

- **Description:** Play sounds on move placement, piece flip, invalid move, and game over.
- **Value:** Adds polish and feedback. Small effort, noticeable impact.
- **Prerequisites:** None.
- **Difficulty:** Low.
- **Risk:** Low. Use `javax.sound.sampled` — built into Java. Keep sounds optional.
- **Approach:** Create a `SoundManager` utility class. Load `.wav` clips on startup. Call `SoundManager.play("move")` from the controller after successful actions. Add a mute toggle.
- **Cleanup first?** No.

### 2.6 Board Themes / Visual Polish

- **Description:** Support multiple color themes (classic green, dark mode, high-contrast). Optionally add smooth piece-placement animations.
- **Value:** Visual appeal without functional risk.
- **Prerequisites:** None.
- **Difficulty:** Medium.
- **Risk:** Low for themes, Medium for animations (timing bugs, repaint issues).
- **Approach:** Extract color constants from `ReversiPanel` into a `Theme` interface with implementations. Apply theme on construction. For animations, use `javax.swing.Timer` ��� do NOT use `Thread.sleep` on the EDT.
- **Cleanup first?** Yes — extract hardcoded color values from `ReversiPanel` first.

### 2.7 Alpha-Beta Pruning for MiniMax

- **Description:** Optimize the MiniMax strategy with alpha-beta pruning for deeper search in the same time.
- **Value:** Stronger AI without changing the interface. Enables higher difficulty levels.
- **Prerequisites:** Tests (1.3) to verify identical results at same depth.
- **Difficulty:** Medium.
- **Risk:** Medium. Algorithmic bugs can silently produce wrong moves.
- **Approach:** Create a new `AlphaBetaMiniMax` strategy class. Do NOT modify the existing `MiniMax` — keep it as a reference. Compare outputs at same depth in tests before trusting the new version.
- **Cleanup first?** No.

### 2.8 Network Multiplayer (LAN)

- **Description:** Allow two players on different machines to play over a local network.
- **Value:** High fun factor, but significantly more complex.
- **Prerequisites:** Save/load (2.2) for state sync patterns. Solid test coverage (1.3).
- **Difficulty:** High.
- **Risk:** High. Networking introduces concurrency, latency, and disconnection handling.
- **Approach:** Client-server model using Java sockets. Server owns the model, clients own views. Implement a simple text protocol (e.g. `MOVE 3 4`, `STATE ...`). Start with LAN only — do NOT attempt internet play in v1. Add a timeout for disconnected players.
- **Cleanup first?** Yes — ensure the model is fully thread-safe or accessed only from one thread.

---

## Recommendations

### Build First (Top 3)

1. **Score Display + Turn Indicator (1.1 + 1.2)** — Minimal effort, maximum UX improvement. Build these together as one task.
2. **Expand Test Coverage (1.3)** — Safety net for everything that follows.
3. **Move Highlighting (1.5)** — Makes the game playable without confusion.

### Most Reusable Features

- **Game History (2.1)** — Foundation for save/load, replay, move analysis, and undo improvements.
- **Pre-Game Setup Screen (2.4)** — Reusable dialog pattern for any future configuration.
- **`Theme` interface (from 2.6)** — Once extracted, every new visual feature benefits from it.

### Most Likely to Introduce Bugs

- **Undo (1.6)** — State rollback interacts with turn logic and AI scheduling. Test heavily.
- **Network Multiplayer (2.8)** — Concurrency and state sync are inherently error-prone.
- **Alpha-Beta Pruning (2.7)** — Subtle algorithmic bugs produce silently wrong moves.

---

## Mistakes to Avoid

1. **Don't put UI state in the model.** Score labels, highlights, and animations belong in the view or controller. The model should only know about the board and rules.
2. **Don't modify working strategies.** When improving AI, create new strategy classes. Keep originals as reference implementations and test baselines.
3. **Don't use Java serialization for save/load.** It breaks when you rename or move classes. Use a simple text or JSON format you control.
4. **Don't block the Swing EDT.** Any delay (AI thinking, network calls, animations) must run on a background thread or use `javax.swing.Timer`. Blocking the EDT freezes the entire UI.
5. **Don't skip testing undo edge cases.** Undo after a pass, undo when it's the other player's turn, undo after game over — all need explicit tests.
6. **Don't add "Play Again" by mutating the existing model.** Create a fresh model instance. Resetting internal state is fragile and error-prone.
7. **Don't introduce external dependencies casually.** The project is dependency-free (besides JUnit). Keep it that way unless there's a strong reason. Prefer JDK built-in libraries.
