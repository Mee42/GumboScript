#compiler 0.1.0
#include debug
#include extra




push 10
push 10
math::equalto
asserttrue 0
pop

push 10
push 9
math::equalto
asserttrue 1
pop

push 9
push 10
math::equalto
asserttrue 1
pop

push 10
push 10
math::lessthen
asserttrue 1
pop


push 9
push 10
math::lessthen
asserttrue 1
pop

push 10
push 9
math::lessthen
asserttrue 0
pop


push 10
push 10
math::greaterthen
asserttrue 1
pop

push 9
push 10
math::greaterthen
asserttrue 0
pop

push 10
push 9
math::greaterthen
asserttrue 1
pop


push 0
push 0
math::and
asserttrue 0
pop

push 1
push 0
math::and
asserttrue 1
pop

push 1
push 1
math::and
asserttrue 1
pop

push 0
push 0
math::or
asserttrue 0
pop


push 1
push 0
math::or
asserttrue 0
pop

push 1
push 1
math::or
asserttrue 1
pop


push 0
push 0
math::xor
asserttrue 1
pop

push 1
push 0
math::xor
asserttrue 0
pop

push 1
push 1
math::xor
asserttrue 1
pop

push 0
push :SIZE
asserttrue 1
pop
pop



//the stack needs to be empty
push :SIZE
asserttrue 0
pop

assertdone
exit
