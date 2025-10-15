import os

def execute_cmd(cmd):
	a_variable = cmd
	if a_variable.startswith("ls"):
		a_variable = "echo 'hardcoded safe string'" 

	os.system(a_variable)


