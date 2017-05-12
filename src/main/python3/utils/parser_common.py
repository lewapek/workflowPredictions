import math
import random


def add_common_arguments_to(parser):
    parser.add_argument('-s', '--split', dest='split', default=None, required=False, help='split training:testing')
    parser.add_argument('-f', '--split_factor', dest='split_factor', default="0.8", required=False,
                        help='split factor, specifies training part size')
    parser.add_argument('-t', '--tmp_dir', dest='tmp_dir', default="tmp", required=False, help='input file')
    parser.add_argument('-i', '--input', dest='input_file', default="tmp/taskLogsInput.csv", required=False,
                        help='input file')
    parser.add_argument('-c', '--comparison', dest='comparison_file', default="tmp/comparison.csv", required=False,
                        help='comparison file')
    parser.add_argument('--comparison_indexed', dest='comparison_file_indexed', default="tmp/comparisonIndexed.csv",
                        required=False, help='comparison file')
    parser.add_argument('--rmse', dest='rmse_file', default='rmse.csv', required=False, help='rmse output file')
    parser.add_argument('--mae', dest='mae_file', default='mae.csv', required=False, help='mae output file')
    parser.add_argument('--abs_div_mean', dest='abs_div_mean_error_file', default='absDivMean.csv', required=False,
                        help='absolute error divided by mean output file')
    parser.add_argument('--relative', dest='relative_error_file', default='relativeError.csv', required=False,
                        help='relative error output file')


def get_split_parameter_using(args, data):
    if args.split is None:
        split_factor = float(args.split_factor)
        print("Shuffling and calculating split, split factor = " + str(split_factor))
        random.shuffle(data)  # shuffles in place
        split = math.ceil(split_factor * len(data))
    else:
        print("Getting split from argument, split = " + args.split)
        split = int(args.split)
    return split
