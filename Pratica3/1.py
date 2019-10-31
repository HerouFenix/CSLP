import sys

def main(filename):
    sum = 0
    counter = 0
    with open(filename,"r") as reader:
        for line in reader:
            counter+=1
            sum += (float)(line)

    print(sum/counter)

if __name__ == "__main__":
    filename = sys.argv[1]
    main(filename)