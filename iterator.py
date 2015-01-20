import subprocess
import matplotlib.pyplot as plt
import sys

no_of_nodes = 10879
distribution = sys.argv[1]
mean = int(sys.argv[2])

def run_shell_cmd(cmd1, cmd2):
	subprocess.call(cmd1, shell=True)
	p = subprocess.Popen(cmd2, stdout=subprocess.PIPE, shell=True)
	output = p.communicate()[0]
	output_list = output.split('\n')[:-1]
	return output_list

def find_mean_values():	
	N = 20
	m = [4,8,16,24,32]
	wastage_sum = [ [0 for x in range(4)] for y in range(5)] 			# 4 -> number of algos, 5-> number of message sizes
	time_sum = [ [0 for x in range(4)] for y in range(5)]
	bandwidth_sum = [ [0 for x in range(4)] for y in range(5)]
	avg_wastage = [ [0 for x in range(4)] for y in range(5)]
	avg_time = [ [0 for x in range(4)] for y in range(5)]
	avg_bandwidth = [ [0 for x in range(4)] for y in range(5)]
	inefficiency = [ [0 for x in range(4)] for y in range(5)]
	coverage = [ [0 for x in range(4)] for y in range(5)]
	for j in range(5):
		cmds = [ ['javac blind_push.java', 'java blind_push '+str(m[j])+' '+distribution+' '+str(mean)], ['javac xpercentstrategy.java', 'java xpercentstrategy '+str(m[j])+' 8'+' '+distribution+' '+str(mean)], ['javac push_with_giveup.java', 'java push_with_giveup '+str(m[j])+' '+distribution+' '+str(mean)], ['\n', 'python push_pull_with_giveup.py '+str(m[j])+' '+distribution+' '+str(mean)] ]
		for x in range(N):
			for i in range(4):
				output_list = run_shell_cmd(cmds[i][0], cmds[i][1])
				time_sum[j][i] += int(output_list[0])
				wastage_sum[j][i] += int(output_list[1])
				bandwidth_sum[j][i] += int(output_list[2])
		for i in range(4):
			avg_time[j][i] = time_sum[j][i]/float(N)
			avg_wastage[j][i] = wastage_sum[j][i]/float(N)
			avg_bandwidth[j][i] = bandwidth_sum[j][i]/float(N)
			inefficiency[j][i] = float(avg_wastage[j][i])/(avg_wastage[j][i]+avg_bandwidth[j][i]) * 100
			coverage[j][i] = ( float(avg_bandwidth[j][i])/m[j] + 1 ) / no_of_nodes * 100   		# adding 1 for including the source node				

	print avg_time
	print inefficiency
	print coverage
	return avg_time,inefficiency,coverage

def get_combined_lists(time, wastage, coverage):
	combined_time = []
	combined_wastage = []
	combined_coverage = []
	for j in range(len(time)):
		combined_time = combined_time + time[j]
		combined_wastage = combined_wastage + wastage[j]
		combined_coverage = combined_coverage + coverage[j]
	return combined_time, combined_wastage, combined_coverage	

def plot_bar_graph(time, wastage, coverage):
	left = [1,2,3,4,6,7,8,9,11,12,13,14,16,17,18,19,21,22,23,24]
	text_positions = [2,7,12,17,22]
	width = 0.35
	combined_time, combined_wastage, combined_coverage = get_combined_lists(time,wastage,coverage)

	p1 = plt.bar(left, combined_time, width, color=['b','m','r','g'])
	plt.xticks(text_positions, ('m=k=4', 'm=k=8', 'm=k=16', 'm=k=24', 'm=k=32') )
	plt.xlabel('Message size')
	plt.ylabel('T')
	plt.title('Time')
	plt.yticks(range(0,5000,1000))
	plt.show()

	p2 = plt.bar(left, combined_wastage, width, color=['b','m','r','g'])
	plt.xticks(text_positions, ('m=k=4', 'm=k=8', 'm=k=16', 'm=k=24', 'm=k=32') )
	plt.xlabel('Message size')
	plt.ylabel('C*')
	plt.title('Wastage')
	plt.yticks(range(0,100,20))
	plt.show()

	p3 = plt.bar(left, combined_coverage, width, color=['b','m','r','g'])
	plt.xticks(text_positions, ('m=k=4', 'm=k=8', 'm=k=16', 'm=k=24', 'm=k=32') )
	plt.xlabel('Message size')
	plt.ylabel('Coverage')
	plt.title('Coverage')
	plt.yticks(range(0,100,20))
	plt.show()

if __name__=="__main__":
	avg_time,inefficiency,coverage = find_mean_values()
	if(len(sys.argv) != 4 or sys.argv[3]!='noplot'):
		plot_bar_graph(avg_time,inefficiency,coverage)	