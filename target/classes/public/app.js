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
                alert('Login successful! Redirecting to dashboard...');
                // Redirect the user to the main dashboard.
                window.location.href = '/dashboard.html';
            } else {
                // If there was an error, show an alert.
                alert('Login failed. Please check your username and password.');
            }
        });
    }
});