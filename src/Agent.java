import java.io.*;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class Agent extends Thread {

    Scanner scanner;
    public String inputFolder;
    public AtomicInteger counter = new AtomicInteger(0);

    public Agent(Scanner scanner, String folderName) {
        this.scanner = scanner;
        this.inputFolder = folderName;
    }

    public void run() {
        String line = null;
        String[] split = null;

        int numProducts = 0;
        while (true) {

            counter.set(0);

            try {
                Tema2.semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (this.scanner.hasNextLine() == false) {
                Tema2.semaphore.release();
                break;
            }

            line = this.scanner.nextLine();
            Tema2.semaphore.release();
            split = line.split(",");
            numProducts = Integer.parseInt(split[1]);

            Object mutex = new Object();
            for (int i = 0; i < numProducts; i++) {
                Tema2.exec.submit(new Worker(split[0], i, counter, mutex, this.inputFolder));
            }

            while (true) {

                synchronized (mutex) {

                    if (counter.get() >= Integer.parseInt(split[1])) {

                        if (counter.get() == 0) {
                            break;
                        }

                        try {
                            Tema2.myWriter1.write(line + ",shipped\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        break;

                    }
                    else {

                        try {
                            mutex.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                }

            }

        }

    }
}
