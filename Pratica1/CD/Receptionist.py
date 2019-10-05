## @package CD
# coding: utf-8

import logging
from Entity import Entity
import threading
from utils import RECEP, work
from uuid import uuid1

## Receptionist Class
# He's who receives the clients
# When it receives a client's order, it will attach to it a Ticket, from the UUID library
# This is so that, by the end of the simulation, it's still possible to relate the order to the respective client
class Receptionist(threading.Thread):
    ## Constructor Function
    # Has all the necessary arguments with a default value
    # This includes the communication port, the special ID and the default port to enter the Token Ring
    def __init__(self, port=5001, ide=RECEP, init_port=5000):
        threading.Thread.__init__(self)
        ## Node entity is who establishes the communication
        self.node_entity = Entity(ide, ('localhost', port), "Receptionist", ('localhost', init_port))
        self.node_entity.start()
        ## Logger to write on console throughout the simulation
        self.logger = logging.getLogger("Rececionista")

    ## Send Order Function
    # The function responsible to attaching the ticket to the function and sending it to the chef
    def send_order(self, client, args):
        ticket = uuid1()
        self.node_entity.queue_in.put({'entity': 'Chef', 'method': 'SEND_ORDER', 'args': args, 'ticket': ticket})
        self.node_entity.send(client, {'args': ticket})

    ## Run Function
    # The receptionist only has one job, so it will only receive one method: order method
    def run(self):
        while True:
            o = self.node_entity.queue_out.get()
            self.logger.info("O %s", o)

            if o['method'] == 'ORDER':
                work()
                self.send_order(o['client_addr'], o['args'])
