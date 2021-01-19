def metodaProstokatow(poczatek, koniec, iloscPodzialow, f):
  assert koniec >= poczatek
  assert iloscPodzialow > 0

  dx = (koniec - poczatek) / iloscPodzialow

  wynik = 0
  for i in range(1, iloscPodzialow+1):
    wynik += f(poczatek + i*dx)

  wynik *= dx
  return wynik

import pytest
from math import sin, pi

assert metodaProstokatow(1, 3, 1000, lambda x: x) == pytest.approx(4, 0.01)
assert metodaProstokatow(0, pi, 1000, lambda x: sin(x)) == pytest.approx(2, 0.01)

def metodaTrapezow(poczatek, koniec, iloscPodzialow, f):
  assert koniec > poczatek
  assert iloscPodzialow > 0

  dx = (koniec - poczatek) / iloscPodzialow

  wynik = (f(poczatek) + f(koniec)) / 2
  for i in range(1, iloscPodzialow):
    wynik += f(poczatek + i*dx)

  wynik *= dx
  return wynik

import pytest
from math import sin, pi

assert metodaTrapezow(1, 3, 1000, lambda x: x) == pytest.approx(4, 0.01)
assert metodaTrapezow(0, pi, 1000, sin) == pytest.approx(2, 0.01)

def cpu():
  with tf.device('/cpu:0'):
    metodaProstokatow(0, pi, 10_000, sin)
    metodaTrapezow(0, pi, 10_000, sin)

def gpu():
  with tf.device('/device:GPU:0'):
    metodaProstokatow(0, pi, 10_000, sin)
    metodaTrapezow(0, pi, 10_000, sin)

import timeit
import tensorflow as tf

cpu()
gpu()

print('CPU (s):')
cpu_time = timeit.timeit('cpu()', number=1000, setup="from __main__ import cpu")
print(cpu_time)
print('GPU (s):')
gpu_time = timeit.timeit('gpu()', number=1000, setup="from __main__ import gpu")
print(gpu_time)
print('GPU jest szybsze niż CPU: {}x'.format(int(cpu_time/gpu_time)))
