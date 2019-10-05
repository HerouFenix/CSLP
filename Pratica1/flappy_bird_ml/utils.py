import math


def sigmoid(x):
	"""
	Function computes the sigmoid function of a given x
	"""
	return 1 / (1 + math.pow(math.e, 2*x))


def get_index_of_value_interval_vector(vector, value):
	"""
	Function returns the index of the smallest closest value to a given number inside a vector
	"""
	if value < vector[0]:
		return 0
	for i in range(len(vector) + 1):
		if value < vector[i + 1] and value > vector[i]:
			return i + 1
	return len(vector) - 1
