import subprocess
import matplotlib.pyplot as plt

def plot_across_algos(l, title, loc=3): 		# l is of the form [ [1,2,3,4,5],[6,7,8,9,10],[11,12,13,14,15],[16,17,18,19,20] ]
	x = [1,2,3,4,5]
	plt.xticks(x, ('m=k=4', 'm=k=8', 'm=k=16', 'm=k=24', 'm=k=32') )
	plt.plot(x, l[0], 'bo', ms=8, label='BP')
	plt.plot(x, l[1], 'go', ms=8, label='XPP')
	plt.plot(x, l[2], 'ro', ms=8, label='PG')
	plt.plot(x, l[3], 'mo', ms=8, label='PPG')
	plt.title(title)
	plt.legend(loc=loc, numpoints=1)
	plt.show()

if __name__=='__main__':
	Uniform_list = [ [[] for y in range(4)] for x in range(3) ]
	Gaussian_list = [ [[] for y in range(4)] for x in range(3) ]
	Powerlaw_list = [ [[] for y in range(4)] for x in range(3) ]
	Poisson_list = [ [[] for y in range(4)] for x in range(3) ]

	output = subprocess.check_output(['python', 'iterator.py', 'uniform', 'noplot'])
	output_list = output.split('\n')[:-1] 
	for x in range(len(output_list)):
		measure_list = output_list[x][1:-2].split('], ')
		for measure in measure_list:
			item_list = measure[1:].split(', ')
			for i in range(len(item_list)):
				Uniform_list[x][i].append(item_list[i])

	output = subprocess.check_output(['python', 'iterator.py', 'gaussian', 'noplot'])
	output_list = output.split('\n')[:-1] 
	for x in range(len(output_list)):
		measure_list = output_list[x][1:-2].split('], ')
		for measure in measure_list:
			item_list = measure[1:].split(', ')
			for i in range(len(item_list)):
				Gaussian_list[x][i].append(item_list[i])

	output = subprocess.check_output(['python', 'iterator.py', 'powerlaw', 'noplot'])
	output_list = output.split('\n')[:-1] 
	for x in range(len(output_list)):
		measure_list = output_list[x][1:-2].split('], ')
		for measure in measure_list:
			item_list = measure[1:].split(', ')
			for i in range(len(item_list)):
				Powerlaw_list[x][i].append(item_list[i])

	output = subprocess.check_output(['python', 'iterator.py', 'poisson', 'noplot'])
	output_list = output.split('\n')[:-1] 
	for x in range(len(output_list)):
		measure_list = output_list[x][1:-2].split('], ')
		for measure in measure_list:
			item_list = measure[1:].split(', ')
			for i in range(len(item_list)):
				Poisson_list[x][i].append(item_list[i])

	print Uniform_list
	print Gaussian_list
	print Powerlaw_list
	print Poisson_list									

	plot_across_algos(Uniform_list[0], 'Uniform distribution - Time vs Message size', 2)
	plot_across_algos(Uniform_list[1], 'Uniform distribution - Wastage vs Message size')
	plot_across_algos(Uniform_list[2], 'Uniform distribution - Coverage vs Message size')
	plot_across_algos(Gaussian_list[0], 'Gaussian distribution - Time vs Message size', 2)
	plot_across_algos(Gaussian_list[1], 'Gaussian distribution - Wastage vs Message size')
	plot_across_algos(Gaussian_list[2], 'Gaussian distribution - Coverage vs Message size')
	plot_across_algos(Powerlaw_list[0], 'Powerlaw distribution - Time vs Message size', 2)
	plot_across_algos(Powerlaw_list[1], 'Powerlaw distribution - Wastage vs Message size')
	plot_across_algos(Powerlaw_list[2], 'Powerlaw distribution - Coverage vs Message size')
	plot_across_algos(Poisson_list[0], 'Poisson distribution - Time vs Message size', 2)
	plot_across_algos(Poisson_list[1], 'Poisson distribution - Wastage vs Message size')
	plot_across_algos(Poisson_list[2], 'Poisson distribution - Coverage vs Message size')