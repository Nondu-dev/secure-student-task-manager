package za.co.wethinkcode.security;

import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * Starts the web API on port 7077. Endpoints: POST /register, POST /login,
 * POST /logout, POST /tasks and GET /tasks/{id}. Protected endpoints need the
 * header "Authorization: Bearer <token>".
 */
public class Main {

    public static void main(String[] args) throws Exception {

        // Prepare the database before starting the API.
        Database.initialize();

        UserService userService = new UserService();
        TaskService taskService = new TaskService();

        // Create the web server.
        Javalin app = Javalin.create();

        // Register a new user.
        app.post("/register", ctx -> {

            try {
                RegisterRequest request =
                        readBody(ctx, RegisterRequest.class);

                userService.register(
                        request.username(),
                        request.password()
                );

                ctx.status(201).result(
                        "User registered successfully"
                );

            } catch (IllegalArgumentException e) {

                // Return an error if registration data is invalid.
                ctx.status(400).result(e.getMessage());
            }
        });

        // Log in a user and create a session token.
        app.post("/login", ctx -> {

            try {
                LoginRequest request =
                        readBody(ctx, LoginRequest.class);

                String token =
                        userService.loginAndCreateSession(
                                request.username(),
                                request.password()
                        );

                // The client uses this token for protected requests.
                ctx.status(200).result(token);

            } catch (SecurityException e) {

                // Do not reveal whether the username or password was wrong.
                ctx.status(401).result(
                        "Invalid username or password"
                );

            } catch (IllegalArgumentException e) {

                // The request body was not valid JSON.
                ctx.status(400).result(e.getMessage());
            }
        });

        // Log out a user by invalidating their session token.
        app.post("/logout", ctx -> {

            String authorization =
                    ctx.header("Authorization");

            // A valid Bearer header is required.
            if (authorization == null ||
                    !authorization.startsWith("Bearer ")) {

                ctx.status(401).result(
                        "Missing or invalid authorization"
                );

                return;
            }

            String token = authorization.substring(7);

            try {
                // Check that the session exists and has not expired.
                userService.getSessionService().getUserId(token);

                // Remove the session so the token can no longer be used.
                userService.getSessionService().removeSession(token);

                ctx.status(200).result(
                        "Logged out successfully"
                );

            } catch (SecurityException e) {

                ctx.status(401).result(
                        "Invalid or expired session"
                );
            }
        });

        // Create a task for the logged-in user.
        app.post("/tasks", ctx -> {

            String authorization =
                    ctx.header("Authorization");

            if (authorization == null ||
                    !authorization.startsWith("Bearer ")) {

                ctx.status(401).result(
                        "Missing or invalid authorization"
                );

                return;
            }

            try {
                String token = authorization.substring(7);

                // Identify the user through their session.
                int userId =
                        userService.getSessionService()
                                .getUserId(token);

                TaskRequest request =
                        readBody(ctx, TaskRequest.class);

                int taskId =
                        taskService.createTask(
                                userId,
                                request.title(),
                                request.description()
                        );

                ctx.status(201).result(
                        "Task created with ID: " + taskId
                );

            } catch (SecurityException e) {

                ctx.status(401).result(
                        "Invalid or expired session"
                );

            } catch (IllegalArgumentException e) {

                ctx.status(400).result(e.getMessage());
            }
        });

        // Retrieve a task, but only if the logged-in user owns it.
        app.get("/tasks/{id}", ctx -> {

            String authorization =
                    ctx.header("Authorization");

            if (authorization == null ||
                    !authorization.startsWith("Bearer ")) {

                ctx.status(401).result(
                        "Missing or invalid authorization"
                );

                return;
            }

            try {
                String token = authorization.substring(7);

                int userId =
                        userService.getSessionService()
                                .getUserId(token);

                int taskId =
                        Integer.parseInt(ctx.pathParam("id"));

                String task =
                        taskService.getTask(taskId, userId);

                ctx.status(200).result(task);

            } catch (AuthorizationException e) {

                // The user is logged in but does not own this task.
                // (Caught before SecurityException because it is a subclass of it.)
                ctx.status(403).result(e.getMessage());

            } catch (NumberFormatException e) {

                ctx.status(400).result("Invalid task ID");

            } catch (IllegalArgumentException e) {

                ctx.status(404).result("Task not found");

            } catch (SecurityException e) {

                ctx.status(401).result(
                        "Invalid or expired session"
                );
            }
        });

        // Start the API on port 7077.
        app.start(7077);

        System.out.println(
                "Secure Student Task Manager running on http://localhost:7077"
        );
    }

    // Reads the JSON body. A broken or missing body becomes a 400 error
    // (IllegalArgumentException) instead of a 500 server error.
    private static <T> T readBody(Context ctx, Class<T> type) {
        try {
            return ctx.bodyAsClass(type);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid request body");
        }
    }

    // Request body for registration.
    private record RegisterRequest(
            String username,
            String password
    ) {
    }

    // Request body for login.
    private record LoginRequest(
            String username,
            String password
    ) {
    }

    // Request body for creating a task.
    private record TaskRequest(
            String title,
            String description
    ) {
    }
}