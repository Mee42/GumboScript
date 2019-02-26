pgoto a;
pgoto b;

push 0
//0
setgoto a
    push 1
    //0 1
    add
    //1
    dupl
    //1 1
    push 0
    //1 1 0
    //starting value
    swap
    //1 0 1
    //puts the number 0 in the map at index of the new
    map :POP
    //0 = 1
    //1

    dupl
    //1 1
    push 1000
    //1 1 100
    swap
    //1 100 1

    sub
    //99
gotoif b
goto a
setgoto b

pgoto endConsume

setgoto startConsume
consume :POP
push :SIZE
gotoif endConsume
goto startConsume
setgoto endConsume



pgoto c;pgoto d;

pgoto else
pgoto if

push 1
setgoto c;
    push 1
    add

    push :[:PEEK]

    gotoif if
    goto else
    setgoto if
    dupl
    println :POP
    //newline
    //X
    dupl
    dupl
    mult
    //X I(X+X)
    setgoto smallLoopSet

        strbuff setting
        buff 32
        strbuff :PEEK
        buff 32
        strbuff to 1
        clearbuff


        dupl
        //X I I
        map -1

        //X I
        push 1
        //X I 1
        map :[-1]
        //map I to 1
        //X I
        map -1
        //X
        dupl
        //X X
        push:[-1]
        //X X I

        add
        dupl
        //X new_I new_I
        //check to break
        push 1001
        //X I I 100
        swap
        //X I 100 I

        sub
        //X I 100-I

//        debug
        pgoto elseS
        gotoif elseS

        //X
        //goes if (100-I) <= 0
        goto smallLoopSet
    setgoto elseS
        consume :POP

    setgoto else
    //X I

    //X
    dupl
    //X X
    push 1000
    swap
    //X 100 X
    sub

    //X (100_X) <= x
gotoif d
goto c
setgoto d


exit 0