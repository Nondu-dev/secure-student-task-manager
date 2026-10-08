package za.co.wethinkcode.security;

import io.javalin.Javalin;

public class Main {

    public static void main(String[] args) throws Exception {

        Database.initialize();

        UserService userService = new UserService();
        TaskService taskService = new TaskService();

        Javalin app = Javalin.create();

        app.post("/register", ctx -> {

            try {
                RegisterRequest request =
                        ctx.bodyAsClass(RegisterRequest.class);

                userService.register(
                        request.username(),
                        request.password()
                );

                ctx.status(201).result(
                        "User registered successfully"
                );

            } catch (IllegalArgumentException e) {

                ctx.status(400).result(
                        e.getMessage()
                );
            }
        });

        app.post("/login", ctx -> {

            try {
                LoginRequest request =
                        ctx.bodyAsClass(LoginRequest.class);

                String token =
                        userService.loginAndCreateSession(
                                request.username(),
                                request.password()
                        );

                ctx.status(200).result(token);

            } catch (SecurityException e) {

                ctx.status(401).result(
                        "Invalid username or password"
                );
            }
        });

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
                String token =
                        authorization.substring(7);

                int userId =
                        userService.getSessionService()
                                .getUserId(token);

                TaskRequest request =
                        ctx.bodyAsClass(TaskRequest.class);

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

                ctx.status(400).result(
                        e.getMessage()
                );
            }
        });

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
                String token =
                        authorization.substring(7);

                int userId =
                        userService.getSessionService()
                                .getUserId(token);

                int taskId =
                        Integer.parseInt(
                                ctx.pathParam("id")
                        );

                String task =
                        taskService.getTask(
                                taskId,
                                userId
                        );

                ctx.status(200).result(task);

            } catch (AuthorizationException e) {

                ctx.status(403).result(
                        e.getMessage()
                );

            } catch (NumberFormatException e) {

                ctx.status(400).result(
                        "Invalid task ID"
                );

            } catch (IllegalArgumentException e) {

                ctx.status(404).result(
                        "Task not found"
                );

            } catch (SecurityException e) {

                ctx.status(401).result(
                        "Invalid or expired session"
                );
            }
        });

        app.start(7077);

        System.out.println(
                "Secure Student Task Manager running on http://localhost:7077"
        );
    }

    private record RegisterRequest(
            String username,
            String password
    ) {
    }

    private record LoginRequest(
            String username,
            String password
    ) {
    }

    private record TaskRequest(
            String title,
            String description
    ) {
    }
}