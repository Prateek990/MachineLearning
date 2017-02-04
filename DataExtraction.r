#Displaying the initial data
rm(list=ls())

#The first coloumn is for win loss and draw
load(file="football_data.Rdata")
str(dtset)
# Storing result
y  <- factor(dtset[,1], labels=c("W","L","D"))
# Initial covariate, all 482 
Xi <- dtset[,2:ncol(dtset)]
AbsFreq <- table(y)
PerFreq <- round(prop.table(AbsFreq)*100,1)
cbind(AbsFreq,PerFreq) 

# Initial analysis on data
library(ggplot2)
Freq <- data.frame(PerFreq)
plot1 <- ggplot(Freq,aes(x="", fill=y, weight=Freq))+ geom_bar(width = 1)+ 
    scale_fill_manual(values=c("green","yellow","red"))+
    scale_y_continuous("Percentage frequency")+scale_x_discrete(name="")+
    theme(text=element_text(size = 24))
ggsave(filename="plot1.pdf", plot=plot1)

library(Hmisc)
describe(Xi)

x.name <- "O_OCCAS_C"
x <- Xi[,names(Xi) %in% x.name]
pf <- prop.table(table(x,y),1)[,c(1,3,2)]
dtst <- data.frame(PctFreq=c(t(pf)),
      x=rep(as.numeric(rownames(pf)),each=ncol(pf)),
      Outcome=ordered(rep(1:3,nrow(pf)), labels=colnames(pf))) 

plot2 <- ggplot(dtst, aes(x=x, y=PctFreq, group=Outcome, fill=Outcome)) +
    geom_area(position="fill") +  scale_x_continuous(x.name) +
    scale_y_continuous("Percentage frequency") +
    scale_fill_manual(values = c("green","yellow","red"))+
    theme(text=element_text(size = 24))
ggsave(filename="plot2.pdf", plot=plot2)
