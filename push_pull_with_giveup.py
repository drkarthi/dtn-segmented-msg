import sys
from random import randrange
import math
import numpy as np

class node_push_pull_with_giveup:
	def __init__(self,id,message_size):
		self.id = id
		self.src = 0
		self.msg = [0 for x in range(message_size)]
		self.neighbours = []
		self.discovered_neighbours = []
		self.discovered_sources = []
		self.ps_count = 0
		self.pl_count = 0
		self.sleep_time = 0

def read_sample(filename, sample):
	fo = open(filename, "r")
	lines = fo.readlines()
	for item in lines:
		sample.append(int(item))

if __name__=="__main__":
	no_of_nodes = 10879
	no_of_connected_nodes = 10876
	p = 0.9
	wastage = 0
	bandwidth = 0
	time = 0
	successful_pushes = 0
	successful_pulls = 0
	message_size = int(sys.argv[1])
	distribution = sys.argv[2]
	mean = int(sys.argv[3])
	nodes = []
	sources = []
	gaussian_sample = []
	power_law_sample = []
	poisson_sample = []
	read_sample('gaussian_distribution.txt', gaussian_sample)
	read_sample('powerlaw_distribution.txt', power_law_sample)
	read_sample('poisson_distribution.txt', poisson_sample)
	for i in range(no_of_nodes):
		nodes.append(node_push_pull_with_giveup(i, message_size))
	fo = open("gnutella_network.txt", "r")
	lines = fo.readlines()
	for line in lines:
		vertice = line.split("\t")
		nodes[ int(vertice[0]) ].neighbours.append( int(vertice[1]) )
		nodes[ int(vertice[1]) ].neighbours.append( int(vertice[0]) )
	while(True): 															# random source node
		s = randrange(no_of_nodes)
		if( len(nodes[s].neighbours)>0 ):
			break
	for i in range(message_size):
		nodes[s].msg[i] = 1		
	nodes[s].src = 1
	# print "Source = ",s		
	sources.append(s)
	while( len(sources)>0 ):
		# push
		for source in sources:
			#print "source = ",source
			rand_neighbour = nodes[source].neighbours[ randrange(len(nodes[source].neighbours)) ]
			neighbour_node = nodes[rand_neighbour]
			neighbour_id = neighbour_node.id
			#print "random neighbour = ",neighbour_id

			if(neighbour_id not in nodes[source].discovered_neighbours):
				# print "New neighbour discovered"
				nodes[source].discovered_neighbours.append(neighbour_id)
				nodes[neighbour_id].discovered_neighbours.append(source)
				nodes[neighbour_id].discovered_sources.append(source)
				nodes[neighbour_id].pl_count += 1 								# updating pl_count for pull

			if(nodes[neighbour_id].src == 1):
				# print("Neighbour is a source")
				wastage += 1
				nodes[source].ps_count += 1
				if(nodes[source].ps_count > math.log(1/(1-p))*(len(nodes[source].discovered_neighbours)+1) ):
					# print "ps_count = ",nodes[source].ps_count
					# print "threshold = ",math.log(1/(1-p))*(len(nodes[source].discovered_neighbours)+1)
					sources.remove(source)
			# picking the number of packets from the distribution
			elif(nodes[neighbour_id].src == 0):
				no_of_transfer_packets = 0
				while(no_of_transfer_packets < 1):
					if(distribution == "gaussian"):
						rn = randrange(10000)
						no_of_transfer_packets = gaussian_sample[rn] 					
					elif(distribution == "uniform"):
						no_of_transfer_packets = mean
					elif(distribution == "powerlaw"):
						rn = randrange(10000)
						no_of_transfer_packets = power_law_sample[rn]
					elif(distribution == "poisson"):
						rn = randrange(10000)
						no_of_transfer_packets = poisson_sample[rn]	

				pkt = 0
				while(nodes[neighbour_id].msg[pkt] == 1):
					pkt += 1	
				transfer_count = 0
				while(transfer_count < no_of_transfer_packets):	
					nodes[neighbour_id].msg[pkt] = 1
					transfer_count += 1
					bandwidth += 1
					successful_pushes += 1	
					if(pkt == message_size-1):
						nodes[neighbour_id].src = 1
						sources.append(neighbour_id)
						nodes[neighbour_id].sleep_time = 0
						partial_wastage = 1 - transfer_count/float(no_of_transfer_packets)
						wastage += partial_wastage
						break
					pkt += 1
					

		for x in range(no_of_nodes):
			if nodes[x].msg == [1 for k in range(message_size)] and nodes[x].src != 1:
				print "Source status not updated properly for node ",x							

		# pull
		for x in range(no_of_nodes):					
			if( nodes[x].src==0 and nodes[x].sleep_time==0 and len(nodes[x].discovered_neighbours)>0 ):
				# choose a random neighbour
				rn = randrange( len(nodes[x].neighbours) )
				neighbour_node = nodes[ nodes[x].neighbours[rn] ]
				neighbour_id = neighbour_node.id
				if(neighbour_id not in nodes[x].discovered_neighbours):
					nodes[x].discovered_neighbours.append(neighbour_id)
					nodes[neighbour_id].discovered_neighbours.append(x)
				# pull from the neighbour
				if(nodes[neighbour_id].src == 0):
					wastage += 1
					nodes[x].sleep_time = len(nodes[x].discovered_neighbours) - nodes[x].pl_count

				elif(neighbour_node.src == 1): 									# if the neighbour is a source
					if(neighbour_id not in nodes[x].discovered_sources):
						nodes[x].discovered_sources.append(neighbour_id)
						nodes[x].pl_count += 1

					no_of_transfer_packets = 0
					while(no_of_transfer_packets < 1):
						if(distribution == "gaussian"):
							rn = randrange(10000)
							no_of_transfer_packets = gaussian_sample[rn] 					
						elif(distribution == "uniform"):
							no_of_transfer_packets = mean
						elif(distribution == "powerlaw"):
							rn = randrange(10000)
							no_of_transfer_packets = power_law_sample[rn]
						elif(distribution == "poisson"):
							rn = randrange(10000)
							no_of_transfer_packets = poisson_sample[rn]
								
					pkt = 0
					while(nodes[x].msg[pkt] == 1):
						pkt += 1
					transfer_count = 0
					while(transfer_count < no_of_transfer_packets):								
						nodes[x].msg[pkt] = 1 										# packet transmission
						transfer_count += 1
						bandwidth += 1 												# update bandwidth
						successful_pulls += 1
						if(pkt == message_size-1):
							nodes[x].src = 1
							sources.append(nodes[x].id)	 							# updating sources
							nodes[x].sleep_time = 0
							partial_wastage = 1 - transfer_count/float(no_of_transfer_packets)
							wastage += partial_wastage
							break
						pkt += 1	
				
		time += 1	
		for x in range(no_of_nodes):
			if(nodes[x].sleep_time > 0):
				nodes[x].sleep_time -= 1
		# print "Number of sources = ", len(sources)		
	
	print time
	print wastage
	print bandwidth							