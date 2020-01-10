package frankie;

import java.io.IOException;

public class CliMain {
    public static void main (String[] args) throws IOException {
        Cli test = new Cli();
        Cli Joe = new Cli("Joe Schmoe");
        Joe.run();
        Cli Sam = new Cli("Sam password");
        Sam.start();
    }
}
