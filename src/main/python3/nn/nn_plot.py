from matplotlib import pyplot as plot

path_prefix = "../../../../src/main/resources/data/snsWorkflows/nnResults/"

f = open(path_prefix + "namd_write.csv")
lines = f.readlines()
f.close()

split = list(map(lambda line: line.split(','), lines))[1:]

time = list(map(lambda line: float(line[2]), split))
stime = list(map(lambda line: float(line[8]), split))
predicted_stime = list(map(lambda line: float(line[9]), split))

plot.plot(stime, '.')
plot.show()
