from examples.triangle import triangle
import sys
import inspect

def analyze(frame, event, arg):
	# get function code
	function_code = frame.f_code

	# get function name
	function_name = function_code.co_name

	# get line number
	lineno = frame.f_lineno
	
	# TODO: get all source lines
	source_lines, start  = inspect.getsourcelines(function_code)
	statement = source_lines[lineno - start].strip()
	
	# TODO: get all variables and print their values
	variables = []
	for v in frame.f_locals:
		variables.append(f"{v}={frame.f_locals[v]}")
	variables = ",".join(variables)


	# TODO: prints them f"{event} --> {function_name}:{lineno} {statement} ({variables})"
	print(f"{event} --> {function_name}:{lineno} {statement} ({variables})")

	# returns the function itself to track the new scope
	return analyze
    
    

def main():
	# start tracing the invocation
	sys.settrace(analyze)
	output = triangle(2, 2, 1)
	# stop tracing the invocation
	sys.settrace(None)
	print("Output at main()", output)
    

if __name__ == '__main__':
	main()