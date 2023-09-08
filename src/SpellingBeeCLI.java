import java.util.Scanner;

public class SpellingBeeCLI {

    public static void main(String[] args) {
        String input = "";
        Scanner scanner = new Scanner(System.in);
        UserFunctions functions = new UserFunctions();
        bool continueGame = true;

        while (continueGame) {
            input = scanner.nextLine();
            continueGame = functions.parseCommand(input);
        }

        scanner.close();
    }

}