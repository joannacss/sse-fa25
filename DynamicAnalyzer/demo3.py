from examples.triangle import triangle
import sys
import inspect
from random import randrange


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

	visited_lines.add(lineno)
	# returns the function itself to track the new scope
	return analyze
    
    

def main():
	
	num_attempts = 0
	threshold = 200
	while True:
		a = randrange(1, 5)
		b = randrange(1, 5)
		c = randrange(1, 5)
		# start tracing the invocation
		print("START")
		sys.settrace(analyze)
		output = triangle(a, b, c)
		# stop tracing the invocation
		sys.settrace(None)
		print("END")
		if len(visited_lines) == total_lines-4:
			print("100% coverage!")
			break
		num_attempts += 1
		if num_attempts > threshold:
			break

	print("Output at main()", output)
    

if __name__ == '__main__':
	total_lines = len(inspect.getsourcelines(triangle)[0])
	visited_lines = set()
	main()