package Zad1;

import java.io.IOException;
import java.util.Scanner;

public interface Filozofowie {
    public void uruchomProblem();
}

class FilozofowieNiesymetrycznie implements Filozofowie {
    private int iloscFilozofow;

    FilozofowieNiesymetrycznie(int ilosc) {
        iloscFilozofow = ilosc;
    }

    @Override
    public void uruchomProblem() {
        FilozofNiesymetryczny.usawIlosWidelcow(iloscFilozofow);

        for (int i = 0; i < iloscFilozofow; i++) {
            new FilozofNiesymetryczny(i).start();
        }
    }
}

class FilozofNiesymetryczny extends Thread {
    public static Semaphore[] widelce;
    private int mojNum;

    public FilozofNiesymetryczny(int nr) {
        mojNum = nr;
    }

    public static void usawIlosWidelcow(int ilosc) {
        widelce = new Semaphore[ilosc];
        for (int i = 0; i < widelce.length; i++)
            widelce[i] = new Semaphore(1);
    }

    public void run() {
        while (true) {
            System.out.println("Myslę: " + mojNum);
            czekaj();

            podniesWidelce();
            System.out.println("Zaczynam jeść: " + mojNum);
            czekaj();

            System.out.println("Konczę jeść: " + mojNum);
            odlozWidelce();
        }
    }

    private void podniesWidelce() {
        int widelecLewy = mojNum;
        int widelecPrawy = (mojNum + 1) % widelce.length;
        if (mojNum == 0) {
            widelce[widelecPrawy].acquireUninterruptibly();
            widelce[widelecLewy].acquireUninterruptibly();
        } else {
            widelce[widelecLewy].acquireUninterruptibly();
            widelce[widelecPrawy].acquireUninterruptibly();
        }
    }

    private void odlozWidelce() {
        int widelecLewy = mojNum;
        int widelecPrawy = (mojNum + 1) % widelce.length;
        widelce[widelecLewy].release();
        widelce[widelecPrawy].release();
    }

    private void czekaj() {
        try {
            Thread.sleep((long) (5000 * Math.random()));
        } catch (InterruptedException e) {
        }
    }
}

class FilozofowieRzutMoneta implements Filozofowie {
    private int iloscFilozofow;

    FilozofowieRzutMoneta(int ilosc) {
        iloscFilozofow = ilosc;
    }

    @Override
    public void uruchomProblem() {
        FilozofSemafora.usawIlosWidelcow(iloscFilozofow);

        for (int i = 0; i < iloscFilozofow; i++) {
            new FilozofSemafora(i).start();
        }
    }
}

class FilozofRzutMoneta extends Thread {
    public static Semaphore[] widelce;
    private int mojNum;
    private Random losuj = new Random();

    public FilozofRzutMoneta(int nr) {
        mojNum = nr;
    }

    public static void usawIlosWidelcow(int ilosc) {
        widelce = new Semaphore[ilosc];
        for (int i = 0; i < widelce.length; i++)
            widelce[i] = new Semaphore(1);
    }

    public void run() {
        while (true) {
            System.out.println("Myslę: " + mojNum);
            czekaj();

            podniesWidelce();
            System.out.println("Zaczynam jeść: " + mojNum);
            czekaj();

            System.out.println("Konczę jeść: " + mojNum);
            odlozWidelce();
        }
    }

    private void podniesWidelce() {
        int widelecLewy = mojNum;
        int widelecPrawy = (mojNum + 1) % widelce.length;

        boolean najpierwLewy = losuj.nextBoolean();
        boolean podnioslDwaWidelce = false;
        do {
            if (najpierwLewy)
                podnioslDwaWidelce = podniesNajpierwPotem(widelecLewy, widelecPrawy);
            else
                podnioslDwaWidelce = podniesNajpierwPotem(widelecPrawy, widelecLewy);
        } while (!podnioslDwaWidelce);
    }

    private boolean podniesNajpierwPotem(int najpierw, int potem) {
        widelce[najpierw].acquireUninterruptibly();

        if (!widelce[potem].tryAcquire()) {
            widelce[najpierw].release();
        } else {
            return true;
        }

        return false;
    }

    private void odlozWidelce() {
        int widelecLewy = mojNum;
        int widelecPrawy = (mojNum + 1) % widelce.length;
        widelce[widelecLewy].release();
        widelce[widelecPrawy].release();
    }

    private void czekaj() {
        try {
            Thread.sleep((long) (5000 * Math.random()));
        } catch (InterruptedException e) {
        }
    }
}

class FilozofowieSemafory implements Filozofowie {
    private int iloscFilozofow;

    FilozofowieSemafory(int ilosc) {
        iloscFilozofow = ilosc;
    }

    @Override
    public void uruchomProblem() {
        FilozofSemafora.usawIlosWidelcow(iloscFilozofow);

        for (int i = 0; i < iloscFilozofow; i++) {
            new FilozofSemafora(i).start();
        }
    }
}

class FilozofSemafora extends Thread {
    public static Semaphore[] widelce;
    private int mojNum;

    public FilozofSemafora(int nr) {
        mojNum = nr;
    }

    public static void usawIlosWidelcow(int ilosc) {
        widelce = new Semaphore[ilosc];
        for (int i = 0; i < widelce.length; i++)
            widelce[i] = new Semaphore(1);
    }

    public void run() {
        while (true) {
            System.out.println("Myslę: " + mojNum);
            czekaj();

            podniesWidelce();
            System.out.println("Zaczynam jeść: " + mojNum);
            czekaj();

            System.out.println("Konczę jeść: " + mojNum);
            odlozWidelce();
        }
    }

    private void podniesWidelce() {
        int widelecLewy = mojNum;
        int widelecPrawy = (mojNum + 1) % widelce.length;
        widelce[widelecLewy].acquireUninterruptibly();
        widelce[widelecPrawy].acquireUninterruptibly();
    }

    private void odlozWidelce() {
        int widelecLewy = mojNum;
        int widelecPrawy = (mojNum + 1) % widelce.length;
        widelce[widelecLewy].release();
        widelce[widelecPrawy].release();
    }

    private void czekaj() {
        try {
            Thread.sleep((long) (5000 * Math.random()));
        } catch (InterruptedException e) {
        }
    }
}

public class Zad1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int rodzaj;
        do {
            wyczyscKonsole();
            System.out.println("Wybierz problem:");
            System.out.println("1. Wykorzystanie Semaforów.");
            System.out.println("2. Niesymetryczne sięganie po widelec.");
            System.out.println("3. Wykorzystanie rzutu monetą.");
            rodzaj = scanner.nextInt();
        }while(rodzaj < 1 || rodzaj > 3);

        int ilosc;
        do{
            wyczyscKonsole();
            System.out.println("Ilość filozofów od 2 do 100:");
            ilosc = scanner.nextInt();
        }while(ilosc <2 || ilosc > 100);
        
        scanner.close();

        Filozofowie filozof;
        if (rodzaj == 1)
            filozof = new FilozofowieSemafory(ilosc);
        else if (rodzaj == 2)
            filozof = new FilozofowieNiesymetrycznie(ilosc);
        else if (rodzaj == 3)
            filozof = new FilozofowieRzutMoneta(ilosc);
        else {
            filozof = new FilozofowieSemafory(2);
            System.exit(1);
        }

        filozof.uruchomProblem();
    }

    private static void wyczyscKonsole()
    {
        try {
            Runtime.getRuntime().exec("clear");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
