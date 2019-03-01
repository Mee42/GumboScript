#compiler 0.1.0
#include math
#include extra


print Guess a number between 1 and 10
print Don't tell me

push 1
var::popvar left
push 10
var::popvar right

push 0
startloop main
    var::pushvar left
    var::pushvar right
    add
    push 2
    swap
    div
    dupl
    var::popvar mid

    print is your number :POP (yes:0, no:1)?
    input
    if
        print I guessed it!
        exit
    fi
    print is your number higher or lower (higher:0, lower 1)?
    input
    dupl
    if
      var::pushvar mid
      push 1
      add
      var::popvar left
    fi
    flip
    if
      var::pushvar mid
      push -1
      add
      var::popvar right
    fi


    push :SIZE
    flip
    if
        print size is not zero
        debug::stack
    fi
    push 0
stoploop main