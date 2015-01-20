import subprocess
import matplotlib.pyplot as plt
import sys

def plot_across_distributions(l, title, loc=3): 		# l is of the form [ [1,2,3,4,5],[6,7,8,9,10],[11,12,13,14,15],[16,17,18,19,20] ]
	x = [1,2,3,4,5]
	plt.xticks(x, ('m=k=4', 'm=k=8', 'm=k=16', 'm=k=24', 'm=k=32') )
	plt.plot(x, l[0], 'bo', ms=8, label='Uniform')
	plt.plot(x, l[1], 'go', ms=8, label='Gaussian')
	plt.plot(x, l[2], 'ro', ms=8, label='Power law')
	plt.plot(x, l[3], 'mo', ms=8, label='Poisson')
	plt.title(title)
	plt.legend(loc=loc, numpoints=1)
	plt.show()

if __name__=='__main__':
	distribution_list = ['uniform', 'gaussian', 'powerlaw', 'poisson']
	BP_list = [ [[] for y in range(4)] for x in range(3) ]
	XPP_list = [ [[] for y in range(4)] for x in range(3) ]
	PG_list = [ [[] for y in range(4)] for x in range(3) ]
	PPG_list = [ [[] for y in range(4)] for x in range(3) ]
	mean = sys.argv[1]

	for y in range(len(distribution_list)):
		output = subprocess.check_output(['python', 'iterator.py', distribution_list[y], str(mean),'noplot'])
		output_list = output.split('\n')[:-1] 
		for x in range(len(output_list)):
			measure_list = output_list[x][1:-2].split('], ')
			for item in measure_list:
				item_list = item[1:].split(', ')
				BP_list[x][y].append(float(item_list[0]))
				XPP_list[x][y].append(float(item_list[1]))
				PG_list[x][y].append(float(item_list[2]))
				PPG_list[x][y].append(float(item_list[3]))
	print BP_list
	print XPP_list
	print PG_list
	print PPG_list

	plot_across_distributions(BP_list[0], 'BP-Time vs Message size', 2)
	plot_across_distributions(BP_list[1], 'BP-Wastage vs Message size')
	plot_across_distributions(BP_list[2], 'BP-Coverage vs Message size')
	plot_across_distributions(XPP_list[0], 'XPP-Time vs Message size', 2)
	plot_across_distributions(XPP_list[1], 'XPP-Wastage vs Message size')
	plot_across_distributions(XPP_list[2], 'XPP-Coverage vs Message size')
	plot_across_distributions(PG_list[0], 'PG-Time vs Message size', 2)
	plot_across_distributions(PG_list[1], 'PG-Wastage vs Message size')
	plot_across_distributions(PG_list[2], 'PG-Coverage vs Message size')
	plot_across_distributions(PPG_list[0], 'PPG-Time vs Message size', 2)
	plot_across_distributions(PPG_list[1], 'PPG-Wastage vs Message size')
	plot_across_distributions(PPG_list[2], 'PPG-Coverage vs Message size')		