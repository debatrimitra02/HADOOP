1. 
Tested on AWS ===> Passed


SC.jar

running instruction:
hadoop jar SC.jar <inputfilepath> <outputfilepath>/sentence_count 


2.
Word_position_arg.jar
Tested EMR ===> OK
hadoop jar Word_position_arg.jar <inputfilepath> <outputfilepath>/outi i


running instruction:
in a linux script run like this:

for i in {1..N}
do
   hadoop jar Word_position_arg.jar <inputfilepath> <outputfilepath>/out$i i 
done
/// Tested in 
local ===> OK




3. 
Then we run the CorpusCalculator.java as a standalone with all the outputfiles in "D:/hadoop"
Please change the filepath in the code as this is hard-coded.