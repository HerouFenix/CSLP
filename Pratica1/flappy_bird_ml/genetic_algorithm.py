import math
import random
import numpy as np

from training import IntelligentBird
from utils import get_index_of_value_interval_vector


def crossover(brains, nn_size, window_width, window_height, mutation_rate):
	all_brains_puncts = [math.pow(2, b.punctuation + 1) for b in brains]
	sum_all_puncts = sum(all_brains_puncts)
	normalized_puncts = [p/sum_all_puncts for p in all_brains_puncts]

	probabilities = np.cumsum(normalized_puncts)

	new_brains = []
	for i in range(len(all_brains_puncts)):
		parents = [
			brains[get_index_of_value_interval_vector(probabilities, random.random())]
			for i in range(2)
		]
		new_brains.append(IntelligentBird(nn_size, window_width, window_height))

		new_connection_col = []
		for p in range(nn_size):
			if random.random() > mutation_rate:
				new_connection_col.append(
					parents[int(round(random.random()))].first_connection_col[p]
				)
			else:
				new_connection_col.append([random.random()])
		new_brains[i].set_connection_col(new_connection_col)

	return new_brains
