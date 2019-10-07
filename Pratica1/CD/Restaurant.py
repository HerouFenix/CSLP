## @package CD
# coding: utf-8

import logging
from Entity import Entity
import threading
from utils import REST, work

## Restaurant Class
# The restaurant class must be the first one to be initialized
# While the order of the IDs itself doesn't matter, the default values for each classes is designed to communicate to the Restaurant's port
# So in order to change the order for whatever reason, you must change all the default values in every class so that it can remain in accordance
class Restaurant(threading.Thread):
    ##Constructor function
    # Has all the necessary arguments with a default value
    # This includes the communication port and the special ID
    def __init__(self, port=5000, ide=REST):
        threading.Thread.__init__(self)
        ## Communication node
        self.node_entity = Entity(ide, ('localhost', port), "Restaurant")
        self.node_entity.start()
        ## Logger to write on console
        self.logger = logging.getLogger("Restaurant")
        ## Boolean variable to check if grill is being used
        self.used_grill = False
        ## Boolean variable to check if frier is being used
        self.used_fry = False
        ## Boolean variable to check if drink machine is being used
        self.used_drink = False

    ## Run function
    # Uses mostly the boolean variables defined in the constructor
    # If a request from a chef comes to use a certain equipment, the restaurant will send it the corresponding variable
    # If a request comes saying the chef will use the equipment, the restaurant will change the corresponding variable to True
    # Once it's done cooking, and the chef sends a request saying he has stopped using it, the restaurant will change the variable to False
    def run(self):

    	while True:
            o = self.node_entity.queue_out.get()
            self.logger.info("O: %s", o)

            if o['method'] == 'ask_hamburger':
                self.node_entity.queue_in.put({'entity': 'Chef', 'method': 'USE_GRILL', 'args': self.used_grill})

            if o['method'] == 'ask_fries':
                self.node_entity.queue_in.put({'entity': 'Chef', 'method': 'USE_FRIES', 'args': self.used_fry})

            if o['method'] == 'ask_drink':
                self.node_entity.queue_in.put({'entity': 'Chef', 'method': 'USE_DRINK', 'args': self.used_drink})

            if o['method'] == 'USING_GRILL':
                self.used_grill = True

            if o['method'] == 'STOPPED_GRILL':
                self.used_grill = False

            if o['method'] == 'USING_FRY':
                self.used_fry = True

            if o['method'] == 'STOPPED_FRY':
                self.used_fry = False

            if o['method'] == 'USING_DRINK':
                self.used_drink = True

            if o['method'] == 'STOPPED_DRINK':
                self.used_drink = False
