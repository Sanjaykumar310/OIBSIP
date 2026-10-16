
import java.util.Random;
import java.util.Scanner;

public class NumberGuessingGame {

    private static final int MIN_NUMBER = 1;
    private static final int MAX_NUMBER = 100;
    private static final int MAX_ATTEMPTS = 7;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        int round = 0;
        int wins = 0;
        int losses = 0;
        int totalAttempts = 0;

        System.out.println("====================================");
        System.out.println("       JAVA NUMBER GUESSING GAME");
        System.out.println("====================================");
        System.out.println("Guess the secret number between "
                + MIN_NUMBER + " and " + MAX_NUMBER + ".");
        System.out.println("You have " + MAX_ATTEMPTS
                + " attempts in each round.");

        boolean playAgain = true;

        while (playAgain) {
            round++;

            int secretNumber = random.nextInt(
                    MAX_NUMBER - MIN_NUMBER + 1
            ) + MIN_NUMBER;

            int attempts = 0;
            boolean guessedCorrectly = false;

            System.out.println("\n========== Round " + round + " ==========");

            while (attempts < MAX_ATTEMPTS && !guessedCorrectly) {
                System.out.println("Attempts remaining: "
                        + (MAX_ATTEMPTS - attempts));
                System.out.print("Enter your guess (1-100): ");

                if (!scanner.hasNextInt()) {
                    scanner.next();
                    System.out.println(
                            "Invalid input! Enter a whole number."
                    );
                    continue;
                }

                int guess = scanner.nextInt();

                if (guess < MIN_NUMBER || guess > MAX_NUMBER) {
                    System.out.println(
                            "Please enter a number between 1 and 100."
                    );
                    continue;
                }

                attempts++;
                totalAttempts++;

                System.out.println("Attempt " + attempts
                        + " of " + MAX_ATTEMPTS);

                if (guess < secretNumber) {
                    System.out.println("Too Low!");
                } else if (guess > secretNumber) {
                    System.out.println("Too High!");
                } else {
                    System.out.println("Correct!");
                    guessedCorrectly = true;
                    wins++;
                }
            }

            if (guessedCorrectly) {
                System.out.println("You won Round " + round + "!");
                System.out.println("Round " + round
                        + " — guessed in " + attempts + " attempts.");
            } else {
                losses++;
                System.out.println("You Lost!");
                System.out.println("The correct number was: "
                        + secretNumber);
                System.out.println("Round " + round
                        + " — you used all " + attempts + " attempts.");
            }

            System.out.println("\n========== SCOREBOARD ==========");
            System.out.println("Rounds played : " + round);
            System.out.println("Rounds won    : " + wins);
            System.out.println("Rounds lost   : " + losses);
            System.out.println("Total guesses : " + totalAttempts);
            System.out.println("================================");

            while (true) {
                System.out.print("Play Again? (yes/no): ");
                String answer = scanner.next().trim();

                if (answer.equalsIgnoreCase("yes")
                        || answer.equalsIgnoreCase("y")) {
                    playAgain = true;
                    break;
                } else if (answer.equalsIgnoreCase("no")
                        || answer.equalsIgnoreCase("n")) {
                    playAgain = false;
                    break;
                } else {
                    System.out.println("Please enter yes or no.");
                }
            }
        }

        System.out.println("\n========== GAME OVER ==========");
        System.out.println("Final rounds played: " + round);
        System.out.println("Rounds won         : " + wins);
        System.out.println("Rounds lost        : " + losses);
        System.out.println("Total guesses      : " + totalAttempts);
        System.out.println("Thanks for playing!");

        scanner.close();
    }
}
