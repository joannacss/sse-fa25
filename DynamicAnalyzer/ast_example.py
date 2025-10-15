import ast
from examples.twice import test
import inspect


source = inspect.getsource(test)
test_ast = ast.parse(source)

print(ast.dump(test_ast,indent=2))
