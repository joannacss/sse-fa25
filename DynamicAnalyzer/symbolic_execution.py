# Code based on the code from the FuzzingBook.org
import inspect
import ast 
from examples.twice import test
from examples.triangle import triangle


def collect_conditions(tree):
	paths = []

	def traverse_if_children(children, context, cond):
		previous_len = len(paths)
		for child in children:
			traverse(child, context + [cond])
		if len(paths) == previous_len:
			paths.append(context + [cond])

	def traverse(node, context):
		# method call, loops, ...		
		if isinstance(node, ast.If):
			cond = ast.unparse(node.test).strip()
			not_cond = f"({cond}) == False"
			traverse_if_children(node.body, context, cond)
			traverse_if_children(node.orelse, context, not_cond)
		else:
			for child in ast.iter_child_nodes(node):
				traverse(child, context)

	traverse(tree, [])
	
	return paths


def main():
	function_source = inspect.getsource(test)
	function_ast = ast.parse(function_source)
	paths = collect_conditions(function_ast)
	
	for i in range(len(paths)):
		print(f"Path {i+1}", paths[i])


if __name__ == '__main__':
	main()