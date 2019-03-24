#compiler 0.2.0

varr arr int
var size equals 6
::[arr:0] equals 0
::[arr:1] equals 2
::[arr:2] equals 1
::[arr:3] equals 3
::[arr:4] equals 4
::[arr:5] equals 5

var index equals 0
var temp equals 0 - 1
var temp2 equals 0 - 1


setgoto loop3
    print "" + :[index] + ":" + ::[arr::[index]]
    index equals :[index] + 1
goto loop3 :[index] < :[size]

index equals 0


setgoto loop1
    temp equals ::[arr::[index]]
    temp2 equals ::[arr::[index] + 1]
    print "index:" + :[index] + " temp:" + :[temp] + " temp2:" + :[temp2]
    print ::[arr:0]+":"+::[arr:1]+":"+::[arr:2]+":"+::[arr:3]+":"+::[arr:4]+":"+::[arr:5]
    goto x !(:[temp] > :[temp2])
        print "swapping"
        ::[arr::[index]] equals :[temp2]
        ::[arr::[index] + 1] equals :[temp]
        print ::[arr:0]+":"+::[arr:1]+":"+::[arr:2]+":"+::[arr:3]+":"+::[arr:4]+":"+::[arr:5]
    setgoto x
    
    index equals :[index] + 1
goto loop1 (:[index] + 1) < :[size]


index equals 0
setgoto loop2
    print "" + :[index] + ":" + ::[arr::[index]]
    index equals :[index] + 1
goto loop2 :[index] < :[size]


exit 0