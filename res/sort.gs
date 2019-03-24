#compiler 0.2.0


varr arr boolean

//this sets everything to true
var max equals 100
var current equals 0

setgoto start1
    ::[arr: :[current]] equals true

    //print ::[arr:(:[current])]

    current equals :[current] + 1

goto start1 !(:[current] == :[max])


//set 0 and 1 to true
::[arr:0] equals true
::[arr:1] equals true

//loop through
var nA equals 1
var nB equals 0
var tmpA equals false

setgoto loop1
    nA equals :[nA] + 1
    goto loop1 !::[arr:(:[nA])]
//    print "Asserting "  + :[nA] + " as true"


    nB equals :[nA] * 2

    setgoto loop2
        ::[arr:(:[nB])] equals false
//        print "Setting " + :[nB] + " to false"
        nB equals :[nB] + :[nA]
    goto loop2 :[nB] < :[max]
 //   print "end of loop A"
goto loop1 :[nA] < (:[max]/2)

var cur equals 0
setgoto print
//    print "" + :[cur] + " " + ::[arr::[cur]]

    goto else !::[arr::[cur]]
        print :[cur]
    setgoto else

    cur equals :[cur] + 1
goto print :[cur] < :[max]

exit 0
