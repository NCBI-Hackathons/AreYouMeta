       
proj_max = imageDatastore('/project/hackathon/hackers08/shared/matlab_data_excludingBadImages/','IncludeSubfolders',true,'LabelSource','foldernames');

numClass = 2;


layers = [ ... 
        imageInputLayer([512 512 1])
        convolution2dLayer(7, 24, 'Stride', 2,'Name','conv1')
        batchNormalizationLayer
        reluLayer
        maxPooling2dLayer(3,'Stride',2,'Name','maxPool1')
        convolution2dLayer(5, 48,'Stride',2,'Name','conv2')
        batchNormalizationLayer
        reluLayer
        maxPooling2dLayer(3,'Stride',2,'Name','maxPool11')
        convolution2dLayer(3,48,'Name','conv3')
        batchNormalizationLayer
        reluLayer
        maxPooling2dLayer(3,'Stride',2,'Name','maxPool3')
        convolution2dLayer(3,96,'Name','conv4','Padding','same')
        reluLayer
        convolution2dLayer(3,96,'Name','conv5','Padding','same')
        reluLayer
        maxPooling2dLayer(3,'Stride', 2,'Name','maxPool4')
        fullyConnectedLayer(1024,'Name','fc1')
        reluLayer
        dropoutLayer
        fullyConnectedLayer(1024,'Name','fc2')
        reluLayer
        dropoutLayer
        fullyConnectedLayer(numClass,'Name','fc3')
        softmaxLayer
        classificationLayer];
    
    
    
    
    
    [proj_maxTrain,proj_maxValidation] = splitEachLabel(proj_max,0.7,'randomized');



proj_maxTrain_aug = augmentedImageDatastore([512 512],proj_maxTrain);

proj_maxValidation_aug = augmentedImageDatastore([512 512],proj_maxValidation);



options = trainingOptions('sgdm', ...
    'MiniBatchSize',10, ...
    'MaxEpochs',6, ...
    'Shuffle','every-epoch', ...
    'InitialLearnRate',1e-3, ...
    'ValidationData',proj_maxValidation_aug, ...
    'ValidationFrequency',3, ...
    'Verbose',false, ...
    'Plots','training-progress');



netTrained = trainNetwork(proj_maxTrain_aug,layers,options);

    deepOut=deepDreamImage(netTrained, 24, 1:25);

montage(deepOut)


deepOut=deepDreamImage(netTrained, 24, 1:2)

montage(deepOut)
    