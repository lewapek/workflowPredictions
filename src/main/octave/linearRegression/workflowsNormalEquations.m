clear; close all; clc

function [theta] = normalEquations(x, y)
  theta = pinv(x' * x) * x' * y;
end

# getting filename from command line
filename = '../../../../workflowsData.csv';
if length(argv()) >= 1
  argument = argv(){1};
  if strcmp(argument, "--force-gui") == 0 # 0 means different
    filename = argv(){1};
  end
end
#filename

# preparing data
data = csvread(filename);
columnsQuantity = size(data, 2);
x = data(:, 2:columnsQuantity);
y = data(:, 1);
m = length(y);

# adding intercept column features
x = [ones(m, 1) x];

# computing theta
theta = normalEquations(x, y);

# computing RMSE (root mean square error)
predicted = zeros(m, 1);
for i=1:m
  predicted(i) = x(i, :) * theta;
end
errors = (predicted - y) .^ 2;
rmse = sum(errors) / m;

# writing results to files
csvwrite("tmp/theta.csv", theta);
csvwrite("tmp/rmse.csv", rmse);
