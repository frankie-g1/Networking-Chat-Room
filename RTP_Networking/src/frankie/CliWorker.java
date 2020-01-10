package frankie;


import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class CliWorker extends Thread{

    private Socket socket;
    private BufferedWriter bw;
    private BufferedReader br;
    private String name;

    public CliWorker(Socket socket) throws IOException {
        this(socket, "user1");
    }

    public CliWorker(Socket socket, String name) throws IOException {
        this.socket = socket;
        OutputStream out = socket.getOutputStream();
        InputStream in = socket.getInputStream();
        this.bw = new BufferedWriter(new OutputStreamWriter(out));
        this.br = new BufferedReader(new InputStreamReader(in));
        this.name = name;
    }

    @Override
    public void run() {
        try {
            connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //DataOutputStream out = new DataOutputStream(socket.getOutputStream());

    private void connect() throws IOException {
        Scanner s = new Scanner(System.in);

        String login = "login " + name + "\n";



        bw.write(login);
        bw.flush();

//        bw.writeUTF(login);
    String line;
//        String line = br.readLine();
//        System.out.println(line);

        while((line = br.readLine()) != null){
            if(line.equalsIgnoreCase("ok login")){
                System.out.println("login success, this was read from br");
                //System.out.println("type chat to start chatting");
                break;
            }else{
                System.out.println(line);
               break;
            }


        }
        sendMessages();


    }

    private void sendMessages() throws IOException {
        System.out.println("Send messages to those online!");
        System.out.println("Type /send or /quit before each message");

        Scanner s = new Scanner(System.in);
        String rec;
        String msg = "";

        do{
            msg = s.nextLine();
            bw.write(msg);
            bw.flush();
            if((rec = br.readLine()) != null){
                System.out.println(rec);
            }
        }while(!msg.equalsIgnoreCase("quit"));




    }
}
