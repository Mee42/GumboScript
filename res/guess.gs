#compiler 0.1.0
#include math
#include extra
#include io

print Guess a number between 1 and 100
print Don't tell me

push 1
var::popvar left
push 100
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

    print is your number :POP (yes:true, no:false)?
    inputbool
    if
        print I guessed it!
        exit
    fi
    print is your number higher or lower (higher:true, lower:false)?
    inputbool
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

    var::pushvar right
    var::pushvar left
    math::equalto
    if
        var::pushvar right
        print It's :POP
        exit
    fi


    push :SIZE
    debug::asserttrue 0
    pop

    push 0
stoploop main