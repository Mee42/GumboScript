//push all correct values to the stack
//72 101 108 108 111 32     119 111 114 108 100
push 72;
push 101;
push 108;
push 108;
push 111;
push 32;
push 119;
push 111;
push 114;
push 108;
push 100;
push 0;
reverse;


pgoto loop;
pgoto end;

setgoto loop;

arr 1;

dupl;
gotoif end
goto loop;

setgoto end;

printarr 1;
exit 0;