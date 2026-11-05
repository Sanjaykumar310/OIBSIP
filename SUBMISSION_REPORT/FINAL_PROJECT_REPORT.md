# Final Project Report

*Internship Overview*
- Program: Oasis Infobyte (OIBSIP) – Java Development Internship.
- Duration: ...
- Deliverables: Four Java console / Swing projects.

*Project Descriptions*
1. **Number Guessing Game** – console game where the user guesses a random number (1‑100) with up to 7 attempts.
2. **ATM Interface** – console simulation of a simple ATM supporting login, balance, deposit, withdrawal, transfer, and transaction history. Data persisted in `atm-data.ser`.
3. **Online Examination System** – Swing desktop app that presents a 10‑question MCQ exam with a 5‑minute countdown, login, profile update, navigation, and result display.
4. **Digital Library Management System** – console library manager with admin/user roles, book CRUD, borrowing, returning, reservations, fine calculation, and persistence via `library-data.ser`.

*Technologies*
- Java 8+ (JDK), Swing for UI, `java.io` serialization, `java.util` collections, `java.time`.

*Implementation Highlights*
- Added robust error handling for corrupted/invalid `.ser` files (ATM and Library) with automatic backup.
- Replaced fixed frame sizes with `frame.pack()` for platform‑independent UI sizing.
- Updated startup logic to avoid overwriting existing data on I/O/permission failures.

*Test Methodology*
- Automated console tests where feasible (Number Guessing Game, ATM, Digital Library) using scripted input streams.
- Manual interactive testing for GUI components (Online Examination System) and edge‑case scenarios.
- Verified persistence across restarts and backup creation for corrupted files.

*Actual Results*
- All automated test cases passed.
- Manual GUI tests confirmed correct layout, timer behavior, and state reset after logout.
- Corrupted data recovery behaved as documented; backup files created.

*Limitations*
- GUI testing is manual; visual inspection required.
- No database integration – data stored via Java serialization only.

*Conclusion*
The four projects meet the internship requirements, with improved robustness and usability. They are ready for submission.
