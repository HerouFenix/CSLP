# @package CD
# coding: utf-8

import pickle
import socket
import random
import logging
import configparser
import threading
import uuid
from RingNode import RingNode
from utils import work

# configure the log with INFO level
logging.basicConfig(level=logging.INFO,
                    format='%(asctime)s %(name)-12s %(levelname)-8s %(message)s',
                    datefmt='%m-%d %H:%M:%S')

# get configuration file values with work times for each equipment
config = configparser.ConfigParser()
config.read("conf.ini")

## Documentation for Clerk Class
#   O Rececionista é quem recebe os clientes
#   Ao receber um pedido do cliente, o rececionista deve entregar um Ticket, feito pela biblioteca uuid
#   Isto fará com que seja possivel, ao longo da simulacao, relacionar o pedido do cliente com o cliente em si 
class Clerk(threading.Thread):
    ##  Constructor 
    #    Starts with pre-set values, the right ones for the current project.
    #        Number of Entity: Simply for identification purposes, in the case there are more than one CHEF
    #        Port: Port number from which it can communicate to the other objects
    #        id: Needed to construct the token ring, to be able to get the right order in the circle
    #        Name: General name of object
    #        Timeout: Time until it gives up on receiving a message
    #        Ring: Port where the initial coordinator is
    def __init__(self, nOfEntity=0, port=5001, id=1, name="CLERK", timeout=3, TG=0, ring=5000, ringSize=4):
        threading.Thread.__init__(self)

        if nOfEntity == 0:
            loggerName = name
        else:
            loggerName = name+"-"+str(nOfEntity)
        ## Logger to print out the information throughout the execution of the programme
        self.logger = logging.getLogger(loggerName)

        # Creating special socket for receiving clients' requests
        self.client_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.client_socket.settimeout(timeout)
        self.client_socket.bind(('localhost', port-50))

        ## Communication thread
        self.comm_clerk = RingNode(loggerName, id, ('localhost', port), name,
                                   timeout, TG, ('localhost', ring), ringSize) 

        ## Communication port
        self.port = port
        ## Communication timeout
        self.timeout = timeout
        ##
        self.count = 0

    ## Receive function
    # Simple communication function that receives messages from outside
    # Returns pickle message and incoming address
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
    # Sends a message to the outside
    # Needs an object to pickle and an address
    def send(self, address, o):
        p = pickle.dumps(o)
        self.client_socket.sendto(p, address)

    ## Run function
    # Starts the communication node and its own work function
    def run(self):
        self.logger.info("CREATING CLERK")
        self.comm_clerk.start()
        self.logger.debug("CREATED CLERK SUCCESSFULLY")
        self.logger.debug("#Threads: %s", threading.active_count())
        self.clk_work(self.comm_clerk, self.port, self.timeout)

    ## Work function
    # Starts by filling out the discovery table
    # Once he gets a request from a client, he will forward it to the cook(s)
    def clk_work(self, comm, port, timeout):

        # get discovery table
        self.discovery_table = comm.get_ringIDs()
        while self.discovery_table == None:
            self.discovery_table = comm.get_ringIDs()
            work(0.5)
        self.logger.info("Discovery Table from Comm thread: %s",
                         self.discovery_table)

        while True:
            request = comm.get_in_queue()
            if request is not None:
                self.logger.info("Request from queue: %s", request)

                # Wait for a random time
                delta = random.gauss(int(config['ACTION']['MEAN']), float(
                    config['ACTION']['STD_DEVIATION']))
                self.logger.info('Wait for %f seconds', delta)
                work(delta)

                client_addr = request['args']['CLIENT_ADDR']
                order_id = uuid.uuid1()
                msg = {'method': 'ORDER_RECEIVED', 'args': order_id}
                self.send(client_addr, msg)  # send ticket to client
                if isinstance(self.discovery_table['CHEF'], list):
                    msg = {'method': 'TOKEN', 'args': {'method': 'COOK', 'args': {
                        'id': self.discovery_table['CHEF'][self.count], 'order': request['args']['order'],
                        'CLIENT_ADDR': request['args']['CLIENT_ADDR'], 'TICKET': order_id}}}
                    self.count = (
                        self.count+1) % len(self.discovery_table['CHEF'])
                else:
                    msg = {'method': 'TOKEN', 'args': {'method': 'COOK', 'args': {
                        'id': self.discovery_table['CHEF'], 'order': request['args']['order'],
                        'CLIENT_ADDR': request['args']['CLIENT_ADDR'], 'TICKET': order_id}}}
                self.logger.debug(msg)
                comm.put_out_queue(msg)
