## @package CD
# coding: utf-8

import logging
from Entity import Entity
import threading
from utils import COOK, work
from queue import Queue

## Chef Class
#   Responsible for receiving incoming requests from Receptionist
#   Cook whatever is necessary
#   Send the prepared order to the waiter
class Chef(threading.Thread):
    ## Constructor Function
    # Has all the necessary arguments with a default value
    # This includes the communication port, the special ID and the default port to enter the Token Ring
    def __init__(self, port=5002, ide=COOK, init_port=5000):
        threading.Thread.__init__(self)
        ## Node in the Ring that will establish the communication
        self.node_entity = Entity(ide, ('localhost', port), 'Chef', ('localhost', init_port))
        self.node_entity.start()
        ##Logger to print out information throughout the simulation
        self.logger = logging.getLogger("Chef")
        ##List of requests received
        self.req = []
        ##List of foods to prepare
        self.to_do = []

    ##Completed Function
    # Check if the cook has finished the first request in line
    ## @param req: a record of all the different foods currently done
    def completed(self, req):
        if len(self.req) <= 0:
            return False
        temp = self.req[0][1]
        for food in req:
            if req[food] < temp[food]:
                return False
        return True

    ##Add to_do Function
    # Add a list of foods to the to_do list of the cook, for him to prepare
    ## @param args: a dictionary with what food to make and in what quantities
    def add_to_do(self, args):
        for key, val in args.items():
            for i in range(val):
                self.to_do.append(key)
        return self.to_do[0]
        
    ## Run Function
    # Starts the thread run:
    # When a requests comes from the Receptionist, it will add it to the request's list and the to_do list;
    # Once it has items on the to_do list, it will start confectionating these items, having to ask
    # for permission to use the needed cookingware from the Restaurant
    # To simulate the cooking process, the Chef has a sleep function builtin to it
    # After it finishes cooking anything, it will check if the first order has been completed: if so, it will be sent to the waiter, if not it will simply move on
    # It will also send a request for the next food item on the to_do list, to check if the cookingware is available
    def run(self):
        food_done = dict.fromkeys(['hamburger', 'fries', 'drink'], 0)
        cooking_item = False
        item_index = 0

        while True:
            o = self.node_entity.queue_out.get()
            self.logger.info("O: %s", o)

            if o['method'] == 'SEND_ORDER':
                self.req.append((o['ticket'], o['args']))
                self.add_to_do(o['args'])

            if o['method'] == 'USE_FRIES':
                if not o['args']:
                    self.node_entity.queue_in.put({'entity': 'Restaurant','args': None, 'method': 'USING_FRY'})
                    work(5, 0.5)
                    self.to_do.pop(item_index)
                    item_index = 0
                    food_done['fries'] += 1
                    self.node_entity.queue_in.put({'entity': 'Restaurant','args': None, 'method': 'STOPPED_FRY'})
                else:
                    item_index+=1
                cooking_item = False
            
            if o['method'] == 'USE_GRILL':
                if not o['args']:
                    self.node_entity.queue_in.put({'entity': 'Restaurant','args': None, 'method': 'USING_GRILL'})
                    work(3, 0.5)
                    self.to_do.pop(item_index)
                    item_index = 0
                    food_done['hamburger'] += 1
                    self.node_entity.queue_in.put({'entity': 'Restaurant','args': None, 'method': 'STOPPED_GRILL'})
                else:
                    item_index+=1
                cooking_item = False
            if o['method'] == 'USE_DRINK':
                if not o['args']:
                    self.node_entity.queue_in.put({'entity': 'Restaurant','args': None, 'method': 'USING_DRINK'})
                    work(1, 0.5)
                    self.to_do.pop(item_index)
                    item_index = 0
                    food_done['drink'] += 1
                    self.node_entity.queue_in.put({'entity': 'Restaurant','args': None, 'method': 'STOPPED_DRINK'})
                else:
                    item_index+=1
                cooking_item = False

            if self.completed(food_done):
                ticket, completed_req = self.req.pop()
                for key in food_done:
                    food_done[key] -= completed_req[key]
                self.node_entity.queue_in.put({'entity': 'Waiter', 'method': 'COMPLETED_REQ', 'args': completed_req, 'ticket': ticket})

            if not cooking_item:
                if len(self.to_do) > 0:
                    item = self.to_do[item_index]
                    cooking_item = True
                    self.node_entity.queue_in.put({'entity': 'Restaurant', 'args': None, 'method': 'ask_'+item})

