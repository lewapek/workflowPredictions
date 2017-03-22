import argparse
import random

import math
from statistics import mean

from sklearn.neighbors import KNeighborsRegressor
from sklearn.neighbors import RadiusNeighborsRegressor


from regression_utils import read_from_input_file, write_error_file

parser = argparse.ArgumentParser(description="Decision tree regression")

parser.add_argument('-t', '--tmp_dir', dest='tmp_dir', default="tmp", required=False, help='input file')
parser.add_argument('-i', '--input', dest='input_file', default="tmp/taskLogsInput.csv", required=False,
                    help='input file')
parser.add_argument('-c', '--comparison', dest='comparison_file', default="tmp/comparison.csv", required=False,
                    help='comparison file')
parser.add_argument('--rmse', dest='rmse_file', default='rmse.csv', required=False, help='rmse output file')
parser.add_argument('--abs_div_mean', dest='abs_div_mean_error_file', default='absDivMean.csv', required=False,
                    help='absolute error divided by mean output file')
parser.add_argument('--relative', dest='relative_error_file', default='relativeError.csv', required=False,
                    help='relative error output file')
parser.add_argument('-n', '--neighbours', dest='neighbours', default='10', required=False, help='neighbours number')
args = parser.parse_args()

neighbours_number = int(args.neighbours)


data = read_from_input_file(args.input_file)
random.shuffle(data)  # shuffles in place

m = len(data)
split = math.ceil(0.8 * m)
train_data = data[:split]
test_data = data[split:]
m_train = len(train_data)
m_test = len(test_data)
print("train size = " + str(m_train) + ", test size = " + str(m_test))

y = list(map(lambda elem: elem[0], train_data))
x = list(map(lambda elem: elem[1:], train_data))


RadiusNeighborsRegressor()

regression = KNeighborsRegressor(n_neighbors=neighbours_number)
regression.fit(x, y)


x_test = list(map(lambda elem: elem[1:], test_data))
y_test = list(map(lambda elem: elem[0], test_data))
y_predicted = regression.predict(x_test)
print(list(zip(y_test, y_predicted))[:4])


rmse = 0.0
absolute_error_div_mean = 0.0
relative = 0.0
relative_is_infinity = False
comparison_file = open(args.comparison_file, "w")
for test, predicted in zip(y_test, y_predicted):
    comparison_file.write(str(test) + "," + str(predicted) + "\n")

    absolute_error_div_mean += math.fabs(predicted - test)
    rmse += math.pow(test - predicted, 2)
    if not relative_is_infinity:
        if test != 0.0:
            relative += math.fabs(predicted - test) / test
        else:
            relative_is_infinity = True

comparison_file.close()

rmse = math.sqrt(rmse / m_test)
absolute_error_div_mean = (absolute_error_div_mean / m_test) / mean(y_test)
relative = (relative / m_test) if not relative_is_infinity else -1.0

write_error_file(rmse, args.tmp_dir + "/" + args.rmse_file)
write_error_file(absolute_error_div_mean, args.tmp_dir + "/" + args.abs_div_mean_error_file)
write_error_file(relative, args.tmp_dir + "/" + args.relative_error_file)

print(rmse, absolute_error_div_mean, relative)
