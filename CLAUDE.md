# CLAUDE.md — Project Rules for AI Assistants

These rules are **mandatory**. Follow them in every interaction on this project.

---

## Project Overview

- **Language:** Java 11
- **Build:** Maven (`mvn compile`, `mvn test`)
- **Test framework:** JUnit 4.13.2
- **Package root:** `cs3500.reversi`
- **Source:** `src/main/java/cs3500/reversi/`
- **Tests:** `src/test/java/cs3500/reversi/`
- **Roadmap:** See `PLAN.md` for the feature roadmap and current phase

## Directory Structure

```
src/main/java/cs3500/reversi/
├── Reversi.java                  # Entry point
├── controller/                   # Controllers, player types, listeners
├── model/                        # Game state, board, coordinates, interfaces
├── strategy/                     # AI strategies (pluggable)
└── view/                         # Swing GUI, text view, hexagon rendering

src/test/java/cs3500/reversi/     # All tests and mock strategies
```

---

## Subagent Policy

- Use subagents (`Agent` tool with `Explore` type) for investigating unfamiliar parts of the codebase
- Use subagents for researching external API patterns or library usage
- Do **NOT** read more than 5 files directly in the main context — delegate to subagents instead
- For simple targeted lookups (1–2 files, known path), direct reads are fine

## Post-Change Hook

After every `Edit` or `Write` to any file under `src/main/java/cs3500/reversi/**/*.java` or `src/test/java/cs3500/reversi/**/*.java`, follow this checklist:

1. **Tests:** Create or update tests for the changed code. Run `mvn test` to confirm they pass.
2. **README / .gitignore:** Check if the change adds new file types, dependencies, or directories that need updating. Update if needed.
3. **Commit suggestion:** Output a `COMMIT: <3-6 words>` message suggestion at the end of the response.

## Compaction Rules

When compacting context, **always preserve**:

- The full list of files modified in the current session
- All test commands that have been run and their pass/fail status
- The current phase and step being worked on from `PLAN.md`
- Any unresolved errors or blocked tasks

---

## Code Style & Conventions

### Architecture

- **MVC pattern** — model knows nothing about views or controllers
- **Read-only model interface** (`IReadOnlyReversiModel`) — views only get the read-only version
- **Strategy pattern** for AI — all strategies implement `IReversiStrategies`
- Do NOT put UI state (highlights, labels, animation state) in the model
- Do NOT put game rules in the controller or view

### Java Style

- Java 11 — no features from newer versions
- Use interfaces for public-facing types (`IReversiModel`, `ISpace`, `ITextView`, etc.)
- Package-private by default; only `public` when needed across packages
- No wildcard imports — use explicit imports
- Prefer composition over inheritance

### Dependencies

- The project is dependency-free besides JUnit. Keep it that way.
- Do NOT add external libraries without explicit user approval
- Prefer JDK built-in libraries (`javax.swing`, `javax.sound.sampled`, `java.io`, etc.)
- Do NOT use Java serialization — use plain-text or JSON formats for persistence

### Testing

- All test classes go in `src/test/java/cs3500/reversi/`
- Mock strategies go in the test directory (e.g., `MockCornersFirst`, `MockMiniMax`)
- Run tests with: `mvn test`
- Compile check with: `mvn compile`
- When adding features, write tests first or alongside — never leave a feature untested

---

## Safety Rules

### What NOT to Modify Without Tests

- `ReversiModel.java` — core game logic, any change here must have test coverage
- `IReversiModel.java` / `IReadOnlyReversiModel.java` — interface changes cascade everywhere
- Any strategy class — algorithmic bugs silently produce wrong moves

### What NOT to Do

- Do NOT modify working strategy classes. Create new ones instead and keep originals as reference.
- Do NOT add "Play Again" by mutating existing model state. Create a fresh model instance.
- Do NOT block the Swing EDT. Use `javax.swing.Timer` or background threads for delays/AI thinking.
- Do NOT use `Thread.sleep` on the Event Dispatch Thread.
- Do NOT use Java serialization for save/load — it breaks when classes are renamed or moved.
- Do NOT skip testing undo edge cases (undo after pass, undo on opponent's turn, undo after game over).
- Do NOT do a full rewrite of any existing file. Make incremental, targeted changes.
- Do NOT rename packages or move files without explicit user approval.

### Git Discipline

- Keep commits small and focused — one logical change per commit
- Never commit code that doesn't compile (`mvn compile` must pass)
- Never commit code with failing tests (`mvn test` must pass)
- Write commit messages as imperative present tense (e.g., "Add score display to GUI")

---

## Build & Run Commands

| Task | Command |
|------|---------|
| Compile | `mvn compile` |
| Run tests | `mvn test` |
| Run game | `mvn exec:java -Dexec.mainClass="cs3500.reversi.Reversi"` |
| Clean build | `mvn clean compile` |
| Package JAR | `mvn package` |

---

## Current Roadmap Reference

The implementation roadmap lives in `PLAN.md`. Always check which phase/step is current before starting work. Implement features **in the order listed** — the ordering accounts for dependencies and safety.
