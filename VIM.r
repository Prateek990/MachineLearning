####################################################
################DATA PREPROCESSING##################
####################################################
rm(list=ls())

load(file="football_data.Rdata")
y  <- factor(dtset[,1], labels=c("W","L","D"))
Xf <- dtset[,2:ncol(dtset)]

# VARIABLE IMPORTANCE EVALUATION

Zf <- Xf[sample(nrow(Xf)),]
dtset.pseudo <- data.frame(cbind(Xf, Zf, y))
library(randomForest)
rf <- randomForest(y ~ ., data=dtset.pseudo, ntree=500)
VIMs <- importance(rf, type=2)
p <- ncol(Xf)
VIMs.unb <- VIMs[1:p,] - VIMs[(p+1):(2*p),]

VIMs.unb <- function(k){
   set.seed(k)
   Zf <- Xf[sample(nrow(Xf)),]
   dtset.pseudo <- data.frame(cbind(Xf,Zf,y))
   rf <- randomForest(y ~ ., data=dtset.pseudo, ntree=500)
   VIMs <- importance(rf, type=2)
   VIMs[1:p,] - VIMs[(p+1):(2*p),]
}

library(snowfall)
sfInit(parallel=TRUE, cpus=6, type="SOCK")
sfLibrary(randomForest)
sfExport("Xf", "y", "p")
VIMs.list <- sfLapply(x=1:10, VIMs.unb)
sfStop()

VIMs <- t(matrix(unlist(VIMs.list),p))
GINI.unb <- apply(VIMs,2,mean)
idx <- order(GINI.unb,decreasing=T)
Xs <- Xf[,idx[1:13]]
Xs

# Plotting important VIM on the graph

dt <- data.frame(id=1:50,
           VIM=GINI.unb[idx[1:50]],
           grp=c(rep(1,4),rep(2,9),rep(3,50-4-9)),
           names=c(names(Xs),rep("",50-13)),
           cols= c(rep("red",4),rep("blue",9),rep("gray50",50-4-9)))

plot4 <- ggplot(dt, aes(x=id, y=VIM, label=names, colour=cols))+ 
   geom_point() + scale_colour_discrete(l=60)+ scale_fill_identity() +
   geom_text(angle = 45,hjust=-.05, vjust=0, size=4.2)+
   scale_y_continuous(name="Gini VIM corrected",limits=c(0,3.05))+
   scale_x_continuous(name="")+
   theme(legend.position="none",text=element_text(size = 24))

ggsave(filename="plot4.pdf", plot=plot4)

save(Xs,y,file="selected_covariates.RData")
Xc <- Xs
save(Xc,y,file="object_scores.RData")

