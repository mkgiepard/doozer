export ROOT_DIR="/Users/mario/gitrepos/doozer"
export RESULTS_DIR="target/doozer-tests"

cd $ROOT_DIR/$RESULTS_DIR

for x in `ls -d *Test`;
do cp $x/*.png ../../src/test/java/dev/softtest/doozer/scripts/$x/goldens/;
rm ../../src/test/java/dev/softtest/doozer/scripts/$x/goldens/*DIFF.png; 
rm ../../src/test/java/dev/softtest/doozer/scripts/$x/goldens/*onFAILURE.png;
done