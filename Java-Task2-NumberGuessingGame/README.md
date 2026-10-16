# Java Number Guessing Game

**Track:** Java Development  
**Internship:** Oasis Infobyte (OIBSIP)  
**Task:** Task 2 — Number Guessing Game  
**Language:** Java  
**Interface:** Console

## Overview
A console-based Java game in which the computer selects a secret integer between 1 and 100. The player guesses the number and receives a `Too High!` or `Too Low!` hint after each valid guess. Each round has a seven-attempt limit.

## Features
- Generates a new random number from 1 to 100 for each round.
- Allows up to 7 valid guesses per round.
- Displays the number of attempts remaining.
- Provides `Too High!` and `Too Low!` hints.
- Reports a win or reveals the secret number after a lost round.
- Tracks the total score across rounds.
- Offers a play-again prompt.
- Rejects non-integer input without consuming an attempt.
- Rejects values outside 1–100 without consuming an attempt.

## Requirements
- Java Development Kit (JDK) 8 or later
- A terminal or command prompt

## Project structure
```text
Java-Task2-NumberGuessingGame/
├── src/
│   └── NumberGuessingGame.java
├── screenshots/
├── .gitignore
└── README.md
```

Compiled `.class` files and the `out/` directory are excluded from Git using `.gitignore`.

## Compile and run
Open a terminal in this project folder and run:

```powershell
javac -d out src\NumberGuessingGame.java
java -cp out NumberGuessingGame
```

Alternatively, compile directly into `src`:

```powershell
javac src\NumberGuessingGame.java
java -cp src NumberGuessingGame
```

## How to play
1. Run the program.
2. Enter a whole number from 1 to 100.
3. Use the high/low hints to refine your next guess.
4. Guess correctly within seven valid attempts to win the round.
5. Enter `yes` to play another round or `no` to exit.

## Testing checklist
- [ ] Correct guess produces a success message and round score.
- [ ] Guess below the secret number displays `Too Low!`.
- [ ] Guess above the secret number displays `Too High!`.
- [ ] A round ends after seven valid incorrect guesses and reveals the number.
- [ ] Non-numeric input is rejected without reducing attempts.
- [ ] Values below 1 or above 100 are rejected without reducing attempts.
- [ ] `yes` starts a new round with a new secret number.
- [ ] `no` ends the game and displays the final score.

## Screenshots
Add genuine screenshots of your own program output to `screenshots/`. Suggested files:
- `game-start.png` — title and instructions
- `game-hints.png` — a high/low hint and remaining attempts
- `game-win.png` — successful guess and round score
- `game-loss-replay.png` — loss message and replay prompt

Only include screenshots actually captured from a running build.

## Repository
[OIBSIP GitHub repository](https://github.com/Sanjaykumar310/OIBSIP)

## Learning outcomes
This project practices Java's `Random` and `Scanner` classes, loops, conditional logic, input validation, score tracking, and console interaction.
