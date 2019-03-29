---
layout: post
title:  "Finding Prime Numbers"
date:   2019-03-29 0:0:0
---

Prime numbers.

That's always my first project in a new language.
Normally, I use the `Sieve of Eratosthenes`.
This is a fun method that has no math more complex then addition,
but it has array manipluation, so it won't work in Gumboscript.

*You can find more information [here](https://en.wikipedia.org/wiki/Sieve_of_Eratosthenes) if you want*

Regardless, lets get starting.
We first need out base file, `prime.gs`.
Specify the compiler with
```
#compiler 0.3.0
```
And lets try and compile it.
```
$ gumbo -c -f prime.gs
```
No errors, that's good! Let's actually run it this time
```
$ gumbo -g -f prime.gs
```
You shouldn't get output, becuase we don't have any print statements.
Now, lets start!

### Step one - loop through all numbers

Loops are pretty simple, there are three main parts:
- Counter variable, to keep track of time. Normally `i`
- The Loop, something like `while i < 10`
- Incriminting the counter variable at the end of the loop. `i equals i + 1`

Using these three parts, we can make a simi-decent loop
```
var i equals 0
while i < 5
{
  print i
  i equals i + 1
}
```
Output:
```
0
1
2
3
4
```
As you can see, the loop is `0 to 4`, inclusive.
This is generally how things are done in programming,
because if you count the outputs, it's exactly `5`.

Lets add a `max` variable so we change change it easier
```
var max equals 10
while i < max
{
  print i
  i equals i + 1
}
```
Pretty simple.

Now, we need to figure out if a number is prime or not.
To do this, we can divide it by every number between 2 and the square root
of the number. Now, square root isn't the easiest thing to calculate with just
multiplication, so let's just try and find it with trial-and-error.

*(this code is inside the while loop above)*
```
var sqrt equals i / 2
while sqrt * sqrt > i
{
  sqrt equals sqrt - 1
}
sqrt equals sqrt + 1
```
We add one to sqrt just so we make sure we don't miss anything.

We can print out the `floor(sqrt(x)) + 1)` with this code:
```
var i equals 0
var max equals 10
while i < max
{
  var sqrt equals i / 2
  while sqrt * sqrt > i
  {
    sqrt equals sqrt - 1
  }
  sqrt equals sqrt + 1
  print "" + i + " : " + sqrt
  i equals i + 1
}
```
Which gives us
```
$ gumbo -f prime.gs
0 : 1
1 : 1
2 : 2
3 : 2
4 : 3
5 : 3
6 : 3
7 : 3
8 : 3
9 : 4
```
That's good enough for our purposes, lets move on to the real numbers

We want to divide `i` by every number between `2` and `sqrt`.
If if divides evenly, it's not prime.
```
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
```
We use a boolean to keep track of whether or not it's prime.


It works really well, actually
```
$ gumbo -f prime.gs
2:true
3:true
4:false
5:true
6:false
7:true
8:false
9:false
```
You can move this up to higher numbers by increasing `max`.
The highest I've gotten under a minute (on my chromebook) is
`10000`, which isn't very good.


Anyway, here's the final file:
```
#compiler 0.3.0
var i equals 2
var max equals 10000

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
```
Not the hardest thing in the world, is it?
