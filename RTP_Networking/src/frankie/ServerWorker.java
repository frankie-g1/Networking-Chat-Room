package frankie;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ServerWorker extends Thread {


    private final Socket clientSocket;
    private Server server;
    private String login = null;
    private OutputStream outputStream;
    private boolean loggedIn = false;

    public ServerWorker(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            handleClientConnection(clientSocket);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleClientConnection(Socket clientSocket) throws IOException, InterruptedException {

        InputStream inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();

//        outputStream.write("hello".getBytes());
//        outputStream.flush();


//        BufferedReader is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//        PrintWriter os = new PrintWriter(clientSocket.getOutputStream());

        BufferedReader reader = new BufferedReader((new InputStreamReader((inputStream))));
        String line;
        while ((line = reader.readLine()) != null) {

            String[] tokens = StringUtils.split(line);
            if (tokens != null && tokens.length > 0) {
                String cmd = tokens[0];
                if ("quit".equalsIgnoreCase(cmd) || "logoff".equalsIgnoreCase(cmd)) {
                    handleLogoff();
                    break;
                } else if ("login".equalsIgnoreCase(cmd)) {
                    System.out.println("connected success");
                    handleLogin(outputStream, tokens);
                    this.loggedIn = true;
                } else if (loggedIn && "/send".equals(cmd)) {
                    handleMessage(login, tokens);
                } else {
                    String msg = "unknown" + cmd;
                    outputStream.write(msg.getBytes());
                }
            }


//            os.println(msg);
//            os.flush();
//            System.out.println("response to client:" + line);
//            line = is.readLine();
        }
        clientSocket.close();


    }

    private void handleMessage(String login, String[] tokens) throws IOException {
        List<ServerWorker> workerList = server.getWorkerList();
        String message = "";

        int i = 1;
        while (i < tokens.length) { //maybe append to a string and then send, instead of multiple .sends()
            if (i == tokens.length - 1) {
                message += tokens[i] + "\n";
            } else {
                message += tokens[i];
            }
            i++;
        }
        message = message + "\n\r";

        for (ServerWorker worker : workerList) {
            if (!login.equals(worker.getLogin())) {
                worker.send("From [" + login + "]: ");
                worker.send(message);
            }
        }


    }


    private void handleLogoff() throws IOException {
        String onlineMsg = login + " just went offline\n";

        List<ServerWorker> workerList = server.getWorkerList();

        for (ServerWorker worker : workerList) {
            if (!login.equals(worker.getLogin())) {
                worker.send(onlineMsg);
            }

        }
        clientSocket.close();
    }

    public String getLogin() {
        return login;
    }

    private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
        if (tokens.length == 3) {
            String login = tokens[1];
            String password = tokens[2];

            String msg = "ok login\n";
            outputStream.write(msg.getBytes());
            outputStream.flush(); //whatever is flushed is sent to client class
            this.login = login;
            System.out.println("User logged in successfully: " + login);

            List<ServerWorker> workerList = server.getWorkerList();

            //sends current user all those who are online
            for (ServerWorker worker : workerList) {
                if (worker.getLogin() != null) {
                    if (!login.equals(worker.getLogin())) {
                        String msg2 = "User " + worker.getLogin() + " is also online!\n";
                        this.send(msg2);
                    }
                }

            }

            //notifies others of who just logged in
            String onlineMsg = login + " just went online\n";
            for (ServerWorker worker : workerList) {
                if (!login.equals(worker.getLogin())) {
                    worker.send(onlineMsg);
                }

            }
        } else {
            String msg = "error login\n";
            outputStream.write(msg.getBytes());
            outputStream.flush();
        }

    }

    //   outputStream.write("Successful login".getBytes());

//            if(login.equals("guest") && password.equals("guest")){
//                String msg = "ok login";
//                outputStream.write(msg.getBytes());
//            }else{
//                String msg = "error login";
//                outputStream.write(msg.getBytes());
//            }


    private void send(String msg) throws IOException {
        if (login != null) {
            outputStream.write(msg.getBytes());
            outputStream.flush();
        }
    }
}

