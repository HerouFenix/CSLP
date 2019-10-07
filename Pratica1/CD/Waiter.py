## @package CD
# coding: utf-8

import logging
from Entity import Entity
import threading
from utils import EMP, work


## Waiter Class
# The waiter is the last one to communicate in a simulation
# He receives the orders from the chef, and will forward them to the respective clients
class Waiter(threading.Thread):
    ## Constructor Function
    # Has all the necessary arguments with a default value
    # This includes the communication port, the special ID and the default port to enter the Token Ring
    def __init__(self, port=5003, id=EMP, init_port=5000):
        threading.Thread.__init__(self)
        ##Communication node
        self.node_entity = Entity(id, ('localhost', port), 'Waiter', ('localhost', init_port))
        self.node_entity.start()
        ##Logger to write information on console throughout simulation
        self.logger = logging.getLogger("Waiter")
        ##List of clients currently waiting for orders
        self.tickets = {}
        ##List of requests that have yet to be requested from the clients
        self.completed_req = {}

    ## Pickup function
    # When the client shows up to get food, two things may happen:
    # Either it's already completed, so the waiter can just send the request to the client
    # It's not completed yet, so client will simply be noted on the list of tickets
    def pickup(self, client, ticket):
        if ticket in self.completed_req:
            self.node_entity.send(client, {'args': self.completed_req[ticket]})
        else:
            self.tickets[ticket] = client

    ## Send request function
    # When the chef delivers the completed request to the waiter:
    # If the client has already arrived to get his food, it will be sent straight away
    # If not, then it will just be added to the list of completed requests
    def send_req(self, ticket, req):
        self.logger.info("Send request to %s", ticket)
        if ticket in self.tickets:
            self.node_entity.send(self.tickets[ticket], {'args': req})
        else:
            self.completed_req[ticket] = req

    ## Run function
    # Fairly simple function, it can only receive two orders:
    # A pick up from a client, that will call the pick up function
    # A completed request from a chef, that will call the send request function
    def run(self):

        while True:
            o = self.node_entity.queue_out.get()

            self.logger.info("Waiter: %s", o)

            if o['method'] == 'PICKUP':
                work()
                self.pickup(o['client_addr'], o['args'])

            if o['method'] == 'COMPLETED_REQ':
                work()
                self.send_req(o['ticket'], o['args'])

