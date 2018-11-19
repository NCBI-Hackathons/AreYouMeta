by Mahmoud Elgenedy - Nov 2018


proj_max = imageDatastore('/project/hackathon/hackers08/shared/matlab_data/','IncludeSubfolders',true,'LabelSource','foldernames');

[proj_maxTrain,proj_maxValidation] = splitEachLabel(proj_max,0.7,'randomized');



proj_maxTrain_aug = augmentedImageDatastore([227 227],proj_maxTrain,'ColorPreprocessing','gray2rgb');

proj_maxValidation_aug = augmentedImageDatastore([227 227],proj_maxValidation,'ColorPreprocessing','gray2rgb');



options = trainingOptions('sgdm', ...
    'MiniBatchSize',10, ...
    'MaxEpochs',6, ...
    'Shuffle','every-epoch', ...
    'InitialLearnRate',1e-4, ...
    'ValidationData',proj_maxValidation_aug, ...
    'ValidationFrequency',3, ...
    'Verbose',false, ...
    'Plots','training-progress');


net = alexnet;

numClasses = 2;

layersTransfer = net.Layers(1:end-3);


layers = [
    layersTransfer
    fullyConnectedLayer(numClasses,'WeightLearnRateFactor',20,'BiasLearnRateFactor',20)
    softmaxLayer
    classificationLayer];


netTransfer = trainNetwork(proj_maxTrain_aug,layers,options);

deepOut=deepDreamImage(netTransfer, 24, 1:25);

montage(deepOut)


deepOut=deepDreamImage(netTransfer, 24, 1:2)

montage(deepOut)


