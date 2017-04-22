import argparse
from matplotlib import pyplot as plot

parser = argparse.ArgumentParser(description="Comparison plotter")
parser.add_argument('-c', '--comparison', dest='comparison_file', default="tmp/comparison.csv", required=False,
                    help='comparison file')
parser.add_argument('-i', '--indexing_mode', dest='indexing_mode', default=False, required=False, help='indexing mode')
parser.add_argument('-o', '--output', dest='output_file', required=True, help='output file')
parser.add_argument('-t', '--title', dest='title', default='Real vs predicted', required=False, help='title')
args = parser.parse_args()

input_file = open(args.comparison_file, "r")
lines = input_file.readlines()
input_file.close()

extract_data = (
    lambda split_line: [float(split_line[0]), float(split_line[1]), float(split_line[2])]
) if args.indexing_mode else (
    lambda split_line: [float(split_line[0]), float(split_line[1])]
)

data = []
for line in lines:
    split = line.split(",")
    data.append(extract_data(split))
data = sorted(data, key=lambda row: row[0])

columns = list(zip(*data))
real, predicted = columns[0], columns[1]

plot.title(args.title)
plot.plot(real, ".", label="real", color="blue", )
plot.plot(predicted, ".", label="predicted", color="red")
plot.legend(loc=0)
plot.savefig(args.output_file + ".png")

if args.indexing_mode:
    data = sorted(data, key=lambda row: row[2])
    columns = list(zip(*data))
    real, predicted, index = columns[0], columns[1], columns[2]
    plot.figure()
    plot.title(args.title)
    plot.plot(index, real, ".", label="real", color="blue", )
    plot.plot(index, predicted, ".", label="predicted", color="red")
    plot.legend(loc=0)
    plot.savefig(args.output_file + "_indexed.png")
