package model;

public class LoginSession {
    private static int loggedInUserId = -1;

    public static void setLoggedInUserId(int userId) {
        loggedInUserId = userId;
    }

    public static int getLoggedInUserId() {
        return loggedInUserId;
    }

    public static void logout() {
        loggedInUserId = -1;
    }

    public static boolean isLoggedIn() {
        return loggedInUserId != -1;
    }
}
