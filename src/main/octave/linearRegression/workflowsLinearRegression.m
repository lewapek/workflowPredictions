clear; close all; clc

function [xNormalized, columnsMean, sigma] = featureNormalize(x)
  xNormalized = x;
  columnsMean = zeros(1, size(x, 2));
  sigma = zeros(1, size(x, 2));

  columnsMean = mean(x); # mean for each column (each feature)
  sigma = std(x);
  xNormalized = (x - columnsMean) ./ sigma;
end

function [theta, J_history] = gradientDescentMulti(X, y, theta, alpha, num_iters)
  m = length(y); # number of training examples
  J_history = zeros(num_iters, 1);

  for iter = 1:num_iters
      delta = (1 / m) * sum(X .* repmat((X * theta - y), 1, size(X, 2)));
      theta = (theta' - (alpha * delta))';
   
      J_history(iter) = computeCostMulti(X, y, theta);
  end
end

function J = computeCostMulti(X, y, theta)
  m = length(y); # number of training examples

  constantFactor = 1 / (2 * m);
  squares = (X * theta - y) .^ 2;
  J = constantFactor * sum(squares);
end

# getting filename from command line
filename = '../../../../workflowsData.csv';
if length(argv()) >= 1
  argument = argv(){1};
  if strcmp(argument, "--force-gui") == 0 # 0 means different
    filename = argv(){1};
  end
end
# filename

data = csvread(filename);
# shuffling data
randomRowsPermutation = randperm(size(data, 1));
data = data(randomRowsPermutation, :);

rowsQuantity = size(data, 1);
rowsQuantity
trainingRowsQuantity = int32(0.8 * rowsQuantity);
trainingRowsQuantity
columnsQuantity = size(data, 2);

x = data(1:trainingRowsQuantity, 2:columnsQuantity);
y = data(1:trainingRowsQuantity, 1);
m = length(y);

xTest = data(trainingRowsQuantity + 1:rowsQuantity, 2:columnsQuantity);
yTest = data(trainingRowsQuantity + 1:rowsQuantity, 1);
mTest = length(yTest);

[x columnsMean sigma] = featureNormalize(x);
[xTest columnsMeanTest sigmaTest] = featureNormalize(xTest);

# adding intercept feature column
x = [ones(m, 1) x];
xTest = [ones(mTest, 1), xTest];

# gradient descent
alpha = 0.1;
num_iters = 1000;

theta = zeros(columnsQuantity, 1);
[theta, J_history] = gradientDescentMulti(x, y, theta, alpha, num_iters);

% Plot the convergence graph
%figure;
%plot(1:numel(J_history), J_history, '-b', 'LineWidth', 2);
%xlabel('Number of iterations');
%ylabel('Cost J');

# computing RMSE (root mean square error)
predicted = zeros(mTest, 1);
for i=1:mTest
  predicted(i) = xTest(i, :) * theta;
end
csvwrite("tmp/comparison.csv", [yTest, predicted]);

errors = (predicted - yTest) .^ 2;
#errors = ((1 ./ predicted) - (1 ./ yTest)) .^ 2;
relative_errors = abs(predicted - yTest) ./ yTest;

rmse = sqrt(sum(errors) / mTest);
relative_error = sum(relative_errors) / mTest;

# writing results to files
csvwrite("tmp/theta.csv", theta);
csvwrite("tmp/rmse.csv", rmse);
csvwrite("tmp/relativeError.csv", relative_error);
