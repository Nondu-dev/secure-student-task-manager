package za.co.wethinkcode.security;

import io.javalin.Javalin;

public class Main {

    public static void main(String[] args) throws Exception {

        Database.initialize();

        UserService userService = new UserService();

        Javalin app = Javalin.create();

        // Register a new user
        app.post("/register", ctx -> {

            RegisterRequest request =
                    ctx.bodyAsClass(RegisterRequest.class);

            userService.register(
                    request.username(),
                    request.password()
            );

            ctx.status(201).result(
                    "User registered successfully"
            );
        });

        // Login and create a session
        app.post("/login", ctx -> {

            LoginRequest request =
                    ctx.bodyAsClass(LoginRequest.class);

            String sessionToken =
                    userService.loginAndCreateSession(
                            request.username(),
                            request.password()
                    );

            ctx.status(200).result(sessionToken);
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
}