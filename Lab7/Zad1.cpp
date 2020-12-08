#include <iostream>
#include <cstdlib>
#include <ctime>
#include <unistd.h>
#include "mpi.h"

#define TAG 545563


void schronisko(int ilosc_klientow)
{
    std::cout << "Wez Pan psa. Za darmo i na dodatek zaszczepiony!" << std::endl;

    int ilosc_psow = (rand()%ilosc_klientow) + 1;
    while(ilosc_psow > 0 && ilosc_klientow > 0)
    {
        int czynnosc_klienta;
        MPI_Status status;
        MPI_Recv(&czynnosc_klienta,1,MPI_INT,MPI_ANY_SOURCE,TAG,MPI_COMM_WORLD, &status);

        switch (czynnosc_klienta)
        {
        case 1:
            std::cout << "Klient z checia zabiera pieska do domu." << std::endl;
            ilosc_klientow--;
            if(ilosc_psow > 1)
                std::cout << "Pozostalo tylko " << --ilosc_psow << " pieskow." << std::endl;
            else
                std::cout << "Oddano ostatniego pieska." << std::endl;
            break;

        case 2:
            std::cout << "Klient musi sie jeszcze zastanowic!" << std::endl;
            std::cout << "Zaden piesek nie zostal oddany!" << std::endl;
            break;

        case 3:
            std::cout << "Klient przyniosi nowego psiaka do schroniska!" << std::endl;
            std::cout << "Teraz mamy az " << ++ilosc_psow << " pieskow!" << std::endl;
            break;

        case 4:
            std::cout << "Klient przyniosl karme, pieski sa szczesliwe." << std::endl;
            break;

        case 5:
            std::cout << "Klient przyszedl pobawic sie z pieskami, pieski sa szczesliwe." << std::endl;
            break;

        case 6:
            ilosc_klientow--;
            if(ilosc_psow >= 2)
            {
                std::cout << "Klient nie mogl rozdzielic tej slodkiej pary. Zabral az 2 pieski" << std::endl;
                ilosc_psow -= 2;
                if(ilosc_psow >= 1)
                    std::cout << "Pozostalo tylko " << ilosc_psow << " pieskow." << std::endl;
                else
                    std::cout << "Oddano ostatnie dwa pieski." << std::endl;
            }
            else 
            {
                std::cout << "Klient chcial dwa pieski, ale był tylko jeden. Zabral go do domu." << std::endl;
            }
            break;

        default:
            std::cout << "Klient sie pomylil. Myslal, ze to schronisko dla kotow." << std::endl;
            ilosc_klientow--;
            break;
        }

        std::cout << "W schronisku jest jeszcze " << ilosc_klientow << " klientow" << std::endl;
    }

    if (ilosc_klientow == 0 && ilosc_psow == 0)
        std::cout << "Niesamowite szczescie. Co za wspanialy koniec dnia. Ostatni klient zabral ostatniego pieska!" << std::endl;
    else if(ilosc_klientow <= 0 )
        std::cout << "Koniec dnia. Nie mamy juz klientow!" << std::endl;
    else if (ilosc_psow == 0)
        std::cout << "Wszystkie pieski zostaly oddane. To byl udany dzien" << std::endl;
}

void klient(int nr_klienta)
{
    while (true)
    {
        int czynnosc_klienta = rand()%7;
        switch (czynnosc_klienta)
        {
        case 1:
        {
            std::cout << "Ale slodnki piesek. Ja klient nr " << nr_klienta << " z checia go wezme." << std::endl;
            return;
        }

        case 2:
        {
            std::cout << "Ja klient nr " << nr_klienta << " nie jestem pewien czy ten piesek do mnie pasuje." << std::endl;
            break;
        }

        case 3:
        {
            std::cout << "Ja klient nr " << nr_klienta << " przynioslem tego slodkiego psiaka, ktorego znalazlem na drodze." << std::endl;
            std::cout << "Poszukam, moze znajde pieska dla siebie" << std::endl;
            break;
        }
            
        case 4:
        {
            int ilosc_karmy = 1 + (rand() % 10);
            std::cout << "Ja klient nr " << nr_klienta << " przynioslem wam " << ilosc_karmy << "kg karmy." << std::endl;
            break;
        }
            
        case 5:
        {
            std::cout << "Ja klient nr " << nr_klienta << " przyszedlem pobawic sie z pieskami." << std::endl;
            break;
        }

        case 6:
        {
            std::cout << "Ja klient nr " << nr_klienta << " chcialbym zaadoptowac dwa pieski, znajda sie jakies?" << std::endl;
            return;
        }

        default:
        {
            std::cout << "To chyba nie jest schronisko dla kotkow." << std::endl;
            std::cout << "Ja klient nr " << nr_klienta << " ide do innego schroniska." << std::endl;
            return;
        }
        }
        MPI_Send(&czynnosc_klienta, 1, MPI_INT, 0, TAG, MPI_COMM_WORLD);
        int czas_czekania = 1 + (rand() % 3);
        sleep(czas_czekania);
    }
}

int main(int argc, char* argv[])
{
    MPI_Init(&argc, &argv);

    int nr_procesu;
    MPI_Comm_rank(MPI_COMM_WORLD, &nr_procesu);

    int ilosc_procesow;
    MPI_Comm_size(MPI_COMM_WORLD, &ilosc_procesow);

    srand(time(NULL) ^ 56621 ^ 185213<< nr_procesu);
    if(nr_procesu == 0)
        schronisko(ilosc_procesow-1);
    else
        klient(nr_procesu);

    MPI_Finalize();
}
