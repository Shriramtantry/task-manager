// This is a safety wrapper that ensures our code runs only after the HTML page is fully loaded.
document.addEventListener('DOMContentLoaded', function () {

    // --- Logic for the Registration Page ---

    // First, find the registration form on the page by its ID.
    const registerForm = document.getElementById('registerForm');

    // We only want to run this code if we are actually on the registration page.
    if (registerForm) {
        // Add a "listener" that waits for the user to submit the form.
        registerForm.addEventListener('submit', async function (event) {
            // Prevent the form from doing its default browser refresh.
            event.preventDefault();

            // Get the values the user typed into the input boxes.
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;

            // Send the data to our backend registration endpoint.
            const response = await fetch('/api/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ username, password }),
            });

            // Check if the registration was successful.
            if (response.ok) {
                alert('Registration successful! Please login.');
                // Redirect the user to the login page.
                window.location.href = '/login.html';
            } else {
                // If there was an error, show an alert.
                alert('Registration failed. Please try a different username.');
            }
        });
    }


    // --- Logic for the Login Page ---

    // Find the login form on the page by its ID.
    const loginForm = document.getElementById('loginForm');

    // Only run this code if we are on the login page.
    if (loginForm) {
        // Add a listener that waits for the user to submit the form.
        loginForm.addEventListener('submit', async function (event) {
            // Prevent the default browser refresh.
            event.preventDefault();

            // Get the values from the input boxes.
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;

            // Send the data to our backend login endpoint.
            const response = await fetch('/api/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ username, password }),
            });

            // Check if the login was successful.
            if (response.ok) {
                //alert('Login successful! Redirecting to dashboard...');
                // Redirect the user to the main dashboard.
                //window.location.href = '/dashboard.html';
                // --- ADD THESE LINES ---
                const user = await response.json(); // Read the user data from the response.
                localStorage.setItem('userId', user.id); // Save the user's ID to the sticky note.

                alert('Login successful! Redirecting to dashboard...');
                window.location.href = '/dashboard.html'; // Now redirect.
            } else {
                // If there was an error, show an alert.
                alert('Login failed. Please check your username and password.');
            }
        });
    }
    // --- Logic for the Dashboard Page ---

    // Find the form and the list on the dashboard page.
    const newTaskForm = document.getElementById('newTaskForm');
    const taskList = document.getElementById('taskList');

    // This function will get the tasks from the server and display them.
    async function fetchAndDisplayTasks() {
        // Get the remembered user ID from the sticky note.
        const userId = localStorage.getItem('userId');
        if (!userId) {
            alert('You are not logged in!');
            window.location.href = '/login.html';
            return;
        }

        // Ask the server for this user's tasks.
        const response = await fetch(`/api/tasks/${userId}`);
        const tasks = await response.json();

        // Clear the list before adding new items.
        taskList.innerHTML = '';

        // Loop through each task and add it to the page.
        tasks.forEach(task => {
            const listItem = document.createElement('li');
            listItem.textContent = task.task_description;
            taskList.appendChild(listItem);
        });
    }

    // Only run this code if we are on the dashboard page.
    if (newTaskForm) {
        // Display the tasks when the page first loads.
        fetchAndDisplayTasks();

        // Add a listener for when the user submits the "Add Task" form.
        newTaskForm.addEventListener('submit', async function(event) {
            event.preventDefault();

            const taskDescription = document.getElementById('taskDescription').value;
            const userId = localStorage.getItem('userId');

            // Send the new task to the server.
            await fetch('/api/tasks', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    task_description: taskDescription,
                    user_id: parseInt(userId) // Make sure user_id is a number
                })
            });

            // Clear the input box.
            document.getElementById('taskDescription').value = '';
            // Refresh the list to show the new task.
            fetchAndDisplayTasks();
        });
    }
});