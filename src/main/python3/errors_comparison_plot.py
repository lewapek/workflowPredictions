import argparse
from matplotlib import pyplot as plot
from textwrap import wrap

parser = argparse.ArgumentParser(description="Comparison plotter")
parser.add_argument('-c', '--comparison', dest='comparison_file',  required=True, help='comparison file')
parser.add_argument('-o', '--output', dest='output_file', required=True, help='output file')
parser.add_argument('-t', '--title', dest='title', default='', required=False, help='title prefix')
args = parser.parse_args()

input_file = open(args.comparison_file, "r")
lines_no_header = input_file.readlines()[1:]
input_file.close()


data = []
for line in lines_no_header:
    split = line.split(",")
    data.append([split[0], split[1], float(split[2]), float(split[3]), float(split[4]), float(split[5]), int(split[6])])
sorted_rmse = ["rmse", 2, sorted(data, key=lambda row: row[2])]
sorted_mae = ["mae", 3, sorted(data, key=lambda row: row[3])]
sorted_abs_div_mean = ["absDivMean", 4, sorted(data, key=lambda row: row[4])]
sorted_relative = ["relative", 5, sorted(data, key=lambda row: row[5])]

all_plots = [sorted_rmse, sorted_mae, sorted_abs_div_mean, sorted_relative]

for plot_type in all_plots:
    name = plot_type[0]
    value_column = plot_type[1]
    plot_data = plot_type[2]
    columns = list(zip(*plot_data))

    converter, regression, error = columns[0], columns[1], columns[value_column]
    xticks = list(map(
        lambda elem: '\n'.join(wrap(elem[0].replace('_', ' '), 4)) + "\n" + '\n'.join(wrap(elem[1], 4)),
        zip(regression, converter)
    ))

    indexes = list(range(len(error)))
    plot.figure()
    plot.title(args.title + " " + name)
    plot.bar(indexes, error, label=name)
    plot.xticks(indexes, xticks)
    plot.tight_layout()
    plot.legend()
    output = args.output_file + "_" + name + ".png"
    print("output = " + output)
    plot.savefig(output)
