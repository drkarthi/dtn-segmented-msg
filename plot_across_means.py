import subprocess
import matplotlib.pyplot as plt

def run_shell_cmd(cmd1, cmd2):
	subprocess.call(cmd1, shell=True)
	p = subprocess.Popen(cmd2, stdout=subprocess.PIPE, shell=True)
	output = p.communicate()[0]
	output_list = output.split('\n')[:-1]
	return output_list

def plot_across_means(l, title, loc=3):	 				# l is of the form [ [1,2,3,4,5],[6,7,8,9,10],[11,12,13,14,15],[16,17,18,19,20] ]
	x = [1,2,3,4,5]
	plt.xticks(x, ('2', '4', '6', '8', '10') )
	plt.xlabel('Mean')
	plt.plot(x, l[0], 'bo', ms=8, label='Uniform')
	plt.plot(x, l[1], 'go', ms=8, label='Gaussian')
	plt.plot(x, l[2], 'ro', ms=8, label='Power law')
	plt.plot(x, l[3], 'mo', ms=8, label='Poisson')
	plt.title(title)
	plt.legend(loc=loc, numpoints=1)
	plt.show()

if __name__=='__main__':
	distribution_list = ['uniform', 'gaussian', 'powerlaw', 'poisson']
	mean_list = [2,4,6,8,10]
	no_of_nodes = 10879
	msg_size = 32
	algo_dict = {}
	l = []
	algo_list = ['BP_list', 'XPP_list', 'PG_list', 'PPG_list']
	for algo in algo_list:
		print algo
		algo_dict[algo] = [ [[] for y in range(4)] for x in range(3) ]
	print algo_dict	

	for y in range(len(distribution_list)):
		for i in range(len(mean_list)):
			subprocess.call('python gaussian_generator.py '+str(mean_list[i]), shell=True)
			subprocess.call('python distribution_generator.py '+str(mean_list[i]), shell=True)
			subprocess.call('python poisson_generator.py '+str(mean_list[i]), shell=True)
			cmds = [ ['javac blind_push.java', 'java blind_push '+str(msg_size)+' '+distribution_list[y]+' '+str(mean_list[i])], ['javac xpercentstrategy.java', 'java xpercentstrategy '+str(msg_size)+' 8'+' '+distribution_list[y]+' '+str(mean_list[i])], ['javac push_with_giveup.java', 'java push_with_giveup '+str(msg_size)+' '+distribution_list[y]+' '+str(mean_list[i])], ['\n', 'python push_pull_with_giveup.py '+str(msg_size)+' '+distribution_list[y]+' '+str(mean_list[i])] ]
			for j in range(len(algo_list)):
				algo = algo_list[j]
				# print algo
				output_list = run_shell_cmd(cmds[j][0],cmds[j][1])
				for x in range(len(output_list)):
					algo_dict[algo][x][y].append(output_list[x])				
	
	for algo in algo_list:
		for i in range(4):
			for j in range(5):
				a = float(algo_dict[algo][1][i][j])
				b = float(algo_dict[algo][2][i][j])
				algo_dict[algo][1][i][j] = a/(a+b)
				algo_dict[algo][2][i][j] = ( float(algo_dict[algo][2][i][j])/msg_size + 1 )/no_of_nodes*100
	print algo_dict

	titles = ['Time vs Mean', 'Wastage vs Mean', 'Coverage vs Mean']
	for algo in algo_list:
		for x in range(len(algo_dict[algo])):
			if x==0:
				loc = 1
			else:
				loc = 3	
			plot_across_means(algo_dict[algo][x], algo+' - '+titles[x], loc)				