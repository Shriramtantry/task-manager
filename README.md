# Multi-User Task Management Application

This is a simple, full-stack web application that allows users to register, log in, and manage their own personal to-do lists. This project was built as part of a 24-day learning plan to demonstrate core Java backend and web development skills.

---

## Technologies Used

* **Backend:** Java
* **Web Server:** Javalin
* **Database:** MySQL
* **Frontend:** HTML, CSS, JavaScript (Vanilla)
* **Build Tool:** Maven

---

## Features

* User registration and login.
* Create new tasks for a logged-in user.
* View all tasks for a logged-in user.
* Delete tasks.
* A clean, styled user interface.

---

## How to Run This Project

1.  **Prerequisites:**
    * Java JDK (Version 17 or higher)
    * Apache Maven
    * MySQL Server

2.  **Database Setup:**
    * Create a new database in MySQL named `task_manager_db`.
    * Run the SQL scripts located in the `database_setup.sql` file (we will create this file next) to create the `users` and `tasks` tables.

3.  **Configuration:**
    * Open the `src/main/java/com/shriram/DatabaseUtil.java` file.
    * Update the `PASSWORD` variable with your own MySQL root password.

4.  **Running the Server:**
    * Open the project in your favorite IDE.
    * Run the `main` method in the `src/main/java/com/shriram/Main.java` file.
    * The server will start on `http://localhost:7070`.