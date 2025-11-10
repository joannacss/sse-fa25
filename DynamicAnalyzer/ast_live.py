# TODO: import required modules
import ast
import inspect
from examples.twice import test
# TODO: get source code (use inspect module)
test_sourcecode = inspect.getsource(test)

# TODO: get source code's AST
test_ast = ast.parse(test_sourcecode)

# TODO: print the AST of the code
print(ast.dump(test_ast, indent=2))

