## @package CD
# coding: utf-8

import socket
import threading
import logging
import pickle
from utils import contain_successor
from queue import Queue

## Entity Class
# Made for communication between the Actors in the simulation
# To have a distinction between the entites in a ring, an ID and a Name are needed. These identification is needed
# to specify who an entity wants to send a message to
# To build the ring itself, each entity will have a successor, based on the ID. This ring is done in such a way that the successor's ID will be greather than it's own
# Unless, it has the biggest ID in the ring, so it's successor will be the smallest ID in the ring.
# Finally, in order for the objects to unite in a ring, it's needed to introduce one address of someone already inside the ring. If none is give, it's assumed that the entity wants to start the ring, making one with itself and having itself as a successor.
class Entity(threading.Thread):
    ## Constructor
    # Has some default values, i.e. the port of a node in a ring (in_ring), the ring's size, and the timeout for the messages
    # Other paramenters include the name and ID, as said before, to identify the entity itself
    # And the address, for communication purposes
    def __init__(self, id, addr, name, in_ring=None, size=4, timeout=3):
        threading.Thread.__init__(self)
        self.id = id
        self.addr = addr
        self.name = name
        ## The successor's identification and address
        self.successor = None
        self.succ_addr = None
        ## Whether or not it's inside the ring
        self.inside_ring = False
        self.in_ring = in_ring
        ## Identification Table, to corresponde each entity's identification to its address
        self.table = None
        ## Boolean variable to check if the ring is complete
        self.complete_ring = False
        ## Queue in, with messages from the entity to circulate inside the ring
        self.queue_in = Queue()
        ## Queue out, with messages from the ring that are meant for the object's corresponding Actor
        self.queue_out = Queue()
        self.size = size
        if in_ring is None:
            self.successor = id
            self.succ_addr = addr
            self.inside_ring = True
            self.in_ring = self.addr
        ##Socket for communication
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.socket.settimeout(timeout)
        ##Logger to print out information to the console
        self.logger = logging.getLogger("Node {}".format(self.id))

    ## Send function
    # Simple send function that will pickle an object and send it through the socket
    ## @param address: address to whom the message is sent
    ## @param o: object to be pickled and sent
    def send(self, address, o):
        p = pickle.dumps(o)
        self.socket.sendto(p, address)

    ##Receive function
    # Simple receive functoin that will receive a message from the socket and return the message and address
    def recv(self):
        try:
            p, addr = self.socket.recvfrom(1024)
        except socket.timeout:
            return None, None
        else:
            if len(p) == 0:
                return None, addr
            else:
                return p, addr

    ## Connect function
    # Function that allows for an entity to join the ring
    # It needs the entity's information: its id and address
    # There are three alternatives to be explored:
    #   If there's only one Entity on the ring, in which case the candidate will join without further complications
    #   If the candidate entity's id is between the receiver's ID and its successor's, in which case the successor's ID will be sent to the candidate entity, and the receiver's successor will change to candidate's
    #   If none of these apply, it message will just be forward to the successor
    def connect(self, args):
        candidate_id = args['id']
        candidate_addr = args['addr']
        if self.id == self.successor:
            self.send(candidate_addr, {'method': 'CONN_AUTH', 'args': {'id': self.successor, 'addr': self.succ_addr}})
            self.successor = candidate_id
            self.succ_addr = candidate_addr

        elif contain_successor(self.id, self.successor, candidate_id):
            self.send(candidate_addr, {'method': 'CONN_AUTH', 'args': {'id': self.successor, 'addr': self.succ_addr}})
            self.successor = candidate_id
            self.succ_addr = candidate_addr
        else:
            self.send(self.succ_addr, {'method': 'CONNECT', 'args': {'id': candidate_id, 'addr': candidate_addr}})

    ## Broadcast Function
    # Simple function that uses the ring's size introduced in the constructor to check if the ring is complete
    # Starts just sending a message that will go to all entities present in the ring;
    # The one who started the message will use its ID as the Final ID, i.e. the ending condition
    # The message will circulate in the ring, each incrementing a counter within the message by 1
    # Once it has reached the entity from which the message was sent, the count will be checked to see if it matches the ring_size
    # If so, the start token function will be called
    # This function also serves the purpose of finding who must start sending the token, in this implementation the one with the lowest ID will be picked
    ## @param args: arguments with the fields 'method' and 'args', which will be the function that needs to be called next, followed by that function's args
    def broadcast(self, args):
        if self.id == args['final_id']:
            if args['count'] == self.size:
                self.send(self.succ_addr, {'method': 'START_TOKEN', 'args': args['lowest_id']})
        else:
            count = args['count']
            count+=1
            if args['lowest_id'] > self.id:
                args['lowest_id'] = self.id
            self.send(self.succ_addr, {"method": "BROADCAST", 'args': {'final_id': args['final_id'], 'count': count, 'lowest_id': args['lowest_id']}})

    ## Start token function
    # Function that works simply as a warning to all entites inside the ring that a Token will be launched
    # Who sends it is the one defined in the discovery function
    ## @param args: just the args that were sent here, without doing anything
    def start_token(self, args):
        self.send(self.succ_addr, {'method': 'START_TOKEN', 'args': args})
        self.complete_ring = True

    ## Discover function
    # First function to run after the ring is complete
    # Will fill out each entities' discovery table, that connects the name of the entity with its address
    def discovery(self, args):
        if self.name in args and self.table is None:
            self.table = args
            self.send(self.succ_addr, {'TOKEN': 'DISCOVERY', 'args': args})
        elif self.name not in args:
            args[self.name] = self.id
            self.send(self.succ_addr, {'TOKEN': 'DISCOVERY', 'args': args})
        else:
            self.send(self.succ_addr, {'TOKEN': None})
            
    ## Run Function
    # The entity starts by entering the ring, sending a message to the address we have inside ring address until we connect
    # Next it enters a second loop, where it keeps looking out for messages from other entities that want to enter the ring
    # Only when the ring is complete, do we move on to the final, and most extensive, part of the communication: the simulation cycle's
    # In the simulation there will be a token circulation in the ring, to establish the communicatin between entities
    # If the entity receives a Token Message, it will check who it's meant to
    # If that field is empty, it means that the entity can put its own message, if it has one
    # If that field has an ID, but it's not the receiver's ID, then the receiver will simply forward it to the successor
    # If that field has an ID, and it's the receiver's, then the message will be stored in the queue_out, meant for the corresponding actor
    # Now, worth noting that not all messages are from Tokens, they can be from the clients, in which case it will just be put in the receiver's queue_in, slightly modified so that it can be under the regular formatting of the messages in the ring
    def run(self):
        self.socket.bind(self.addr)
        while not self.inside_ring:
            self.send(self.in_ring, {"method": "CONNECT", 'args': {'id': self.id, 'addr': self.addr}})
            p, addr = self.recv()
            if p is not None:
                o = pickle.loads(p)
                if o['method'] == 'CONN_AUTH':
                    self.successor = o['args']['id']
                    self.succ_addr = o['args']['addr']
                    self.inside_ring = True

        self.logger.info("Entity %s created at port %d", self.name, self.addr[1])

        self.send(self.succ_addr, {"method": "BROADCAST", 'args': {'final_id':self.id, 'count': 1, 'lowest_id': self.id}})

        starter = None

        while not self.complete_ring:
            p, addr = self.recv()
            if p is not None:
                o = pickle.loads(p)
                self.logger.info('O: %s', o)
                if o['method'] == 'CONNECT':
                    self.connect(o['args'])

                elif o['method'] == 'BROADCAST':
                    self.broadcast(o['args'])

                elif o['method'] == 'START_TOKEN':
                    starter = o['args'] == self.id
                    self.start_token(o['args'])

        if starter:
            self.send(self.succ_addr, {'TOKEN': 'DISCOVERY', 'args': {self.name: self.id}})

        done = False

        while not done:
            p, addr = self.recv()
            if p is not None:
                o = pickle.loads(p)

                if 'TOKEN' in o:
                    if o['TOKEN'] is None:
                        if not self.queue_in.empty():
                            req = self.queue_in.get()
                            self.logger.info("Request %s", req)
                            o['TOKEN'] = self.table[req.pop('entity')]
                            o['args'] = req
                        self.send(self.succ_addr, o)

                    elif o['TOKEN'] == 'DISCOVERY':
                        self.logger.info('O: %s', o)
                        self.discovery(o['args'])

                    elif o['TOKEN'] != self.id:
                        self.logger.info('O: %s', o)
                        self.send(self.succ_addr, o)
                    else:
                        self.logger.info('O: %s', o)
                        self.queue_out.put(o['args'])
                        o['TOKEN'] = None
                        self.send(self.succ_addr, o)

                else:
                    if o['method'] == 'ORDER':
                        self.queue_in.put({'entity': 'Receptionist', 'method': o['method'], 'args': o['args'], 'client_addr': addr})

                    elif o['method'] == 'PICKUP':
                        self.queue_in.put({'entity': 'Waiter', 'method': o['method'], 'args':o['args'], 'client_addr': addr})
