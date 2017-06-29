
rm(list=ls())
load(file="object_scores.RData")

library(randomForest)
library(caret)
library(nnet)
library(klaR)
library(doParallel)
library(RSNNS)
library(doParallel)

dtset.ind <- data.frame(Xc, y)
set.seed(987654)
idx <- sample(1:nrow(dtset.ind),80)
learn <- dtset.ind[-idx,]
test  <- dtset.ind[idx,] 

describe(test$y)

# RANDOM FOREST (RF)

clus <- parallel::makeCluster(spec=6, type='PSOCK')
registerDoParallel(clus)
ctrl.train <- trainControl(method='repeatedcv',number=10,repeats=15)
fit.rf <- train(y ~ ., data=learn, method='rf', metric='Accuracy',
           tuneGrid=expand.grid(.mtry=1:6),trControl=ctrl.train,
           ntree=1000)
stopCluster(clus)
print(fit.rf)
y.rf <- predict(fit.rf$finalModel, newdata=test[,1:13],type='class')
cbind(y.rf, test$y)
describe(y.rf)
CM.rf <- caret::confusionMatrix(y.rf, test$y)
CM.rf

# K-NEAREST NEIGHBOR ALGORITHM (KNN)

prc_test_pred <- knn(train, test,cl = y, k=10)

clus <- parallel::makeCluster(spec=6, type='PSOCK')
registerDoParallel(clus)
fit.knn <- caret::train(y~., data=learn, method='knn',
            tuneGrid=expand.grid(.k=5:100), 
            metric='Accuracy', trControl=ctrl.train)
stopCluster(clus)
yhat.knn<-predict(fit.knn$finalModel,newdata=test[,1:13],type="class")
describe(yhat.knn)
CM.knn <- caret::confusionMatrix(yhat.knn, test$y)
CM.knn

## NAÏVE BAYESIAN CLASSIFICATION (NBAYES)

library(klaR)
fit.NB <- NaiveBayes(y~., data=learn)
pred.NB <-  predict(fit.NB, newdata=test)
probs.NB <- pred.NB$posterior
y.nb <- pred.NB$class
CM.nb <- caret::confusionMatrix(y.nb, test$y)
y.nb
CM.nb

describe(y.nb)

# CLASSIFICATION NEURAL NETWORK (NNET)

#clus <- parallel::makeCluster(spec=6, type='PSOCK')
#registerDoParallel(clus)
#fit.nnet <- caret::train(y~., data=learn, method='mlp', metric='Accuracy',
#                         tuneGrid=expand.grid(.size=1:15),learnFunc="SCG",
#                         trControl=ctrl.train)
#stopCluster(clus)
#summary(fit.nnet$finalModel)
#probs.nnet <- predict(fit.nnet$finalModel, newdata=test[,1:6])
#head(probs.nnet)
#y.nnet <- apply(probs.nnet,1,which.max)
#y.nnet <- factor(y.nnet,levels=1:3,labels=levels(test$y))




