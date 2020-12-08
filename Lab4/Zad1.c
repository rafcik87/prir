#include <stdio.h>

float func(float x)
{
    return 4*x-6*x+5;
}

float metodaTrapezow(int a, int b, int n)
{
    float dx = (float)(b-a)/n;
    float wynik = 0;
    for(int i=1; i<n; i++)
    {
        wynik += func(a + dx*i);
    }
    
    wynik += (func(a) + func(b))/2;
    wynik *= dx;
    return wynik;
}

int main()
{
    printf("Podaj liczbę procesów: ");
    int liczba;
    scanf("%d", &liczba);

    for(int i=0; i<liczba; i++)
    {
        if(fork() == 0)
        {
            srand(time(NULL) ^ getpid()<<16);
            int a = rand()%50;
            int b =  a + 1 + rand()%50;
            int n = 50 + rand()%50;
            float wynik = metodaTrapezow(a, b, n);
            printf("Obliczono %f, dla przedzialu od %d do %d, przy %d podzialach\n", wynik, a, b, n);
            exit(0);
        }
    }
}
