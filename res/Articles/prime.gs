#compiler 0.3.0
var i equals 2
var max equals 10

while i < max
{
  var sqrt equals i / 2
  while sqrt * sqrt > i
  {
    sqrt equals sqrt - 1
  }
  sqrt equals sqrt + 1

  var n equals 2
  var prime equals true
  while n < sqrt
  {
    if i % n == 0
    {
      prime equals false
    }
    n equals n + 1
  }
  print "" + i + ":" + prime
  i equals i + 1
}