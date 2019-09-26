import pygame
import random
import time

from training import IntelligentBird
from genetic_algorithm import crossover


POPULATION_SIZE = 500
MUTATION_RATE = 0.1


class Pipe:
	def __init__(self, window_height, window_width):
		self.x = window_width  # Current Position

		self.windowHeight = window_height
		self.windowWidth = window_width

		self.width = 70  # Pipe Width
		self.speed = 3.5  # Speed at which pipe moves

		self.color = (48, 142, 1)

		self.passed = False  # Flag to check wheter we've passed the bird or not

		self.gap = 50  # Gap between top and bottom part of pipe

		self.start = random.randint(0, window_height)  # Get a center value for the center of the gap between the pipes
		while self.start < 60 or self.start > self.windowHeight - 60:  # Change these values to alter the min/max height of the pipes
			self.start = random.randint(0, window_height)

		self.top = self.start - self.gap
		self.bottom = (self.start + self.gap) - window_height

	# Check collision with the player
	def checkCollision(self, bird):
		if bird.position[1] - 15 <= self.top \
				or bird.position[1] + 15 >= self.bottom + self.windowHeight:  # Check if bird is in the same height as either the top or bottom of the pipe
			if bird.position[0] + 15 > self.x and bird.position[
				0] - 15 < self.x + self.width:  # Check if the bird is touching either the leftmost part or interior of the pipe
				return True

	# Update the pipe position
	def update(self, bird):
		if self.passed is False and self.checkCollision(bird):
			return True

		return False

	# Draw both bottom and top parts of the pipe
	def draw(self, surface):
		self.x = self.x - int(self.speed)
		# Bottom
		pygame.draw.rect(surface, self.color, (self.x, self.windowHeight, self.width, self.bottom))
		# Top
		pygame.draw.rect(surface, self.color, (self.x, 0, self.width, self.top))


class Bird:
	def __init__(self, size, window_height, window_width, brain):
		self.size = size  # Bird size

		self.color = (239, 177, 49)

		self.position = [window_width / 6, window_height / 2]  # Position of the center of the bird

		self.maxHeight = window_height  # Window Height

		self.gravity = 0.5  # Velocity increases overtime
		self.lift = -10  # How high we jump
		self.velocity = 0  # Used to change the position

		self.brain = brain

	def handleKeys(self):
		self.velocity = self.velocity + self.lift

	def update(self):
		self.velocity = self.velocity + self.gravity
		self.velocity = self.velocity * 0.91  # Makes it so velocity increases incrementally
		self.position[1] = self.position[1] + self.velocity

		if self.position[1] >= self.maxHeight:
			self.position[1] = self.maxHeight

		if self.position[1] <= 0:
			self.position[1] = 0


	def draw(self, surface):
		pygame.draw.circle(surface, self.color, (int(self.position[0]), int(self.position[1])), self.size)


def main():
	nn_size = 3
	generation_number = 1

	clock = pygame.time.Clock()

	pygame.init()
	screen = pygame.display.set_mode((500, 500))
	done = False

	# Create player
	players = [
		Bird(
			15,
			screen.get_width(),
			screen.get_height(),
			IntelligentBird(nn_size, screen.get_width(), screen.get_height())
		) for i in range(POPULATION_SIZE)
	]
	dead_players = []

	# Create pipes array and add first pipe
	pipes = [Pipe(screen.get_width(), screen.get_height())]

	score = 0
	delta_time = time.time()
	time_bt_pipes = 2.7  # 1 second

	while not done:
		screen.fill((76, 188, 252))

		# Player
		for p in players:
			p.draw(screen)

		for event in pygame.event.get():
			if event.type == pygame.QUIT:
				done = True

		for p in players:
			p.update()

		# Pipes
		if time.time() - delta_time >= time_bt_pipes:
			delta_time = time.time()
			pipes.append(Pipe(screen.get_width(), screen.get_height()))

		# TODO -> isto pode ser melhorado
		for pipe in pipes:
			pipe.draw(screen)

			# verify which players are already dead
			for p in players:
				if pipe.update(p):
					dead_players.append(p)
					p.brain.set_punctuation(score)
					players.remove(p)
			dead_players = list(set(dead_players))

			# Check if pipe is off the screen or if it has passed the bird
			if pipe.x + pipe.width < 0:
				pipes.remove(pipe)
			elif pipe.passed is False and pipe.x + pipe.width < screen.get_width() / 6 - 15:
				score = score + 1
				pipe.passed = True

		font = pygame.font.Font(None, 36)

		text = font.render(str(score), 1, (255, 255, 255))
		text_pos = text.get_rect(centerx = screen.get_width() / 2)
		screen.blit(text, text_pos)

		text = font.render(str(generation_number), 1, (255, 255, 255))
		text_gen = text.get_rect(centerx = screen.get_width() - 10)
		screen.blit(text, text_gen)

		# implementation of ml
		for player in players:
			horizontal_distance = pipes[0].x - player.position[0]
			vertical_distance = pipes[0].top - player.position[1]

			if player.brain.decision(horizontal_distance, vertical_distance, player.position[1]):
				player.handleKeys()

		if len(dead_players) == POPULATION_SIZE:
			new_brains = crossover(
				[dp.brain for dp in dead_players],
				3,
				screen.get_width(),
				screen.get_height(),
				MUTATION_RATE
			)
			players = [
				Bird(
					15,
					screen.get_width(),
					screen.get_height(),
					nb
				) for nb in new_brains
			]

			generation_number += 1

			# TODO -> código repetido
			dead_players = []
			pipes = [Pipe(screen.get_width(), screen.get_height())]

			score = 0
			delta_time = time.time()

		pygame.display.update()
		clock.tick(60)


main()
