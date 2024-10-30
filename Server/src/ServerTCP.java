import java.io.*;
import java.net.*;
import java.util.Random;

public class ServerTCP {
    private static String[] generateRandomExercise() {
        String[][] exercises = {
                {"Tu hai bisogno di aiuto.", "Tu _ bisogno di aiuto.", "a. ho", "b. hai", "c. hei", "You need help."},
                {"Una giacca rossa.", "Una giacca _ .", "a. rossa", "b. rosso", "c. rossi", "A red jacket."},
                {"Io devo andare a Milano.", "Io devo _ a Milano", "a. andaro", "b. uscire", "c. andare", "I have to go to Milan."},
                {"Noi abbiamo una cucina grande.", "Noi _ una cucina grande.", "a. hai", "b. abbiamo", "c. avete", "We have a big kitchen."},
                {"Ho bisogno di comprare uno zaino.", "Ho bisogno di _ uno zaino.", "a. comprare", "b. cantare", "c. mangiare", "I need to buy a backpack."},
                {"Io sono di Milano.", "Io sono _ Milano.", "a. la", "b. di", "c. ha", "I am from Milan."},
                {"Ciao, io sono Luca.", "Ciao, _ sono Luca.", "a. io", "b. c e", "c. ho", "Hi, I am Luca."},
                {"Laura viva a Italia.", "Laura _ a Italia.", "a. vivo", "b. vivi", "c. viva", "Laura lives in Italy."},
                {"Loro sono di qui.", "Loro _ di qui.", "a. sono", "b. siete", "c. salve", "They are from here."},
                {"Tu parli italiano.", "Tu _ italiano", "a. parlo", "b. parla", "c. parli", "You speak Italian."},
                {"Ti piace studiare inglese?", "Ti piace _ inglese?", "a. studio", "b. studiare", "c. studi", "Do you like to study English?"},
                {"Lui lavora questo giovedi.", "Lui _ questo giovedi.", "a. lavora", "b. lavori", "c. lavoro", "He works this Thursday."},
                {"Tu hai un cane, vero?", "Tu _ un cane, vero?", "a. hai", "b. vivi", "c. parli", "You have a dog, right?"},
                {"Mi piacciono questo appartamento.", "Mi _ questo appartamento.", "a. piacere", "b. piace", "c. piacciono", "I like this apartment."}
        };

        Random rand = new Random();
        return exercises[rand.nextInt(exercises.length)];
    }

    private static String handleGuess(char guess, String[] exercise, int attempts) {
        for (int i=2;i<=4;i++)
            if (exercise[i].charAt(0)==guess && exercise[i].charAt(1)=='.')
                if (exercise[0].contains(exercise[i].substring(3)) && !exercise[1].contains(exercise[i].substring(3)))
                    return "Correct! " + exercise[0] + "  Meaning: " + exercise[5] + "#" + attempts;

        attempts--;
        for (int i=2;i<=4;i++)
            if (exercise[0].contains(exercise[i].substring(3)) && !exercise[1].contains(exercise[i].substring(3)))
                return "Wrong or invalid! The right answer was " + exercise[i] + " " + exercise[0] +"  Meaning: " + exercise[5] + "#" + attempts;

        return "Invalid choice!"+ "#" + attempts;
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8888);

        System.out.println("Waiting for the first player to connect...");
        Socket client1 = serverSocket.accept();
        PrintWriter out1 = new PrintWriter(client1.getOutputStream(), true);
        out1.println("Welcome! You are Player 1.");
        System.out.println("Player 1 connected from: " + client1.getRemoteSocketAddress());

        System.out.println("Waiting for the second player to connect...");
        Socket client2 = serverSocket.accept();
        PrintWriter out2 = new PrintWriter(client2.getOutputStream(), true);
        out2.println("Welcome! You are Player 2.");
        System.out.println("Player 2 connected from: " + client2.getRemoteSocketAddress());

        String[] exercise = generateRandomExercise();
        int counter = 6;
        int attempts = 3;

        while (attempts > 0) {
            for (Socket client : new Socket[]{client1, client2}) {
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);

                String gameStatus = exercise[1] + "\n" + "\n" + exercise[2] + "\n" + exercise[3] + "\n" + exercise[4] + "\n";
                out.println(gameStatus);
                out.println("Exercises left: " + counter);
                out.println("Lives remaining: " + attempts);

                if (counter==0) {
                    out.println("You win!");
                    break;
                }

                try {BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    char guess = in.readLine().charAt(0);
                    String response = handleGuess(guess, exercise, attempts);
                    String[] parts = response.split("#");
                    String result = parts[0];
                    attempts = Integer.parseInt(parts[1]);

                    out.println(result);

                } catch (NullPointerException e) {
                    out.println("You lost! The sentence was " + exercise[0]);

                }

                counter--;
                exercise=generateRandomExercise();
            }

        }

        client1.close();
        client2.close();
    }
}


