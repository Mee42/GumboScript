#compiler 0.3.0

var current equals 2

while current < 100;{
    var isPrime equals true
    var divider equals 2
    while divider <= current / 2;{
        if current % divider == 0;{;isPrime equals false;};
        divider equals divider + 1
    }
    if isPrime;{;print current;};
    current equals current + 1
}
