# Classic Snake Game with AI Autopilot

An interactive 2D Snake game developed in **Java (Swing/AWT)** featuring customizable difficulty levels, pause/resume controls, persistent score tracking, and an autonomous **AI Autopilot** powered by pathfinding algorithms.

Developed as an academic MCA portfolio project demonstrating core Java, Object-Oriented Programming (OOP), Data Structures & Algorithms, and GUI Event Handling.

---

## 📌 Project Overview

This project implements the classic arcade Snake game with modern enhancements:
- **Object-Oriented Architecture**: Clean separation between game entities, state management, and user interface.
- **Autonomous AI Autopilot**: Computes collision-free paths to food in real-time, with survival heuristics when blocked.
- **Dynamic Difficulty System**: Selectable speed presets (Easy, Medium, Hard) that adjust game loop frequency dynamically.
- **State Management**: Full support for Start, Pause/Resume, Game Over, and Restart.
- **Persistent High Scores**: Stores and reloads the highest score locally.
- **Automated Unit Testing**: Comprehensive test suite written with **JUnit 5**.

---

## ✨ Features

1. **Classic Gameplay & Controls**:
   * Control snake heading using standard Arrow Keys (`←`, `↑`, `→`, `↓`).
   * Smooth movement with boundary collision and self-collision detection.
   * Snake grows after consuming food items.

2. **Autonomous AI Autopilot (`AI BOT: ON / OFF`)**:
   * When activated, an autonomous pathfinding agent navigates the snake to apples.
   * Avoids boundary walls and trailing body segments.
   * Employs open-space survival exploration if direct paths are temporarily obstructed.
   * Can be toggled on or off at any point during gameplay.

3. **Three Difficulty Presets**:
   * **EASY** (48ms timer delay): Slower speed, ideal for beginners.
   * **MEDIUM** (30ms timer delay): Standard balanced arcade pace.
   * **HARD** (18ms timer delay): Fast-paced reaction test.
   * Can be cycled directly via the `DIFFICULTY` button.

4. **Multi-Tiered Consumables**:
   * **Red Apple**: Awards **+10 points** and extends snake by 3 segments.
   * **Golden Apple**: Awards **+30 points** and grants a speed boost.

5. **Pause & Resume**:
   * Press **SPACEBAR** or click **PAUSE** to freeze gameplay at any moment.

6. **Local Score Persistence**:
   * Automatically records and updates all-time high scores in `files/bestScore.txt`.

---

## 🛠️ Technologies Used

* **Language**: Java 17+
* **GUI Framework**: Java Swing & AWT (`JFrame`, `JPanel`, `Graphics2D`, `Timer`, `KeyAdapter`)
* **Collections & Algorithms**: `LinkedList`, `PriorityQueue`, `HashMap`, Pathfinding / Graph Search
* **Build System**: Apache Maven (`pom.xml`)
* **Unit Testing**: JUnit 5 (`junit-jupiter-api`, `junit-jupiter-engine`)
* **Persistence**: Java I/O (`BufferedReader`, `BufferedWriter`, `FileReader`, `FileWriter`)

---

## 📁 Project Structure

```
snake-game/
├── files/                           # Game assets & high-score storage
│   ├── apple.png, goldenapple.png   # Sprites
│   └── bestScore.txt                # Local score record
├── src/
│   ├── main/java/com/snakegame/
│   │   ├── Game.java                # Main entry point (starts Swing EDT)
│   │   ├── ai/
│   │   │   └── SnakeAI.java         # AI Autopilot pathfinding agent
│   │   └── core/
│   │       ├── Snake.java           # Snake entity (segment queue & growth)
│   │       ├── GameObj.java         # Abstract base class for 2D objects
│   │       ├── Food.java            # Strategy interface for consumables
│   │       ├── Apple.java           # Red apple item (+10 pts)
│   │       ├── GoldenApple.java     # Golden bonus item (+30 pts)
│   │       ├── Direction.java       # Cardinal movement enumeration
│   │       ├── Difficulty.java      # Speed presets enum (EASY, MEDIUM, HARD)
│   │       ├── FileLineIterator.java# Custom iterator for file streaming
│   │       ├── SnakeGameBoard.java  # Main game canvas & loop timer
│   │       └── RunSnake.java        # Top JFrame container & toolbar
│   └── test/java/com/snakegame/
│       ├── SnakeTest.java           # Core mechanics & physics tests
│       └── SnakeAITest.java         # AI navigation & safety tests
├── .gitignore                       # Clean Git exclusions
├── pom.xml                          # Maven build & dependency configuration
├── run.bat                          # 1-click Windows launch script
├── run.ps1                          # PowerShell launch script
└── README.md                        # Documentation
```

---

## 🎮 Game Controls

| Action | Control |
| :--- | :--- |
| **Move Snake** | Arrow Keys (`←`, `↑`, `→`, `↓`) |
| **Pause / Resume** | `SPACEBAR` or click **PAUSE** button |
| **Restart Game** | Click **RESET** button |
| **Cycle Difficulty** | Click **DIFFICULTY** button (`EASY` / `MEDIUM` / `HARD`) |
| **Toggle AI Autopilot** | Click **AI BOT** button (`ON` / `OFF`) |
| **Instructions** | Click **INSTRUCTIONS** button |

---

## 🚀 How to Run

### Option 1: Quick Launch (Windows)
Double-click `run.bat` or run in PowerShell:
```powershell
.\run.ps1
```

### Option 2: Maven CLI
```bash
mvn compile exec:java
```

### Option 3: VS Code / IntelliJ IDEA
* Open the `snake-main` folder in your IDE.
* Run `src/main/java/com/snakegame/Game.java`.

---

## 🧪 Automated Testing

Automated tests written in **JUnit 5** verify game integrity:
```bash
mvn test
```

Test coverage includes:
- Snake movement, queue advancement, and segment growth.
- Wall collision and boundary limits.
- Body collision detection.
- Difficulty delay transitions.
- Pause/resume state locking.
- AI pathfinding accuracy and obstacle avoidance.

---

## 📸 Screenshots

*(Place gameplay screenshots here before uploading to GitHub)*

---

## 🔮 Future Improvements

- [ ] Add sound effects for eating fruit and game over.
- [ ] Implement a two-player local mode (Player vs. Player or Player vs. AI).
- [ ] Add skin/theme customizer for snake and canvas colors.

---

## 📄 License
Academic Mini-Project — Developed for study and portfolio demonstration.
