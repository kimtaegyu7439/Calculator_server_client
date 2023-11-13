package Calculator;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client2 {

    public static void main(String[] args) {
        BufferedReader in = null;
        BufferedWriter out = null;
        Socket clientSocket = null;
        Scanner scan = new Scanner(System.in);

        try {
            BufferedReader serverInfoReader = new BufferedReader(new FileReader("src\\Calculator\\serverinfo.txt"));
            String line;
            String serverIP = null;
            String temp_serverPort = null;
            int serverPort = 0;

            while ((line = serverInfoReader.readLine()) != null) {
                String[] serverInfo = line.split(" ");
                serverIP = serverInfo[0];
                temp_serverPort = serverInfo[1];
                serverPort = Integer.parseInt(temp_serverPort);
            }

            if (serverIP == null && serverPort == 0) {
                serverIP = "localhost";
                serverPort = 6780;
            }
            serverInfoReader.close();

            clientSocket = new Socket(serverIP, serverPort);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            while (true) {
                System.out.println("<Input protocol define> operation firstNum secondNum");
                System.out.println("Ex. ADD 10 20 or MINUS 20 5 or MUL 10 20 or DIV 6 2...");
                System.out.print("Input(If you want to finish, enter exit): ");
                String input = scan.nextLine();

                if (input.equals("exit")) {
                    break;
                }

                out.write(input + "\n");
                out.flush();
                String result = in.readLine();

                if (result.startsWith("Error")) {
                    System.out.println(result);
                } else {
                    System.out.println("Answer: " + result);
                }
            }
        } catch (ConnectException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        } finally {
            try {
                scan.close();
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println("Error Occured While Communicating");
            }
        }
    }
}