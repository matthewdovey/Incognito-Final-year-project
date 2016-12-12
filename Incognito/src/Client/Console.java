package Client;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Console extends BorderPane{
    private TextField consoleInput;
    public static TextArea consoleOutput;
    private ArrayList<String> consoleHistory;
    private ArrayList<String> displayHistory;
    private ArrayList<String> commands;
    private int index = 0;
    private Scan scanner;
    private Help helper;
    private Report report;

    //TODO: How do I want the output of these commands to be shown....
    //TODO: I guess in the console output window...
    //TODO: How much do I want to allow the user to do?
    //TODO: I think get the basics done, then if there is time make it more advanced...

    public Console() {
        scanner = new Scan();
        helper = new Help();
        report = new Report();
        getCommands();
        consoleInput = new TextField();
        consoleOutput = new TextArea();
        consoleHistory = new ArrayList<>();
        displayHistory = new ArrayList<>();

        consoleInput.setStyle("-fx-background-color: #000000;" + "-fx-text-inner-color: white;");;
        consoleOutput.setEditable(false);
        setCenter(consoleOutput);
        setBottom(consoleInput);

        consoleInput.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case ENTER:
                    consoleInputHandler(consoleInput.getText());
                    index = 0;
                    consoleInput.clear();
                    break;
                case UP:
                    if (!consoleHistory.isEmpty()) {
                        if (index == consoleHistory.size()) {
                            index = 0;
                        }
                        consoleInput.setText(consoleHistory.get(index));
                        index++;
                    }
                    break;
                case DOWN:
                    if (!consoleHistory.isEmpty() && index > 0) {
                        consoleInput.setText((consoleHistory.get(index-1)));
                        index--;
                    }
            }
        });
    }

    private void consoleInputHandler(String command) {
        //TODO: pretty sure I can shorten the code as I have duplicate code.
        command = command.toLowerCase();
        String[] commandWords = command.split("\\s+");
        displayHistory.add(command);
        if (isCommand(commandWords[0])) {
            consoleHistory.add(0,command);
            updateOutput();
            switch (commandWords[0]) {
                case "help":
                    if (commandWords.length > 2) {
                        displayErrorMessage();
                    } else if (commandWords.length == 2){
                        helper.help(displayHistory, commandWords[1]);
                    } else {
                        helper.helpMenu(displayHistory);
                    }
                    break;
                case "scan":
                    if (commandWords.length == 1) {
                        helper.help(displayHistory, "scan");
                    } else {
                        List<String> followingCommands = Arrays.asList(commandWords).subList(1, commandWords.length);
                        if (!scanner.scan(followingCommands)) {
                            displayErrorMessage();
                        }
                    }
                    break;
                case "clear":
                    if (commandWords.length > 1) {
                        displayErrorMessage();
                    } else {
                        consoleOutput.clear();
                        displayHistory.clear();
                    }
                    break;
                case "ping":
                    if (commandWords.length == 1 || commandWords.length > 2) {
                        helper.help(displayHistory, "ping");
                    } else if (!isIP(commandWords[1])){
                        displayIpErrorMessage();
                    } else {
                        ping(commandWords[1]);
                    }
                    break;
                case "report":
                    if (commandWords.length == 1) {
                        helper.help(displayHistory, "report");
                    } else {
                        List<String> followingCommands = Arrays.asList(commandWords).subList(1, commandWords.length);
                    }
                    break;
                case "exit":
                    if (commandWords.length == 1) {
                        Platform.exit();
                    } else {
                        displayErrorMessage();
                    }
                    break;
            }
        } else {
            displayErrorMessage();
        }
    }

    private void updateOutput() {
        String output = "";
        for (String command : displayHistory) {
            output += command + "\n";
        }
        consoleOutput.setText(output);
        consoleOutput.setScrollTop(Double.MAX_VALUE);
    }

    private void displayErrorMessage() {
        String invalid = "Invalid command entered...";
        String output = "";
        for (String command : displayHistory) {
            output += command + "\n";
        }
        output += invalid;
        displayHistory.add(invalid);
        consoleOutput.setText(output);
        consoleOutput.setScrollTop(Double.MAX_VALUE);
    }

    private void displayIpErrorMessage() {
        String invalid = "Invalid ip entered...";
        String output = "";
        for (String command : displayHistory) {
            output += command + "\n";
        }
        output += invalid;
        displayHistory.add(invalid);
        consoleOutput.setText(output);
        consoleOutput.setScrollTop(Double.MAX_VALUE);
    }

    private boolean isCommand(String command) {
        return commands.contains(command);
    }

    private void getCommands() {
        //TODO: Work out all of the commands that will be available
        commands = new ArrayList<>();
        commands.add("help");
        commands.add("scan");
        commands.add("ping");
        commands.add("clear");
        commands.add("report");
        commands.add("exit");
    }

    private boolean isIP(String ip) {
        //TODO: take arguments after scan and check if it is an IP address if the argument does not match any scan options
        int count = 0;
        if (ip.isEmpty()) {
            return false;
        }
        String[] segments = ip.split("\\.");
        if (segments.length != 4) {
            return false;
        }
        if (ip.contains("-")) {
            for (String segment : segments) {
                if (segment.contains("-")) {
                    //TODO: might need to implement this into the for loop below as
                    //TODO: the segment length check will return false as the length > 3
                }
                count++;
            }
            //TODO: I think I need to iterate through all of the segments, check which has the "-"
            //TODO: remember which has and then act accordingly
        }
        for (String segment : segments) {
            if (segment.length() != 3 || Integer.parseInt(segment) > 255 || Integer.parseInt(segment) < 0) {
                displayErrorMessage();
                return false;
            }
        }
        System.out.println("Valid IP address...");
        return true;
    }

    private void ping(String ip) {
        System.out.println("Ping");
    }

}
