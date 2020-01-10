package frankie;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Cli extends Thread{

    private String server = "69.43.66.35\n";
    private int port = 8080;
    private String name;
//    private int id;
//    private static int ID;
//    private static boolean numClients;
    private Socket cliSocket;

    private ArrayList<CliWorker> clients = new ArrayList<>();

    public Cli(){

    }

    public Cli(String name){
        this.name = name;
    }
//
//    public Cli(String ser, int port){
//        this.server = ser;
//        this.port = port;
//        numClients = true;
//    }
//
//    public Cli(String ser, int port, String name){
//        this.server = ser;
//        this.port = port;
//        numClients = true;
//    }

    @Override
    public void run(){
        try{

                this.cliSocket = new Socket(server, port);
                CliWorker you = new CliWorker(cliSocket, name);
                clients.add(you);
                you.start();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

//    public int getID(){
//        return id;
//    }

//    private void loginToServer(String name) {
//
//    }
}
