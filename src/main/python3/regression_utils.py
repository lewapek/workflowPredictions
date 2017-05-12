import csv
import math
from statistics import mean


def read_from_input_file(input_file):
    rows = []
    f = open(input_file, "r")
    reader = csv.reader(f)
    for row in reader:
        floats = list(map(lambda elem: float(elem), row))
        rows.append(floats)
    f.close()
    return rows


def write_error_file(error, path):
    f = open(path, "w")
    f.write(str(error))
    f.close()


def calculate_and_write_errors_using(comparison_file, comparison_file_indexed, y_test, y_predicted, index_test, m_test,
                                     rmse_file, mae_file, abs_div_mean_file, relative_error_file):
    rmse = 0.0
    absolute_error_sum = 0.0
    relative = 0.0
    relative_is_infinity = False
    comparison_file = open(comparison_file, "w")
    comparison_file_indexed = open(comparison_file_indexed, "w")
    for test, predicted, index in zip(y_test, y_predicted, index_test):
        comparison_file.write(str(test) + "," + str(predicted) + "\n")
        comparison_file_indexed.write(str(test) + "," + str(predicted) + "," + str(index) + "\n")

        absolute_error_sum += math.fabs(predicted - test)
        rmse += math.pow(test - predicted, 2)
        if not relative_is_infinity:
            if test != 0.0:
                relative += math.fabs(predicted - test) / test
            else:
                relative_is_infinity = True

    comparison_file.close()
    comparison_file_indexed.close()

    rmse = math.sqrt(rmse / m_test)
    mae = absolute_error_sum / m_test
    absolute_error_div_mean = mae / mean(y_test)
    relative = (relative / m_test) if not relative_is_infinity else -1.0

    write_error_file(rmse, rmse_file)
    write_error_file(mae, mae_file)
    write_error_file(absolute_error_div_mean, abs_div_mean_file)
    write_error_file(relative, relative_error_file)

    print(rmse, mae, absolute_error_div_mean, relative)
