import java.io.*;
import java.util.AbstractMap;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Worker implements Runnable {

    public String orderID;
    public int numProductsToSkip;
    public AtomicInteger counter = new AtomicInteger(0);
    public Object mutex;
    public String inputFolder;

    public Worker(String orderID, int numProductsToSkip, AtomicInteger counter, Object mutex, String folderName) {
        this.orderID = orderID;
        this.numProductsToSkip = numProductsToSkip;
        this.counter = counter;
        this.mutex = mutex;
        this.inputFolder = folderName;
    }

    public void run() {

        File ORDER_PRODUCTS_FILE = new File(this.inputFolder + "/order_products.txt");
        Scanner scanner = null;
        {
            try {
                scanner = new Scanner(ORDER_PRODUCTS_FILE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        String line = null;
        while (scanner.hasNextLine() == true) {

            line = null;
            line = scanner.nextLine();

            String[] split = line.split(",");

            if (split[0].equals(this.orderID) == true) {

                if (this.numProductsToSkip > 0) {
                    this.numProductsToSkip--;
                }
                else {

                    synchronized (this.mutex) {

                    counter.incrementAndGet();

                    try {
                        Tema2.myWriter2.write(line + ",shipped\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mutex.notify();

                    }
                    break;
                }
            }

        }

    }
}
