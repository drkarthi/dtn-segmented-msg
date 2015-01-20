import matplotlib.pyplot as plt

l = [[708.0, 1318.0, 2277.0, 3153.0, 4374.0], [430.0, 928.0, 1583.0, 2508.0, 3124.0], [449.0, 482.0, 905.0, 961.0, 1440.0], [454.0, 832.0, 2255.0, 2092.0, 2799.0]]
x = [1,2,3,4,5]
plt.xticks(x, ('m=k=4', 'm=k=8', 'm=k=16', 'm=k=24', 'm=k=32') )
plt.plot(x, l[0], 'bo', ms=8, label='Uniform')
plt.plot(x, l[1], 'go', ms=8, label='Gaussian')
plt.plot(x, l[2], 'ro', ms=8, label='Power law')
plt.plot(x, l[3], 'mo', ms=8, label='Poisson')
plt.legend(loc=2, numpoints=1)
plt.show()