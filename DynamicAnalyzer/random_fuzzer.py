from examples.triangle import triangle
import sys
import inspect
import random

seen_lines = set()
def get_code(function_code, lineno) -> str:
    # 0...N
    source_lines, start_index = inspect.getsourcelines(function_code)
    return source_lines[lineno - start_index].strip()


def analyze(frame, event, arg):
    # function name?
    function_code = frame.f_code
    function_name = function_code.co_name
    # line number?
    lineno = frame.f_lineno
    code_line = get_code(function_code, lineno)
    local_variables = frame.f_locals
    variable_values = []
    for v in local_variables:
        variable_values.append(f"{v} = {local_variables[v]}")
    seen_lines.add(lineno)
    print(f"{function_name}:{lineno} ({event})")
    print(f"\t{code_line}")
    print(f"\t\t{variable_values}")

    return analyze


def main():

    # generate inputs: hint: use the random module
    # generate input iteratively, until all of triangle's code lines are executed
    # E-mail subject: Lecture 16 - Extra Credit (2PM)
    triangle(2, 2, 1)
    while len(seen_lines) < 10:
        a = random.randint(1, 11)
        b = random.randint(1, 11)
        c = random.randint(1, 11)
        sys.settrace(analyze)
        triangle(a, b, c)
        sys.settrace(None)
    print(seen_lines)
    print("DONE")


if __name__ == '__main__':
    main()