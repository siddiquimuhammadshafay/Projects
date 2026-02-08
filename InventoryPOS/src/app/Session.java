package app;

import model.User;

public class Session {
    // Ye static variable current user ki details ko poore project mein save rakhta hai
    private static User currentUser;

    // Login ke waqt user object yahan save hoga
    public static void login(User user) {
        currentUser = user;
    }

    // Logout ke liye user ko null kar dena
    public static void logout() {
        currentUser = null;
    }

    // Poore project mein kahin bhi user ka data lene ke liye (e.g. Dashboard par naam dikhane ke liye)
    public static User getCurrentUser() {
        return currentUser;
    }
}