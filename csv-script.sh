# first version 
file=$1 #filename
outputFile=$file.output
startReadingLine=${2:-0} # if you want to skip the first 5 minutes, for example, pass 5 * 6

lines=`cat $file | wc -l`
numOfFunctions=4
offset=`echo $startReadingLine + $numOfFunctions + 2 | bc`
lines=`echo $lines + $numOfFunctions | bc`

cp $file $outputFile
file=$outputFile

# When you add a new function, inc numOfFunctions 
echo "MEDIAN,\"=MEDIAN(B$offset:B$lines)\",\"=MEDIAN(C$offset:C$lines)\",\"=MEDIAN(D$offset:D$lines)\",\"=MEDIAN(E$offset:E$lines)\"," | cat - $file > $file.tmp ;
mv $file.tmp $file;
echo "MAX,\"=MAX(B$offset:B$lines)\",\"=MAX(C$offset:C$lines)\",\"=MAX(D$offset:D$lines)\",\"=MAX(E$offset:E$lines)\"," | cat - $file > $file.tmp ;
mv $file.tmp $file;
echo "AVERAGE,\"=AVERAGE(B$offset:B$lines)\",\"=AVERAGE(C$offset:C$lines)\",\"=AVERAGE(D$offset:D$lines)\",\"=AVERAGE(E$offset:E$lines)\"," | cat - $file > $file.tmp ;
mv $file.tmp $file;
echo "95 percentile,\"=PERCENTILE(B$offset:B$lines, 0.95)\",\"=PERCENTILE(C$offset:C$lines, 0.95)\",\"=PERCENTILE(D$offset:D$lines, 0.95)\",\"=PERCENTILE(E$offset:E$lines, 0.95)\"," | cat - $file > $file.tmp ;
mv $file.tmp $file;