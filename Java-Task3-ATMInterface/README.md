
# ATM Interface — Java

## Overview
A console-based ATM simulation developed in Java for
Oasis Infobyte (OIBSIP) Java Development Task 3.

## Features
- User ID and PIN authentication.
- Access denied after three unsuccessful login attempts.
- Balance checking.
- Cash withdrawal with insufficient-funds validation.
- Deposit functionality.
- Transfer between demo accounts.
- Transaction history using ArrayList.
- Input validation for transaction amounts.
- Menu-driven interface.

## Technologies
- Java
- Object-Oriented Programming (OOP)
- ArrayList
- Scanner
- LocalDateTime

## Demo Credentials

| User ID | PIN | Account ID |
|---|---|---|
| user1 | 1234 | ACC1001 |
| user2 | 5678 | ACC1002 |

These are demonstration credentials only.

## Project Structure

- `src/Main.java` — application entry point.
- `src/ATM.java` — login and transaction menu.
- `src/Account.java` — account information and balance operations.
- `src/Bank.java` — demo account management.
- `src/Transaction.java` — transaction records.
- `out/` — compiled Java classes.
- `screenshots/` — application output screenshots.

## How to Run

Ensure the JDK is installed, then run these commands
from the project folder:

    javac -encoding UTF-8 -d out src/Account.java src/Transaction.java src/Bank.java src/ATM.java src/Main.java

    java -cp out Main

## Limitations
This is an educational simulation. Account information,
balances, and transaction history are stored in memory
and reset when the program exits. It is not connected
to a real banking system.

## Internship
Oasis Infobyte — Java Development
Task 3: ATM Interface
