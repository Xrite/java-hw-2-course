package ru.hse.aabukov.db;

import java.util.List;
import java.util.Scanner;

/** Main class that interacts with user */
class Interactor {
    public static void main(String[] args) {
        interact();
    }

    private static String askName(Scanner in) {
        System.out.println("Enter a name:");
        return in.nextLine().trim();
    }

    private static String askNumber(Scanner in) {
        System.out.println("Enter a number:");
        return in.nextLine().trim();
    }

    private static String ask(Scanner in, String prompt) {
        System.out.println(prompt);
        return in.nextLine().trim();
    }

    private static void printEntries(List<PersonData> list) {
        for (var entry : list) {
            System.out.println("\"" + entry.getName() + "\", \"" + entry.getNumber() + "\"");
        }
    }

    private static int readOperation(Scanner in) {
        boolean done = false;
        int result = 0;
        while (!done) {
            try {
                result = Integer.parseInt(ask(in, "Enter an operation:"));
                done = true;
            } catch (NumberFormatException e) {
                System.out.println("Wrong command format");
            }
        }
        return result;
    }

    private static void printUsage() {
        System.out.println("Usage:\n"
                + "0 - exit\n"
                + "1 - add a record (name and phone number)\n"
                + "2 - find phones by name\n"
                + "3 - find names by phone\n"
                + "4 - delete the specified name-phone pair\n"
                + "5 - change the name of the specified pair \"name-phone\"\n"
                + "6 - change the phone of the specified pair \"name-phone\" \n"
                + "7 - print all phone-name pairs in the directory\n");
    }

    private static void interact() {
        Scanner in = new Scanner(System.in);
        var dataBase = new PhoneDataBase("phones");
        printUsage();
        int nextCommand = readOperation(in);
        while (nextCommand != 0) {
            switch (nextCommand) {
                case 1:
                    dataBase.addRecord(askName(in), askNumber(in));
                    break;
                case 2:
                    printEntries(dataBase.findByName(askName(in)));
                    break;
                case 3:
                    printEntries(dataBase.findByNumber(askNumber(in)));
                    break;
                case 4:
                    dataBase.deleteRecord(askName(in), askNumber(in));
                    break;
                case 5:
                    dataBase.changeName(askName(in), askNumber(in), ask(in, "Enter a new name:"));
                    break;
                case 6:
                    dataBase.changePhone(askName(in), askNumber(in), ask(in, "Enter a new number:"));
                    break;
                case 7:
                    printEntries(dataBase.allRecords());
                    break;
                default:
                    System.out.println("No such operation");
                    break;
            }
            nextCommand = readOperation(in);
        }
    }
}
