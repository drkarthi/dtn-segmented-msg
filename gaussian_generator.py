import numpy as np
import sys

mean = int(sys.argv[1])
s = np.random.normal(mean,1,10000)
l = list(s)
for x in range(len(l)):
	if(l[x] < 1):
		l[x] = 0
	else:	
		l[x] = int( round(l[x],0) )
f = open("gaussian_distribution.txt", "w")
for item in l:
	f.write("%s\n" %item)