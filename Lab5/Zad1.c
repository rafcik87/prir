#include <stdio.h>
#include <math.h>
#include "mpi.h"

int main(int argc, char **argv)
{
    const int TAG = 62087;

    int aktualny;
    int wszystkie;               
    MPI_Init(&argc, &argv); 
    MPI_Comm_size(MPI_COMM_WORLD, &wszystkie);
    MPI_Comm_rank(MPI_COMM_WORLD, &aktualny);
    float odebrana_wartosc = 0;
    if(aktualny != 0)
    {
        MPI_Status status;
        int nadawca = aktualny-1;
        MPI_Recv(&odebrana_wartosc, 1, MPI_FLOAT, nadawca, TAG, MPI_COMM_WORLD, &status);
        printf("Procces %d odebral wartosc: %f\n", aktualny, odebrana_wartosc);
    }

    float aktualna_wartosc = 4 * powf(-1, aktualny) / (2.0f * (aktualny+1) -1 );
    aktualna_wartosc += odebrana_wartosc;

    if(aktualny != wszystkie-1)
    {
        int adresat = aktualny + 1;
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
