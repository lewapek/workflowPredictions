import argparse

from sklearn.neighbors import KNeighborsRegressor

from utils.parser_common import add_common_arguments_to, get_split_parameter_using
from utils.regression_utils import read_from_input_file, calculate_and_write_errors_using

parser = argparse.ArgumentParser(description="Decision tree regression")
add_common_arguments_to(parser)
parser.add_argument('-n', '--neighbours', dest='neighbours', default='10', required=False, help='neighbours number')
parser.add_argument('-a', '--algorithm', dest='algorithm', default='auto', required=False, help='algorithm')
args = parser.parse_args()

neighbours_number = int(args.neighbours)
algorithm = args.algorithm

data = read_from_input_file(args.input_file)
split = get_split_parameter_using(args=args, data=data)

train_data = data[:split]
test_data = data[split:]
m_train = len(train_data)
m_test = len(test_data)
print("train size = " + str(m_train) + ", test size = " + str(m_test))

y = list(map(lambda elem: elem[0], train_data))
x = list(map(lambda elem: elem[1:], train_data))

regression = KNeighborsRegressor(n_neighbors=neighbours_number, algorithm=algorithm)
regression.fit(x, y)

index_test = list(map(lambda elem: elem[-1], test_data))
x_test = list(map(lambda elem: elem[1:], test_data))
y_test = list(map(lambda elem: elem[0], test_data))
y_predicted = regression.predict(x_test)
print(list(zip(y_test, y_predicted))[:4])

calculate_and_write_errors_using(comparison_file=args.comparison_file,
                                 comparison_file_indexed=args.comparison_file_indexed,
                                 y_test=y_test,
                                 y_predicted=y_predicted,
                                 index_test=index_test,
                                 m_test=m_test,
                                 rmse_file=args.tmp_dir + "/" + args.rmse_file,
                                 mae_file=args.tmp_dir + "/" + args.mae_file,
                                 abs_div_mean_file=args.tmp_dir + "/" + args.abs_div_mean_error_file,
                                 relative_error_file=args.tmp_dir + "/" + args.relative_error_file)
