import argparse
import math
import random
from statistics import mean

from sklearn.ensemble import RandomForestRegressor

from regression_utils import read_from_input_file, write_error_file

parser = argparse.ArgumentParser(description="Decision tree regression")

parser.add_argument('-s', '--split', dest='split', default=None, required=False, help='split training:testing')
parser.add_argument('-t', '--tmp_dir', dest='tmp_dir', default="tmp", required=False, help='input file')
parser.add_argument('-i', '--input', dest='input_file', default="tmp/taskLogsInput.csv", required=False,
                    help='input file')
parser.add_argument('-c', '--comparison', dest='comparison_file', default="tmp/comparison.csv", required=False,
                    help='comparison file')
parser.add_argument('--comparison_indexed', dest='comparison_file_indexed', default="tmp/comparisonIndexed.csv",
                    required=False, help='comparison file')
parser.add_argument('--rmse', dest='rmse_file', default='rmse.csv', required=False, help='rmse output file')
parser.add_argument('--abs_div_mean', dest='abs_div_mean_error_file', default='absDivMean.csv', required=False,
                    help='absolute error divided by mean output file')
parser.add_argument('--relative', dest='relative_error_file', default='relativeError.csv', required=False,
                    help='relative error output file')
parser.add_argument('-m', '--max-depth', dest='max_depth', default='10', required=False, help='max depth')

args = parser.parse_args()
max_depth = int(args.max_depth)

data = read_from_input_file(args.input_file)
m = len(data)

if args.split is None:
    print("Shuffling and calculating split")
    random.shuffle(data)  # shuffles in place
    split = math.ceil(0.8 * m)
else:
    print("Getting split from argument, split = " + args.split)
    split = int(args.split)
train_data = data[:split]
test_data = data[split:]
m_train = len(train_data)
m_test = len(test_data)
print("train size = " + str(m_train) + ", test size = " + str(m_test))

y = list(map(lambda elem: elem[0], train_data))
x = list(map(lambda elem: elem[1:], train_data))
# print("x[0]=" + str(x[0]))
# print("y[:10]=" + str(y[:10]))

regression = RandomForestRegressor(max_depth=max_depth, criterion="mae")
regression.fit(x, y)

index_test = list(map(lambda elem: elem[-1], test_data))
x_test = list(map(lambda elem: elem[1:], test_data))
y_test = list(map(lambda elem: elem[0], test_data))
y_predicted = regression.predict(x_test)
print(list(zip(y_test, y_predicted))[:4])

rmse = 0.0
absolute_error_div_mean = 0.0
relative = 0.0
relative_is_infinity = False
comparison_file = open(args.comparison_file, "w")
comparison_file_indexed = open(args.comparison_file_indexed, "w")
for test, predicted, index in zip(y_test, y_predicted, index_test):
    comparison_file.write(str(test) + "," + str(predicted) + "\n")
    comparison_file_indexed.write(str(test) + "," + str(predicted) + "," + str(index) + "\n")

    absolute_error_div_mean += math.fabs(predicted - test)
    rmse += math.pow(test - predicted, 2)
    if not relative_is_infinity:
        if test != 0.0:
            relative += math.fabs(predicted - test) / test
        else:
            relative_is_infinity = True

comparison_file.close()
comparison_file_indexed.close()

rmse = math.sqrt(rmse / m_test)
absolute_error_div_mean = (absolute_error_div_mean / m_test) / mean(y_test)
relative = (relative / m_test) if not relative_is_infinity else -1.0

write_error_file(rmse, args.tmp_dir + "/" + args.rmse_file)
write_error_file(absolute_error_div_mean, args.tmp_dir + "/" + args.abs_div_mean_error_file)
write_error_file(relative, args.tmp_dir + "/" + args.relative_error_file)

print(rmse, absolute_error_div_mean, relative)
