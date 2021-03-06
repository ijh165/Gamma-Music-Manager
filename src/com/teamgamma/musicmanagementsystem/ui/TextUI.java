package com.teamgamma.musicmanagementsystem.ui;

import java.io.File;
import java.util.Scanner;

/**
 * Text based UI (for now)
 */
public class TextUI {
    public TextUI() {
        // Nothing.
    }

    /**
     * @return A valid path to a directory
     * @brief Function to get the user to input a file path to a directory on the system.
     */
    public String getUserInputForDirectory() {
        boolean isValidInput = false;
        String userInput = null;
        while (!isValidInput) {
            System.out.println("Enter the path to the files in the library:");

            Scanner scanner = new Scanner(System.in);
            userInput = scanner.nextLine();
            File userDirectory = new File(userInput);

            if (!userDirectory.exists()) {
                System.out.println("This file does not exits. Enter Valid one.");
            } else if (!userDirectory.isDirectory()) {
                System.out.println("This is not a path to a directory");
            } else {
                isValidInput = true;
            }
        }
        return userInput;
    }
}
