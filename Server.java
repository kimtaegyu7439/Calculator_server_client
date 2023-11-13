package Calculator;
import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static void main(String[] args) throws Exception {
        ServerSocket welcomeSocket = null;
        ExecutorService executorService = Executors.newFixedThreadPool(10); // 최대 10개의 클라이언트를 동시에 처리하는 스레드 풀

        try {
            welcomeSocket = new ServerSocket(6780);

            while (true) {
                System.out.println("Wait For Connecting.....");
                Socket connectionSocket = welcomeSocket.accept();
                System.out.println("Connected.");

                // 클라이언트 연결 시마다 스레드 풀에서 작업 실행
                executorService.submit(() -> handleClient(connectionSocket));
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (welcomeSocket != null) {
                    welcomeSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 프로그램 종료 전에 스레드 풀을 종료
            if (executorService != null && !executorService.isShutdown()) {
                executorService.shutdown();
            }
        }
    }

    private static void handleClient(Socket connectionSocket) {
        try {
            // 클라이언트 소켓에서 입력 및 출력 스트림 생성
            BufferedReader in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connectionSocket.getOutputStream()));

            while (true) {
                // 클라이언트로부터 입력을 받음
                String clientSentence = in.readLine();

                // 에러 핸들링
                String result;
                if (clientSentence == null || clientSentence.equals("exit")) {
                    break; // 클라이언트가 "exit"를 보내면 루프를 종료
                } else {
                    result = calculator(clientSentence);
                }

                // 결과 전송
                out.write(result + '\n');
                out.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connectionSocket != null) {
                    connectionSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 계산 메서드
    private static String calculator(String sentence) {
        StringTokenizer stringToken = new StringTokenizer(sentence, " ");

        // 에러 핸들링
        if (stringToken.countTokens() != 3) {
            return "Error message: Incorrect number of arguments";
        }

        String result;
        String operation = stringToken.nextToken();
        int firstNum = Integer.parseInt(stringToken.nextToken());
        int secondNum = Integer.parseInt(stringToken.nextToken());

        switch (operation) {
            case "ADD":
                result = Integer.toString(firstNum + secondNum);
                break;
            case "DIV":
                if (secondNum == 0) {
                    result = "Error message: divided by zero";
                } else {
                    result = Integer.toString(firstNum / secondNum);
                }
                break;
            case "MUL":
                result = Integer.toString(firstNum * secondNum);
                break;
            case "MINUS":
                result = Integer.toString(firstNum - secondNum);
                break;
            default:
                result = "Error message: Unknown operation";
        }

        return result;
    }
}

