package app;

import ui.DbConfigFrame;
import ui.LoginFrame;
import db.DBSetup;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        File configFile = new File("db_config.properties");

        if (!configFile.exists()) {
            // AGAR FILE NAHI HAI TOH SEEDHA SETUP SCREEN
            System.out.println("Config file missing! Opening Setup Screen...");
            new DbConfigFrame().setVisible(true);
        } else {
            // AGAR FILE HAI TOH TRY KAREIN CONNECT KARNE KA
            try {
                DBSetup.init(); 
                new LoginFrame().setVisible(true);
            } catch (Exception e) {
                // Agar file hone ke bawajood password galat hai
                System.out.println("Connection failed with existing file. Opening Setup Screen...");
                new DbConfigFrame().setVisible(true);
            }
        }
    }
}