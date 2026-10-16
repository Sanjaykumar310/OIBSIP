import java.util.Random;
import java.util.Scanner;

public class NumberGuessingGame {

    private static final Scanner scanner = new Scanner(System.in);
    private static final Random random = new Random();

    public static void main(String[] args) {

        int round = 1;
        int totalScore = 0;
        boolean playAgain = true;

        System.out.println("======================================");
        System.out.println("       JAVA NUMBER GUESSING GAME      ");
        System.out.println("======================================");
        System.out.println("Guess the secret number between 1 and 100.");
        System.out.println("You have 7 attempts in each round.");

        while (playAgain) {

            int secretNumber = random.nextInt(100) + 1;
            int maxAttempts = 7;
            int attempts = 0;
            boolean guessed = false;

            System.out.println("\n---------- Round " + round + " ----------");

            while (attempts < maxAttempts) {

                System.out.print(
                    "Enter your guess (" +
                    (maxAttempts - attempts) +
                    " attempts left): "
                );

                if (!scanner.hasNextInt()) {
                    System.out.println(
                        "Invalid input. Please enter a whole number."
                    );
                    scanner.next();
                    continue;
                }

                int guess = scanner.nextInt();

                if (guess < 1 || guess > 100) {
                    System.out.println(
                        "Please enter a number between 1 and 100."
                    );
                    continue;
                }

                attempts++;

                if (guess == secretNumber) {

                    guessed = true;

                    int roundScore =
                        (maxAttempts - attempts + 1) * 10;

                    totalScore += roundScore;

                    System.out.println(
                        "Correct! You guessed it in " +
                        attempts +
                        " attempts."
                    );

                    System.out.println(
                        "Round score: " + roundScore
                    );

                    break;

                } else if (guess > secretNumber) {

                    System.out.println("Too High!");

                } else {

                    System.out.println("Too Low!");
                }
            }

            if (!guessed) {
                System.out.println(
                    "You Lost! The number was " +
                    secretNumber +
                    "."
                );
            }

            System.out.println(
                "Total score: " + totalScore
            );

            System.out.print(
                "Play Again? (yes/no): "
            );

            String answer = scanner.next();

            if (
                !answer.equalsIgnoreCase("yes") &&
                !answer.equalsIgnoreCase("y")
            ) {
                playAgain = false;
            } else {
                round++;
            }
        }

        System.out.println("\n======================================");
        System.out.println(
            "Game over. Final score: " + totalScore
        );
        System.out.println("Thanks for playing!");
        System.out.println("======================================");

        scanner.close();
    }
}