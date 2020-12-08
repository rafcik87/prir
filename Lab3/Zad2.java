mport java.util.Random;

class Pociag {
    private int iloscMiejsc;
    private int cena;

    Pociag(int iloscMiejsc, int cena) {
        this.iloscMiejsc = iloscMiejsc;
        this.cena = cena;
    }

    public synchronized boolean mamyMiejsce() {
        if (iloscMiejsc > 0) {
            iloscMiejsc--;
            return true;
        }

        return false;
    }

    public synchronized void wyjdz() {
        iloscMiejsc++;
    }

    public int cenaWejscia() {
        return cena;
    }
}


class Pazaser extends Thread {
    private int numer;
    private int iloscPieniedzy;
    private Stan stan;
    private Pociag pociag;
    private Random random;

    public Pasazer(int numer, int iloscPieniedzy, Pociag pociag) {
        this.numer = numer;
        this.iloscPieniedzy = iloscPieniedzy;
        this.stan = new KolejkaStan(this);
        this.pociag = pociag;
        this.random = new Random();
    }

    @Override
    public void run() {
        while (true) {
            czasMija();
            stan = stan.cosRobi();
        }
    }

    public boolean znalazlMiejsce() {
        return this.pociag.mamyMiejsce();
    }

    public void kupujeBilet() {
        this.iloscPieniedzy -= this.pociag.cenaWejscia();
    }

    public boolean maPieniadzeNaBilet() {
        return this.iloscPieniedzy > this.pociag.cenaWejscia();
    }

    public void dodajPieniadze(int wyplata) {
        this.iloscPieniedzy += wyplata;
    }

    public void wysiada() {
        this.pociag.wyjdz();
    }

    public String toString() {
        return String.valueOf(numer);
    }

    private void czasMija() {
        long czas = random.nextInt(1000) + 1000;

        try {
            Thread.sleep(czas);
        } catch (InterruptedException e) {
        }
    }
}

interface Stan {
    public Stan cosRobi();
}

class KolejkaStan implements Stan {
    private Pasazer pasazer;

    public KolejkaStan(Mlynowicz mlynowicz) {
        this.pasazer = pasazer;
    }

    @Override
    public Stan cosRobi() {
        if (!pasazer.maPieniadzeNaBilet()) {
            System.out.println("Pasazer " + this.pasazer + " musi miec trochę wiecej pieniędzy. Czas isc do bankomatu.");
            return new IdzieDoBankomatuStan(this.pasazer);
        }
        if (udaloSieWepchac() && pasazer.znalazlMiejsce()) {
            pasazer.kupujeBilet();
            return new JedzieStan(this.mlynowicz);
        } else {
            System.out.println("Pasazer " + this.pasazer + " walczy o miejsce w kolejce");
            return this;
        }
    }

    private boolean udaloSieWepchac() {
        Random random = new Random();
        return random.nextBoolean();
    }

    class JedzieStan implements Stan {
        private Pasazer pasazer;
        private int iloscOkrazen;

        public JedzieStan(Pasazer pasazer) {
            this.pasazer = pasazer;
            Random random = new Random();
            this.iloscOkrazen = random.nextInt(5) + 1;
        }

        @Override
        public Stan cosRobi() {
            if (iloscOkrazen == 0) {
                this.pasazer.wysiada();
                System.out.println("Koniec jazdy dla Mlynowicza " + this.mlynowicz);
                return new KolejkaStan(this.mlynowicz);
            } else {
                this.iloscOkrazen--;
                System.out.println("Pasazer " + this.pasazer + " mówi, że świetnie się bawi.");
                return this;
            }
        }
    }

    class IdzieDoBankomatuStan implements Stan {
        private Pasazer pasazer;

        public IdzieDoBankomatuStan(Pasazer pasazer) {
            this.pasazer = pasazer;
        }

        @Override
        public Stan cosRobi() {
            System.out.println("Pasazer " + this.pasazer + " biegnie szybko po wiecej pieniedzy.");
            return new WyplacaPieniadzeStan(this.pasazer);
        }
    }

    class WyplacaPieniadzeStan implements Stan {
        private Pasazer pasazer;

        public WyplacaPieniadzeStan(Pasazer pasazer) {
            this.pasazer = pasazer;
        }

        @Override
        public Stan cosRobi() {
            Random random = new Random();
            int wyplaconePieniadze = random.nextInt(300) + 50;
            this.pasazer.dodajPieniadze(wyplaconePieniadze);
            System.out.println("Pasazer " + this.pasazer + " wyplacił dużo pieniedzy. Przybyło mu "
                    + wyplaconePieniadze + "zł.");
            return new KolejkaStan(this.pasazer);
        }
    }
}
public class Zad2 {
    public static void main(String[] args) {
        Pociag pociag = new Pociag(5, 125);
        
        Random random = new Random();    
        for(int i=0; i<10; i++){
            Pasazer pasazer = new Pasazer(i, random.nextInt(500), pociag);
            pasazer.start();
        }
    }
}
