import time
import random

REST = 0
RECEP = 1
COOK = 2
EMP = 3


def contain_successor(id, succ, candidate):
    if id < candidate < succ:
        return True
    if succ < id and candidate > id:
        return True
    return False


def work(seconds=2, sigma=.5):
    delta = 0
    while delta <= 0:
        delta = random.gauss(seconds, sigma)
    time.sleep(delta)