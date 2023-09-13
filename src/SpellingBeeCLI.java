import java.util.Scanner;

public class SpellingBeeCLI {

    public static void main(String[] args) {
        String input = "";
        Scanner scanner = new Scanner(System.in);
        UserFunctions functions = new UserFunctions();
        Boolean continueGame = true;

        System.out.println ("Welcome to the Spelling Bee!");
        //TODO get the new and help command constants from userfunctions here
        System.out.println ("Type \"new\" to create a new puzzle, or \"help\" to see all commands.");
        while (continueGame) {
            input = scanner.nextLine();
            continueGame = functions.parseCommand(input);
        }

        scanner.close();
    }

}