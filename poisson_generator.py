import numpy as np
import sys

mean = int(sys.argv[1])
s = np.random.poisson(mean,10000)
l = list(s)
f = open("poisson_distribution.txt", "w")
for item in l:
	f.write("%s\n" %item)
