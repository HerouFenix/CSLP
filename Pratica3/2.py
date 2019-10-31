import sys
import csv

def get_student_list(filename):
    with open(filename) as csv_file:
        csv_reader = csv.reader(csv_file, delimiter='\t')
    
        student_list = {}
        first_line = True
        for row in csv_reader:
            if first_line:
                first_line = False
                continue
            else:
                student_list[row[0]] = row[1:]
        return student_list

def get_average(student_info):
    return((float)(student_info[4]) + (float)(student_info[5]) + (float)(student_info[6])) / 3

def get_all_students_average(students):
    sum = 0
    counter = 0
    for i in students:
        sum += get_average(students[i])
        counter += 1
    
    return sum/counter



def main(filename):
    student_list = get_student_list(filename)
    
    #print(get_all_students_average(student_list))
    actual_list = []
    for key in student_list:
        actual_list.append([key, student_list[key][0], get_average(student_list[key])])

    actual_list = sorted(actual_list, key=lambda student: student[-1],reverse=True) 

    print('{:5}\t{:^40}\t{:5}'.format('Numero',"Nome","Nota"))
    
    print(student_list)
    for i in actual_list:
        print('{:5}\t{:^40}\t{:5.2f}'.format(i[0],i[1],i[2]))


if __name__ == "__main__":
    filename = sys.argv[1]
    main(filename)