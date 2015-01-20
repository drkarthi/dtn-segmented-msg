import matplotlib.pyplot as plt

message_size = 32
probability_distribution = [20015, 37800, 14268, 2843, 2024, 5790, 0, 1954, 0, 1963, 1908, 0, 0, 0, 918, 0, 1897, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3910]
num_packets = range(1, message_size+1) 
plt.plot(num_packets, probability_distribution)
plt.show()