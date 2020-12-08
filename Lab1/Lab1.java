class Watek extends Thread {
    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(2000);
                System.out.println("Spalem 2 sekundy");
            } catch (InterruptedException e) {
                System.out.println("Dostalem sygnal interrupt");
                break;
            }
        }
    }
}

public class Lab1 {
    public static void main(String[] args) throws Exception {
        Thread w = new Watek();
        w.start();
        Thread.sleep(5000);
        w.interrupt();
        w.join();
        System.out.println("KONIEC");
    }
}
