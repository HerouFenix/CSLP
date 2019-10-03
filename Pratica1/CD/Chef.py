# @package CD
# coding: utf-8

import time
import pickle
import socket
import random
import logging
import configparser
import threading
import uuid
from RingNode import RingNode
from utils import work
from queue import Queue


# configure the log with INFO level
logging.basicConfig(level=logging.INFO,
                    format='%(asctime)s %(name)-12s %(levelname)-8s %(message)s',
                    datefmt='%m-%d %H:%M:%S')

# get configuration file values with work times for each equipment
config = configparser.ConfigParser()
config.read("conf.ini")


## Documentation for Chief Class.
#    Chief Class, responsible for:
#        Receive requests from a Receptionist Object,
#        Cook whatever is necessary,
#        Resend the request to a Waiter Object
class Chef(threading.Thread):
    ##  Constructor 
    #    Starts with pre-set values, the right ones for the current project.
    #        Number of Entity: Simply for identification purposes, in the case there are more than one CHEF
    #        Port: Port number from which it can communicate to the other objects
    #        id: Needed to construct the token ring, to be able to get the right order in the circle
    #        Name: General name of object
    #        Timeout: Time until it gives up on receiving a message
    #        Ring: Port where the initial coordinator is
    def __init__(self, nOfEntity=0, port=5002, id=2, name="CHEF", timeout=3, TG=0, ring=5000, ringSize=4, EG=0, blackList=[]):
    
        threading.Thread.__init__(self)

        if nOfEntity == 0:
            loggerName = name
        else:
            loggerName = name+"-"+str(nOfEntity)
        ## A logger to print to terminal throughout the execution of the program
        self.logger = logging.getLogger(loggerName)

        ## A special socket for receiving clients' requests
        self.client_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.client_socket.settimeout(timeout)
        self.client_socket.bind(('localhost', port-50))

        ## communication thread
        self.comm_chef = RingNode(loggerName, id, ('localhost', port), name, timeout, TG, (
            'localhost', ring), ringSize, EG, blackList)  

        ## Communication port
        self.port = port
        ## Communication timeout
        self.timeout = timeout

        ## Control variables for equipments (0-doesnt have it ; 1-has it/doesnt need it)
        self.requestList = []

        ## Queue that stores the orders to chef
        self.ordersQ = Queue()
        ## Dictionary that stores the orders from clients to make the meals
        self.currentOrder = {'client': None, 'orderID': None,
                             'order': None, 'grill': 0, 'frier': 0, 'drinks': 0}

    ## Receive function
    #
    # Simple receive function, it will listen to the socket created in the Constructor until it receives a message
    def recv(self):
        try:
            p, addr = self.client_socket.recvfrom(1024)
        except socket.timeout:
            return None, None
        else:
            if len(p) == 0:
                return None, addr
            else:
                return p, addr

    ## Send function
    #
    # Using the socket previously created, it will send whatever Object o is passed through the argument
    # and send it to the Address given
    def send(self, address, o):
        p = pickle.dumps(o)
        self.client_socket.sendto(p, address)

    ## Run thread Function
    #
    # Run function to start thread, will simply initialize the RingNode object created in the Constructor
    # And start its own work as Chef
    def run(self):
        self.logger.info("CREATING CHEF")
        self.comm_chef.start()
        self.logger.debug("CREATED CHEF SUCCESSFULLY")
        self.logger.debug("#Threads: %s", threading.active_count())
        self.chef_work(self.comm_chef, self.port, self.timeout)

    ## Chef's Work Function
        
    #    It will be waiting to receive messages from its RingNode object
    #    It may receive four different types of messages:
    #        COOK: Means it has received an order, for instance, 1 Burger, 1 Fries, 1 Drink,
    #            it will add these to the Work Queue 
    #       Grill_token, Fry_token, Drink_token: These three messages mean the cook has authorization to use the Grill, the Fry, or the Drinks machine
    #    These authorizations are important, as it means the cook can work on the next object from its queue
    def chef_work(self, comm, port, timeout):
        
        # get discovery table
        ## Discovery table with all the other works' ID
        self.discovery_table = comm.get_ringIDs()
        while self.discovery_table == None:
            self.discovery_table = comm.get_ringIDs()
            work(0.5)
        self.logger.info("Discovery Table from Comm thread: %s",
                         self.discovery_table)

        done = False
        while not done:
            request = comm.get_in_queue()

            if request is not None:
                if request['method'] == 'COOK':
                    order = {'client': request['args']['CLIENT_ADDR'], 'orderID': request['args']['TICKET'], 'order': request['args']['order'],
                             'grill': 1, 'drinks': 1, 'frier': 1}
                    order['client'] = request['args']['CLIENT_ADDR']
                    order['orderID'] = request['args']['TICKET']
                    order['order'] = request['args']['order']

                    # Check which equipments we'll need (f.ex if we need 3 hamburgers, well store grill = -2 and then increment it by 1 each time we get the grill, noting that well be done when grill = 1)
                    for i in request['args']['order']:
                        if i == 'hamburger':
                            order['grill'] -= request['args']['order']['hamburger']

                        if i == 'drinks':
                            order['drinks'] -= request['args']['order']['drinks']

                        if i == 'fries':
                            order['frier'] -= request['args']['order']['fries']

                    # Add to queue
                    self.ordersQ.put(order)
                    self.logger.info("Order added to Queue : %d - %d - %d",
                                     order['grill'], order['frier'], order['drinks'])

                elif request['method'] == 'GRILL_TOKEN':
                    self.logger.debug("GRILL ACQUIRED")

                    if "grill" in self.requestList:

                        # Grill
                        delta = random.gauss(int(config['GRILL']['MEAN']), float(
                            config['GRILL']['STD_DEVIATION']))
                        self.logger.info('Grilling for %f seconds', delta)
                        work(delta)

                        self.requestList.remove('grill')
                        self.currentOrder['grill'] += 1

                    self.logger.debug("GRILL RELEASED")
                    comm.put_out_queue(request)

                elif request['method'] == 'FRIER_TOKEN':
                    self.logger.debug("FRIER ACQUIRED")

                    if "frier" in self.requestList:

                        # Fry
                        delta = random.gauss(int(config['FRYER']['MEAN']), float(
                            config['FRYER']['STD_DEVIATION']))
                        self.logger.info('Frying for %f seconds', delta)
                        work(delta)
                        self.requestList.remove('frier')
                        self.currentOrder['frier'] += 1

                    self.logger.debug("FRIER RELEASED")
                    comm.put_out_queue(request)

                elif request['method'] == 'DRINKS_TOKEN':
                    self.logger.debug("DRINKS ACQUIRED")
                    if "drinks" in self.requestList:

                        # Grill
                        delta = random.gauss(int(config['DRINKS_BAR']['MEAN']), float(
                            config['DRINKS_BAR']['STD_DEVIATION']))
                        self.logger.info('Pouring for %f seconds', delta)
                        work(delta)

                        self.requestList.remove('drinks')
                        self.currentOrder['drinks'] += 1

                    self.logger.debug("DRINKS RELEASED")
                    comm.put_out_queue(request)

            else:
                # If we're not cooking anything right now
                if self.currentOrder['client'] == None:
                    if self.ordersQ.qsize() == 0:  # If we have no orders continue the loop
                        continue
                    else:
                        self.currentOrder = self.ordersQ.get()  # Get cook order from queue
                        self.logger.info(
                            "Working on order %s", self.currentOrder)

                # If order is ready
                if self.currentOrder['grill'] == 1 and self.currentOrder['frier'] == 1 and self.currentOrder['drinks'] == 1:
                    msg = {'method': 'TOKEN', 'args': {'method': 'DELIVER', 'args': {
                        'id': self.discovery_table['WAITER'], 'order': self.currentOrder['order'],
                        'CLIENT_ADDR': self.currentOrder['client'], 'TICKET': self.currentOrder['orderID']}}}

                    self.logger.info("ORDER READY - %s",self.currentOrder['client'])
                    self.currentOrder = {'client': None, 'orderID': None, 'order': None,
                                         'grill': 0, 'frier': 0, 'drinks': 0}  # Reset current order
                    comm.put_out_queue(msg)

                elif len(self.requestList) == 0:  # Register and Request equipments necessary
                    self.requestList = []

                    # Check which equipments we need
                    if self.currentOrder['grill'] != 1:
                        for i in range(1-self.currentOrder['grill']):
                            self.requestList.append('grill')

                    if self.currentOrder['drinks'] != 1:
                        for i in range(1-self.currentOrder['drinks']):
                            self.requestList.append('drinks')

                    if self.currentOrder['frier'] != 1:
                        for i in range(1-self.currentOrder['frier']):
                            self.requestList.append('frier')
