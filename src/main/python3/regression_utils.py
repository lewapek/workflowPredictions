import csv


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
