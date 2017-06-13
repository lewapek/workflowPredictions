clear; close all; clc

function [theta] = normalEquations(x, y)
  theta = pinv(x' * x) * x' * y;
end

# getting filename from command line
filename = '../../../../workflowsData.csv';
splitFactorString = "0.8";
if length(argv()) >= 1
  argument = argv(){1};
  if strcmp(argument, "--force-gui") == 0 # 0 means different
    filename = argv(){1};
    splitFactorString = argv(){2};
  end
end
# filename
splitFactor = str2double(splitFactorString)

# preparing data
data = csvread(filename);
# shuffling data
randomRowsPermutation = randperm(size(data, 1));
data = data(randomRowsPermutation, :);

rowsQuantity = size(data, 1);
rowsQuantity
trainingRowsQuantity = int32(splitFactor * rowsQuantity);
trainingRowsQuantity
columnsQuantity = size(data, 2);

x = data(1:trainingRowsQuantity, 2:columnsQuantity);
y = data(1:trainingRowsQuantity, 1);
m = length(y);

xTest = data(trainingRowsQuantity + 1:rowsQuantity, 2:columnsQuantity);
indexTest = data(trainingRowsQuantity + 1:rowsQuantity, columnsQuantity);
yTest = data(trainingRowsQuantity + 1:rowsQuantity, 1);
mTest = length(yTest);

# adding intercept feature column
x = [ones(m, 1) x];
xTest = [ones(mTest, 1), xTest];

# computing theta
theta = normalEquations(x, y);

# computing RMSE (root mean square error)
predicted = zeros(mTest, 1);
for i=1:mTest
  predicted(i) = xTest(i, :) * theta;
end
csvwrite("tmp/comparison.csv", [yTest, predicted]);
csvwrite("tmp/comparisonIndexed.csv", [yTest, predicted, indexTest]);

errors = (predicted - yTest) .^ 2;
absolute_errors = abs(predicted - yTest);
relative_errors = absolute_errors ./ yTest;

rmse = sqrt(sum(errors) / mTest);
mae = mean(absolute_errors);
absolute_error_div_mean = mae / mean(yTest);
relative_error = sum(relative_errors) / mTest;

# writing results to files
csvwrite("tmp/theta.csv", theta);
csvwrite("tmp/rmse.csv", rmse);
csvwrite("tmp/mae.csv", mae);
csvwrite("tmp/absDivMean.csv", absolute_error_div_mean);
csvwrite("tmp/relativeError.csv", relative_error);
