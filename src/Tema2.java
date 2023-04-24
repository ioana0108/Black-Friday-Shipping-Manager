import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.*;

public class Tema2 {

    public static ExecutorService exec;
    public static Semaphore semaphore = new Semaphore(1);
    public static FileWriter myWriter1;
    public static FileWriter myWriter2;

    public static void main(String[] args) throws IOException {

        int P = Integer.parseInt(args[1]);
        Thread[] agents = new Agent[P];

        String INPUT_FOLDER = args[0];

        File ORDERS_FILE = new File(args[0] + "/orders.txt");
        Scanner scanner = new Scanner(ORDERS_FILE);

        exec = Executors.newFixedThreadPool(P);

        {
            try {
                myWriter1 = new FileWriter("orders_out.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        {
            try {
                myWriter2 = new FileWriter("order_products_out.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        for (int i = 0; i < P; i++) {
            agents[i] = new Agent(scanner, INPUT_FOLDER);
            agents[i].start();
        }

        for (Thread agent : agents) {
            try {
                agent.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        exec.shutdown();

        myWriter1.close();
        myWriter2.close();
        scanner.close();
    }
}
