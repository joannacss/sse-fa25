from random import randint

cmd = input()

if randint(1, 100) % 2 == 0: 
	x = cmd
else: 
	x = "print('else')"
	
eval(x)
