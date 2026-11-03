# Digital Library Management System (Java)

A console-based library management application built with Core Java and object-oriented programming.

## Features
- Admin login and dashboard
- User registration and login
- Add, update, delete, search, and list books
- Borrow and return books with a 14-day loan period
- FIFO reservation queue for unavailable books; the next reservation is automatically fulfilled when a book is returned
- Automatic overdue fine calculation at Rs. 5 per day
- Fine balance display and payment
- User queries and admin query review
- Persistent storage using Java object serialization (`library-data.ser`)

## Demo credentials
- Admin: `admin` / `admin123`
- Seed user: `user1` / `1234`

## Requirements
- JDK 8 or later
- Windows PowerShell, Command Prompt, or another terminal

## Compile and run
From this project directory:

```powershell
New-Item -ItemType Directory -Force out | Out-Null
javac -d out src\*.java
java -cp out Main
```

The application creates `library-data.ser` in the current working directory to save books, users, fines, and reservations between runs. Keep this file if you want to retain the saved library data.
