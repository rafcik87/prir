import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class Zad3Julia extends Thread {
    final static int N = 4096;
    final static int CUTOFF = 100;
    static int[][] set = new int[N][N];

    public static void main(String[] args) throws Exception {
        // Calculate set
        long startTime = System.currentTimeMillis();

        ParallelJulia[] watki = new ParallelJulia[4];
        for(int i=0; i<4; i++){
            watki[i] = new ParallelJulia(i);
            watki[i].start();
        }

        for (ParallelJulia watek : watki) {
            watek.join();
        }
        
        long endTime = System.currentTimeMillis();
        System.out.println("Obliczenia zakończone w czasie " + (endTime - startTime) + " millisekund");

        // wyświetlanie rysunku
        BufferedImage img = new BufferedImage(N, N, BufferedImage.TYPE_INT_ARGB);

        // Rysowanie pixeli
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int k = set[i][j];
                float level;
                if (k < CUTOFF) {
                    level = (float) k / CUTOFF;
                } else {
                    level = 0;
                }
                Color c = new Color(0, level, 0);
                // zielony
                img.setRGB(i, j, c.getRGB());
            }
        }
        // zapis do pliku
        ImageIO.write(img, "PNG", new File("Julia.png"));
    }

    int me;
    int begin;
    int end;

    static final double ratioY = (1.25 - -1.25) / N;
    static final double ratioX = (1.25 - -1.25) / N;
    public ParallelJulia(int me) {
        this.me = me;
        this.begin = (N/4) * me;
        this.end = (N/4) * (me+1);
    }

    public void run() {
        for (int i = begin; i < end; i++) {
            for (int j = 0; j < N; j++) {
                double rzeczywista = i*ratioY + -1.25;
                double urojona = j*ratioX + -1.25;

                Zespolona c = new Zespolona(-0.123, 0.745);
                Zespolona z = new Zespolona(rzeczywista, urojona);
                int k = 0;

                while (k < CUTOFF && z.modul() < 2.0) {
                    // z = c + z * z
                    z = c.suma(z.kwadrat());
                    k++;
                }
                set[i][j] = k;
            }
        }
    }
}

class Zespolona{
    private double r;
    private double u;
    public Zespolona(double r, double u){
        this.r = r;
        this.u = u;
    }

    Zespolona suma(Zespolona inna){
        return new Zespolona(r + inna.r, u +inna.u);
    }

    Zespolona iloczyn(Zespolona inna){
        double rzeczywista = r * inna.r - u * inna.u;
        double urojona = r*inna.u + u*inna.r;
        return new Zespolona(rzeczywista, urojona);
    }

    double modul(){
        return Math.sqrt(r*r + u*u);
    }

    Zespolona kwadrat(){
        return this.iloczyn(this);
    }
}
