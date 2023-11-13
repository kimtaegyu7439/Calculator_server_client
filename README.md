# Calculator_server_client

# 개요


과제의 목표는 여러 명이 동시접속 가능한 계산기 서버 프로그램을 만드는 것 입니다..



# Requirements 

•	< 요구조건 #1: 계산이 여러 번 가능해야 한다.> 

•	< 요구조건 #2: 여러 명이 동시 접속 가능해야 한다..>

•	< 요구조건 #3: 예외처리가 가능해야 한다.>

•	< 요구조건 #4: 파일을 통해 서버의 종류를 알아내야 한다.>




# 과제 요구 사항:

1.	서버 동시 처리 및 클라이언트 다중 접속: 서버는 동시에 여러 클라이언트를 처리할 수 있어야 합니다. 클라이언트는 서버에 연결되어 계산을 요청하고 서버는 이를 처리하여 응답해야 합니다.

2.	프로토콜 설명 및 확장: 클라이언트와 서버 간의 통신은 정해진 프로토콜을 따라야 합니다. 현재는 "operation firstNum secondNum" 형식의 메시지를 통해 계산을 요청하고 결과를 전달하고 있습니다. 이 프로토콜을 확장하여 더 많은 연산을 지원하거나 기능을 추가할 수 있습니다.

3.	서버 정보 파일 활용: 서버 정보 파일(serverinfo.txt)을 사용하여 서버의 IP 주소와 포트를 설정하고 있습니다.


# 구조도

![image](https://github.com/kimtaegyu7439/Calculator_server_client/assets/84448791/19f86bd8-3236-4ec1-90bb-44a8941206f4)


# server code

    ```
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
```


# client code


```
    package Calculator;
    import java.io.*;
    import java.net.*;
    import java.util.Scanner;
    
    public class Client1 {
    
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

