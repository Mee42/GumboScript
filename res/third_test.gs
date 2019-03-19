#compiler 0.2.0

//auto type setting at compile time
var hello equals "H"

//works on expressions as well
var x equals 1 + 5 + 19

//variables can be reset
hello equals :[hello] + "e"
hello equals :[hello] + "ll"
hello equals :[hello] + "o"

var world equals "World"

print :[hello] + ", " + :[world] + "!"
