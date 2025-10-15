# Code based on the code from the FuzzingBook.org
import ast
import inspect
import z3
from examples.twice import test


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
		if isinstance(node, ast.Call):
			# the actual dispatch
			pass
		elif isinstance(node, ast.If):
			cond = ast.unparse(node.test).strip()
			not_cond = f"({cond}) == False" #TODO: show alternative with Z3 syntax
			traverse_if_children(node.body, context, cond)
			traverse_if_children(node.orelse, context, not_cond)
		else:
			for child in ast.iter_child_nodes(node):
				traverse(child, context)

	traverse(tree, [])
	constraints = []
	for i in range(len(paths)):
		path_constraints = ",".join(paths[i])
		constraints.append(f"z3.And({path_constraints})")
	return constraints


		#TODO: replace with Z3 syntax


def main():
	function_source = inspect.getsource(test)
	function_ast = ast.parse(function_source)
	paths = collect_conditions(function_ast)

	for i in range(len(paths)):
		print(f"Path {i + 1}", paths[i])
		# TODO: use z3 solver to find inputs that satisfy the path condition
		s = z3.Solver()
		x = z3.Int("x")
		y = z3.Int("y")
		z = z3.Int("z")

		s.add(eval(paths[i]))
		if s.check() == z3.sat:
			print(s.model())
		else:
			print("Unsatisfiable!")



if __name__ == '__main__':
	main()
