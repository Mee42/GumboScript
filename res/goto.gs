#compiler 0.2.0

var one equals 1
print 1
var two equals 1
print 1
var three equals 0 - 1

var count equals 1


setgoto start

three equals :[one] + :[two]
one equals :[two]
two equals :[three]
print :[two]
count equals :[count] + 1

goto start !(:[count] == 45)

exit 0