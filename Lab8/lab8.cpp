#include <iostream>
#include <complex>
#include <vector>
#include <fstream>
#include <chrono>
#include <thread>
#include <unistd.h>

#include <mpi.h>

const int ilosc_wierszy = 100;
const int odciencie = 100;
const int tag = 97878;

void generuj(int ilosc_robotnikow, const int ilosc_kolumn)
{
    std::vector<std::vector<int>> wartosci;
    wartosci.resize(ilosc_kolumn);
    for (int i = 0; i< wartosci.size(); i++)
    {
        wartosci[i].resize(ilosc_kolumn);
    }

    // Odebranie wartosci
    std::cout << "Odczytuje wartosci" << std::endl;
    int nr_robotnika = 0;
    std::cout << "Oczytuje wartosc robotnika nr " << nr_robotnika << std::endl;

    for(int i=0, j=0; i< wartosci.size(); i++, j++)
    {
        MPI_Status status;
        if (j == ilosc_wierszy)
        {
            std::cout << "Oczytuje wartosc robotnika nr " << nr_robotnika << std::endl;

            j = 0;
            nr_robotnika++;
        }

        MPI_Recv(&wartosci[i][0], ilosc_kolumn, MPI_INT, nr_robotnika+1, tag + i, MPI_COMM_WORLD, &status);

    }

    std::cout << "Odczytalem wartosci" << std::endl;
    std::cout << "Generuje obrazek" << std::endl;

    std::ofstream plik ("julia.ppm", std::ofstream::out);
    // Pierwsza linia
    plik << "P3 " << ilosc_kolumn << " " << ilosc_kolumn << " 255\n";
    for (int i=0; i < wartosci.size(); i++)
    {
        for (int j=0; j<wartosci.size(); j++)
        {
            int k = wartosci[j][i];
            if(k < odciencie)
            {
                k = k;
            }
            else
            {
                k = 0;
            }

            plik << "0 " << k << " 0 \t";
        }

        plik << "\n";
    }

    // for(auto& wiersz : wartosci)
    // {
    //     for (auto& kolumna : wiersz)
    //     {
    //         int k = kolumna;
    //         if(k < odciencie)
    //         {
    //             k = k;
    //         }
    //         else
    //         {
    //             k = 0;
    //         }
    //
    //         plik << "0 " << k << " 0 \t";
    //     }
    //     plik << "\n";
    // }

    plik.close();
    std::cout << "Obrazek wygenerowany i gotowy do obejrzenia." << std::endl;

}

void oblicz(int nr_robotnika, int ilosc_robotnikow, const int ilosc_kolumn)
{
    const int poczatkowy_tag = tag + ilosc_wierszy * nr_robotnika;

    double ratioY = (1.25 - -1.25) / ilosc_kolumn;
    double ratioX = (1.25 - -1.25) / ilosc_kolumn;

    int poczatek_wierszy = nr_robotnika * ilosc_wierszy;
    int koniec_wierszy = (nr_robotnika + 1) * ilosc_wierszy;

    std::vector<std::vector<int>> wartosci;
    wartosci.resize(ilosc_wierszy);
    for (int i = 0; i<ilosc_wierszy; i++)
    {
        wartosci[i].resize(ilosc_kolumn);
    }

    for (int i = poczatek_wierszy; i < koniec_wierszy; i++)
    {
        for (int j = 0; j < ilosc_kolumn; j++)
        {
            // double rzeczywista = i * ratioY + -1.25;
            // double urojona = j * ratioX + -1.25;
            double rzeczywista = (4.0 * i - 2 * ilosc_kolumn) / ilosc_kolumn;
            double urojona = (4.0 * j - 2 * ilosc_kolumn) / ilosc_kolumn;

            std::complex<double> c = std::complex<double>(rzeczywista, urojona);
            std::complex<double> z = std::complex<double>(rzeczywista, urojona);

            int k = 0;
            while (k < odciencie && std::abs(z) < 2.0)
            {
                z = c + z*z;
                k++;
            }

            wartosci[i - poczatek_wierszy][j] = k;
        }
    }

    // Wyslij
    std::cout << "Wysylam wiadomosci robotnik nr: " << nr_robotnika << std::endl;
    for (int i=0; i<ilosc_wierszy; i++)
    {
        MPI_Send(&wartosci[i][0], ilosc_kolumn, MPI_INT, 0, poczatkowy_tag + i, MPI_COMM_WORLD);
    }
}

int main(int argc, char* argv[])
{
    MPI_Init(&argc, &argv);

    int nr_procesu;
    MPI_Comm_rank(MPI_COMM_WORLD, &nr_procesu);

    int ilosc_procesow;
    MPI_Comm_size(MPI_COMM_WORLD, &ilosc_procesow);

    const int ilosc_kolumn = (ilosc_procesow - 1) * ilosc_wierszy;

    if(nr_procesu == 0)
        generuj(ilosc_procesow - 1, ilosc_kolumn);
    else
        oblicz(nr_procesu - 1, ilosc_procesow - 1, ilosc_kolumn);

    MPI_Finalize();
}
