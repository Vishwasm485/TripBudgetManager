📘 Trip Budget Manager

A JavaFX-based desktop application that helps users manage trip expenses, split costs among travelers, and calculate final settlements.
The system supports multi-user authentication and ensures each user can manage their own trips independently.

📌 Project Overview

Trip Budget Manager is a desktop expense tracking system built using:

Java (JDK 24)

JavaFX 25

SQLite (JDBC)

MVC Architecture

The application allows users to:

Register and login

Create trips with budget and duration

Add travelers to trips

Add and manage expenses

Automatically calculate expense splits

Generate final settlement results

🏗️ System Architecture

The project follows an MVC structure:

app
 └── Main.java

controller
 ├── LoginController
 ├── RegisterController
 ├── DashboardController
 ├── CreateTripController
 ├── ViewTripsController
 └── TripDetailsController

model
 ├── User
 ├── Trip
 ├── Traveler
 └── Expense

database
 ├── DBConnection
 └── DBSetup
🗄️ Database Design

SQLite database: trip.db

Users Table
users
- id (Primary Key)
- email (Unique)
- password
Trips Table
trips
- id (Primary Key)
- user_id (Foreign Key → users.id)
- name
- days
- budget
Travelers Table
travelers
- id (Primary Key)
- trip_id (Foreign Key → trips.id)
- name
Expenses Table
expenses
- id (Primary Key)
- trip_id (Foreign Key → trips.id)
- payer
- amount
- description

Foreign key constraints with cascade delete ensure data consistency.

🔐 Authentication Flow

User registers with email and password

User logs in

Dashboard loads only the logged-in user's trips

Each trip is linked to user_id

Users cannot access other users' trips

💰 Expense Split Algorithm

The application uses a greedy settlement algorithm:

Calculate total expenses

Compute equal share per traveler

Determine each traveler’s balance:

Positive → Creditor

Negative → Debtor

Match debtors to creditors

Generate minimal settlement transactions

Time Complexity: O(n log n)
Space Complexity: O(n)

🚀 Features

✔ Multi-user authentication
✔ User-specific trip isolation
✔ Trip creation with budget and duration
✔ Add travelers
✔ Add/delete expenses
✔ Automatic expense splitting
✔ Final settlement summary
✔ SQLite persistent storage
✔ JavaFX UI with CSS styling

🖥️ Application Flow
Main
 ↓
Login Screen
 ↓
Register (optional)
 ↓
Dashboard
 ↓
Create Trip / View Trips
 ↓
Trip Details
 ↓
Expense Calculation & Settlement
⚙️ Technologies Used
Technology	Purpose
Java	Core Programming
JavaFX	UI Development
SQLite	Local Database
JDBC	Database Connectivity
MVC Pattern	Clean Architecture
📦 How to Run
Requirements

JDK 24

JavaFX SDK 25

SQLite JDBC driver

VM Options (IntelliJ)
--module-path C:\javafx\javafx-sdk-25.0.2\lib
--add-modules javafx.controls,javafx.fxml
Run Command

Run Main.java

The database file trip.db will be created automatically.

🧠 Design Principles Followed

MVC Separation

Encapsulation

Foreign Key Integrity

Modular Controllers

Session-based User Flow

📈 Future Improvements

Password hashing (SHA-256)

Role-based authentication (Admin/User)

Export settlement to PDF

Graph visualization of expenses

Cloud database support

Dark mode UI

Trip sharing between users

🎓 Academic Value

This project demonstrates:

JavaFX event handling

JDBC integration

SQL relational modeling

Multi-user architecture

Greedy algorithm implementation

Clean layered application design

Suitable for:

MCA Mini Project

Java Desktop Application Project

Database + Java Integration Project

👤 Author

Vishwas M
MCA Student

📜 License

This project is developed for educational purposes.
