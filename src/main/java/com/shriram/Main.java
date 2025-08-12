package com.shriram;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import io.javalin.Javalin;

public class Main {
    public static void main(String[] args) {
        // This creates a new Javalin web server instance.
        Javalin app = Javalin.create()
            .start(7070); // The server will listen for requests on port 7070

        System.out.println("Server has started on port 7070!");

        // This is a simple test endpoint.
        // When you go to http://localhost:7070/ in your browser,
        // it will send back the text "Welcome to the Task Manager!"
        app.get("/", ctx -> {
            ctx.result("Welcome to the Task Manager!");
        });
        // This is our new endpoint for user registration.
        // It listens for POST requests sent to the "/api/register" address.
        app.post("/api/register", ctx -> {
            // We will add the logic to save the user to the database here.
            // For now, let's just send back a confirmation message.
            //System.out.println("Received a registration request!");
            //ctx.result("User registered successfully!");
            // --- This is the new code to add ---

            // 1. Get the username and password from the incoming request.
            // The frontend will send these as a JSON object, e.g., {"username": "shriram", "password": "123"}
            User user = ctx.bodyAsClass(User.class);

            // 2. Write the SQL command to insert a new user.
            // The '?' are placeholders to prevent security problems (SQL injection).
            String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

            // 3. Use a try-catch block for safety when talking to the database.
            try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

                // 4. Fill in the placeholders with the actual user data.
                ps.setString(1, user.getUsername()); // The first '?' becomes the username
                ps.setString(2, user.getPassword());  // The second '?' becomes the password

                // 5. Execute the command to save the data to the database.
                ps.executeUpdate();

                // 6. Send a success message back to the user.
                ctx.status(201); // 201 means "Created"
                ctx.result("User registered successfully!");

            } catch (Exception e) {
                // If anything goes wrong, send an error message.
                e.printStackTrace();
                ctx.status(500); // 500 means "Internal Server Error"
                ctx.result("Error registering user.");
}
        });

        // This is our new endpoint for user login.
        // It listens for POST requests sent to the "/api/login" address.
        app.post("/api/login", ctx -> {
            // We will add the logic to check the user's credentials here.
            // For now, let's just send back a placeholder message.
            //System.out.println("Received a login request!");
            //ctx.result("Login endpoint is working!");
            // --- This is the new code to add ---

            // 1. Get the username and password from the incoming request, just like before.
            User submittedUser = ctx.bodyAsClass(User.class);

            // 2. Write the SQL command to find a user with a specific username.
            String sql = "SELECT * FROM users WHERE username = ?";

            // 3. Use a try-catch block for safety.
            try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

                // 4. Fill in the placeholder with the username from the request.
                ps.setString(1, submittedUser.getUsername());

                // 5. Execute the query. A SELECT query uses executeQuery().
                // The results are stored in a special object called a ResultSet.
                ResultSet rs = ps.executeQuery();

                // 6. Check if a user was found.
                if (rs.next()) { // The rs.next() method returns true if a result was found.
                    // A user with that username exists. Now, let's get their real password from the database.
                    String passwordFromDb = rs.getString("password");

                    // 7. Compare the password from the database with the one the user submitted.
                    if (passwordFromDb.equals(submittedUser.getPassword())) {
                        // Passwords match! Login is successful.
                        ctx.status(200); // 200 means "OK"
                        ctx.result("Login successful!");
                    } else {
                        // Passwords do not match.
                        ctx.status(401); // 401 means "Unauthorized"
                        ctx.result("Invalid password.");
                    }
                } else {
                    // No user was found with that username.
                    ctx.status(401); // 401 means "Unauthorized"
                    ctx.result("User not found.");
                }

            } catch (Exception e) {
                // If anything goes wrong, send a server error message.
                e.printStackTrace();
                ctx.status(500);
                ctx.result("Error during login.");
            }

        });

        // This is our new endpoint for creating a task.
        // It listens for POST requests sent to the "/api/tasks" address.
        app.post("/api/tasks", ctx -> {
            // 1. Get the task details from the incoming request.
            Task task = ctx.bodyAsClass(Task.class);

            // 2. Write the SQL command to insert a new task.
            // We need to save the description, its completion status, and which user it belongs to.
            String sql = "INSERT INTO tasks (task_description, is_completed, user_id) VALUES (?, ?, ?)";

            // 3. Use a try-catch block for safety.
            try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

                // 4. Fill in the placeholders with the actual task data.
                ps.setString(1, task.getTask_description()); // The first '?' is the description.
                ps.setBoolean(2, false);                     // The second '?' is the status. New tasks are always false (not completed).
                ps.setInt(3, task.getUser_id());             // The third '?' is the ID of the user who owns the task.

                // 5. Execute the command to save the data.
                ps.executeUpdate();

                // 6. Send a success message back.
                ctx.status(201); // 201 means "Created"
                ctx.result("Task created successfully!");

            } catch (Exception e) {
                // If anything goes wrong, send an error message.
                e.printStackTrace();
                ctx.status(500);
                ctx.result("Error creating task.");
            }
        });
    }
}