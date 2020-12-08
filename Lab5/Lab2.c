#include <stdio.h>
#include <math.h>
#include "mpi.h"

float funkcja(float x)
{
    return x*x;
}

float poleTrapezu(int a, int b, int numer, int wszystkie)
{
    float dx = (b-a) / (float)wszystkie;
    if(numer == wszystkie-1)
    {
        return funkcja(b) / 2 * dx;        
    }
    else if (numer == 0)
    {
        return funkcja(a) / 2 * dx;
    }
    else
    {
        return funkcja(a + dx * numer) * dx;
    }
    
}

int main(int argc, char **argv)
{
    const int TAG = 62087;
    const int a = 0;
    const int b = 10;

    int aktualny;
    int wszystkie;               
    MPI_Init(&argc, &argv); 
    MPI_Comm_size(MPI_COMM_WORLD, &wszystkie);
    MPI_Comm_rank(MPI_COMM_WORLD, &aktualny);
    float odebrana_wartosc = 0;
    if(aktualny != wszystkie-1)
    {
        MPI_Status status;
        int nadawca = aktualny+1;
        MPI_Recv(&odebrana_wartosc, 1, MPI_FLOAT, nadawca, TAG, MPI_COMM_WORLD, &status);
        printf("Procces %d odebral wartosc: %f\n", aktualny, odebrana_wartosc);
    }

    float aktualna_wartosc = poleTrapezu(a, b, aktualny, wszystkie);
    aktualna_wartosc += odebrana_wartosc;

    if(aktualny != 0)
    {
        int adresat = aktualny - 1;
        MPI_Send(&aktualna_wartosc, 1, MPI_FLOAT, adresat, TAG, MPI_COMM_WORLD);
        printf("Procces %d wyslal wartosc: %f\n", aktualny, aktualna_wartosc);
    }
    else
    {
        printf("Wyliczona wartosc to: %f\n", aktualna_wartosc);
    }


    MPI_Finalize();
    return 0;
}
