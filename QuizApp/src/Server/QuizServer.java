package Server;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class QuizServer {

    private static final int PORT = 12345;
    private static ArrayList<ObjectOutputStream> playerStreams = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server started...");

            while (true) {
                Socket playerSocket = serverSocket.accept();
                ObjectOutputStream out = new ObjectOutputStream(playerSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(playerSocket.getInputStream());
                
                playerStreams.add(out);

                // Handle player's request to join the quiz
                String request = (String) in.readObject();
                if ("JOIN_GAME".equals(request)) {
                    out.writeObject("Welcome to the Quiz! Ready to play?");
                }

                // Continue handling the game logic here...
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
