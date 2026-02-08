package model;

public class User {
    private int id;
    private String username;
    private String password;
    private String role; // 'ADMIN' or 'CASHIER'

    // Constructor
    public User(int id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getPassword() { return password; }
}