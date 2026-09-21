# Classic Snake Game with AI Autopilot

An interactive 2D Snake game developed in **Java (Swing/AWT)** featuring customizable difficulty levels, pause/resume controls, persistent score tracking, and an autonomous **AI Autopilot** powered by pathfinding algorithms.

Developed as an portfolio project demonstrating core Java, Object-Oriented Programming (OOP), Data Structures & Algorithms, and GUI Event Handling.

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

