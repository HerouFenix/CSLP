import numpy as np
from utils import sigmoid


class IntelligentBird:
	"""
	Intelligent Agent class
	"""
	def __init__(self, size, window_width, window_height):
		self.first_connection_col = np.random.rand(size, 1)

		self.window_width = window_width
		self.window_height = window_height

		# for the genetic algorithm
		self.punctuation = 0

	def set_connection_col(self, vector):
		"""
		Transform a python vector into a numpy one (which corresponds to the weights vector between the input and the output layers)
		"""
		self.first_connection_col = np.array(vector)

	def decision(self, vertical_dist, horizontal_dist, altitude):
		"""
		Executes the Matrix Multiplication between the input layer and the weights vector, and then between this resulting matrix and the output layer (hence creating a new output layer)
		"""
		input = np.array([
			sigmoid(vertical_dist/self.window_height),
			sigmoid(horizontal_dist/self.window_width),
			sigmoid(altitude/self.window_height)
		])

		return np.matmul(input, self.first_connection_col)[0] > 0.5

	def set_punctuation(self, punt):
		"""
		Defines how well this entity played the game (i.e, how well the player was doing at the moment of this function's calling). This is done so that we can let natural selection improve the genetic algorithm in the next generation's players
		"""
		self.punctuation = punt