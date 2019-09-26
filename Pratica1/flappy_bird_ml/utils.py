import math


def sigmoid(x):
	return 1 / (1 + math.pow(math.e, 2*x))


def get_index_of_value_interval_vector(vector, value):
	if value < vector[0]:
		return 0
	for i in range(len(vector) + 1):
		if value < vector[i + 1] and value > vector[i]:
			return i + 1
	return len(vector) - 1
