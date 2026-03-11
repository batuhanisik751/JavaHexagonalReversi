# Reversi Feature Roadmap — Phase 3+

## Current State Summary

All Phase 1 and Phase 2 features are complete (see `PLAN.md`).
Hexagonal Reversi with MVC architecture, 6 AI strategies, JavaFX GUI, LAN multiplayer,
game history, save/load, undo, sound effects, 3 themes, and 150 tests.

---

## Phase 3 — Polish & Quick Wins

### 3.1 AI Thinking Indicator — NOT STARTED

- Show a spinner or "Thinking..." label while the AI computes its move.
- Replace the plain 300ms delay with visible feedback, especially for harder difficulties.
- Display in the turn indicator area of `FxReversiView`.

### 3.2 Network Disconnect Handling — NOT STARTED

- Detect when a client disconnects mid-game on the server side.
- Add `onClientDisconnect()` callback to `ServerListener`.
- Show "Opponent disconnected" dialog to the remaining player.
- Allow the remaining player to save the game or return to setup.

### 3.3 Keyboard Navigation for Hex Selection — DONE

- Arrow keys to move a selection cursor between hexes on the board.
- Visual indicator for the currently focused hex (distinct from mouse hover).
- Enter to confirm placement, Escape to deselect.
- Integrate with existing keyboard bindings (P = pass, Ctrl+Z = undo).

---

## Phase 4 — Medium Features

### 4.1 Game Replay Mode — DONE

- Add a "Replay" button to the load game dialog or post-game screen.
- Reconstruct game state move-by-move from `GameHistory` / `MoveRecord`.
- Provide forward/back/play/pause controls.
- Highlight each move and its flipped pieces during replay.
- Reuse existing `FxReversiCanvas` rendering — no new board renderer needed.

### 4.2 Piece Flip Animations — NOT STARTED

- Animate captured pieces flipping from one color to another.
- Use a brief color transition or scale effect on `FxReversiCanvas`.
- Keep animations fast (200–400ms) so they don't slow down gameplay.
- Disable or speed up animations during AI vs AI games.
- Use `javafx.animation.Timeline` or `AnimationTimer` — no external libraries.

### 4.3 Win/Loss Statistics — NOT STARTED

- Track game results in a JSON file (e.g., `~/.reversi/stats.json`).
- Record: date, board size, player types, AI difficulty, winner, score, move count.
- Add a "Statistics" button to the setup dialog.
- Display stats screen with:
  - Overall win/loss/draw record.
  - Records per AI difficulty level.
  - Win streaks and longest game.
- Use `java.io` + manual JSON formatting (no external JSON library).

### 4.4 Move Timer — NOT STARTED

- Configurable per-turn time limit (e.g., 10s, 30s, 60s, unlimited).
- Add timer option to `FxSetupDialog`.
- Display countdown in the UI near the turn indicator.
- On timeout, auto-pass the current player's turn.
- Play a warning sound at 5 seconds remaining (reuse `SoundManager`).
- Support in both local and network multiplayer modes.

---

## Phase 5 — Big Features

### 5.1 AI Tournament Mode — NOT STARTED

- Round-robin tournaments: all 6 AI strategies play each other.
- Configurable board sizes and number of rounds per matchup.
- Display a leaderboard with win rates, average scores, and rankings.
- Run tournaments in a background thread with progress updates.
- Add a "Tournament" option to the setup dialog.
- Optionally allow watching individual games in real-time (spectator view).
- Build on existing `AISimulationTest` patterns for game execution.

### 5.2 Online Multiplayer via WebSocket — NOT STARTED

- Upgrade from LAN-only TCP to WebSocket-based networking.
- Use Java's built-in `java.net.http.HttpClient` WebSocket support (no new dependencies).
- Implement a lightweight relay server that pairs two players.
- Add "Online" game mode option to `FxSetupDialog`.
- Handle reconnection and connection loss gracefully.
- Reuse existing `MessageParser` protocol over WebSocket frames.

### 5.3 Board Shape Variants — NOT STARTED

- Support alternative board geometries beyond hexagonal:
  - **Square** — Standard 8×8 Othello rules.
  - **Triangular** — Experimental triangle-tiled board.
- Abstract board shape behind an interface (e.g., `IBoardShape`).
- Add board shape selector to `FxSetupDialog`.
- Adapt `FxReversiCanvas` rendering for each shape.
- Ensure all AI strategies work with any board shape.
- Requires new valid-move and flip logic per geometry.

---

## Implementation Order

Phases are ordered by dependency and complexity. Within each phase,
features are independent and can be implemented in any order.

| Phase | Feature | Depends On |
|-------|---------|------------|
| 3.1 | AI Thinking Indicator | — |
| 3.2 | Network Disconnect Handling | — |
| 3.3 | Keyboard Navigation | — |
| 4.1 | Game Replay Mode | — |
| 4.2 | Piece Flip Animations | — |
| 4.3 | Win/Loss Statistics | — |
| 4.4 | Move Timer | — |
| 5.1 | AI Tournament Mode | — |
| 5.2 | Online Multiplayer | 3.2 (disconnect handling) |
| 5.3 | Board Shape Variants | — |

---

## Mistakes to Avoid

1. **Don't block the UI thread** for animations or timers. Use `javafx.animation.Timeline`, `AnimationTimer`, or `PauseTransition`.
2. **Don't add external dependencies** without explicit approval. Use JDK built-ins for JSON, WebSocket, and animation.
3. **Don't modify existing AI strategies** for tournament mode. Run them as-is and compare results.
4. **Don't couple board shape logic to the view.** Abstract it in the model layer so strategies and persistence work with any shape.
5. **Don't store statistics in Java serialization format.** Use plain-text JSON.
6. **Don't break existing save/load format.** New features should be backwards-compatible with existing `.reversi` files.
