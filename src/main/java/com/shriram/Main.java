package com.shriram;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import io.javalin.Javalin;

public class Main {
    public static void main(String[] args) {
        // This creates a new Javalin web server instance.
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
        }).start(7070);

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
                        //ctx.result("Login successful!");
                        // --- ADD THESE LINES ---
                        // Create a new user object to send back.
                        User loggedInUser = new User();
                        loggedInUser.setId(rs.getInt("id")); // Get the ID from the database result.
                        loggedInUser.setUsername(rs.getString("username"));

// Send the user object as JSON.
ctx.json(loggedInUser);
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

        // This is our new endpoint for getting all tasks for a specific user.
        // It listens for GET requests. Notice the {userId} placeholder in the address.
        app.get("/api/tasks/{userId}", ctx -> {
            // 1. Get the user ID from the path parameter.
            int userId = Integer.parseInt(ctx.pathParam("userId"));

            // 2. Write the SQL command to find all tasks for a specific user.
            String sql = "SELECT * FROM tasks WHERE user_id = ?";

            // We need a list to hold all the tasks we find.
            List<Task> tasks = new ArrayList<>();

            // 3. Use a try-catch block for safety.
            try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

                // 4. Fill in the placeholder with the user ID from the path.
                ps.setInt(1, userId);

                // 5. Execute the query and get the results.
                ResultSet rs = ps.executeQuery();

                // 6. Loop through all the results found.
                // The while(rs.next()) loop continues as long as there are more rows in the results.
                while (rs.next()) {
                    // For each row, create a new Task object.
                    Task task = new Task();
                    task.setTask_description(rs.getString("task_description"));
                    task.setUser_id(rs.getInt("user_id"));
                    // (We can add id and is_completed later if we need them)

                    // Add the newly created task to our list.
                    tasks.add(task);
                }

                // 7. Send the list of tasks back to the user as JSON data.
                ctx.json(tasks);

            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).result("Error fetching tasks.");
            }
        });

        // This is our new endpoint for deleting a task.
        // It listens for DELETE requests. Notice the {taskId} placeholder.
        app.delete("/api/tasks/{taskId}", ctx -> {
            // 1. Get the task ID from the path parameter.
            int taskId = Integer.parseInt(ctx.pathParam("taskId"));

            // 2. Write the SQL command to delete a task with a specific ID.
            String sql = "DELETE FROM tasks WHERE id = ?";

            // 3. Use a try-catch block for safety.
            try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

                // 4. Fill in the placeholder with the task ID.
                ps.setInt(1, taskId);

                // 5. Execute the command. executeUpdate() is used for INSERT, UPDATE, and DELETE.
                int rowsAffected = ps.executeUpdate();

                // 6. Check if a row was actually deleted.
                if (rowsAffected > 0) {
                    ctx.status(200).result("Task deleted successfully.");
                } else {
                    ctx.status(404).result("Task not found."); // 404 means "Not Found"
                }

            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).result("Error deleting task.");
            }
        });
    }
}