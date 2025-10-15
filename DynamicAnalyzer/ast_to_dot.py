import inspect
import ast
from examples.twice import test


def visualize_ast(tree):
	# CODE FROM: https://earthly.dev/blog/python-ast/
	from graphviz import Digraph

	# Create a Graphviz Digraph object
	dot = Digraph()

	# Define a function to recursively add nodes to the Digraph
	def add_node(node, parent=None):
	    node_name = str(node.__class__.__name__)
	    dot.node(str(id(node)), node_name)
	    if parent:
	        dot.edge(str(id(parent)), str(id(node)))
	    for child in ast.iter_child_nodes(node):
	        add_node(child, node)

	# Add nodes to the Digraph
	add_node(tree)

	# Render the Digraph as a PNG file
	dot.format = 'png'
	dot.render('test', view=True)


def main():
	test_source = inspect.getsource(test)
	test_ast = ast.parse(test_source)
	visualize_ast(test_ast)
	print(ast.dump(test_ast, indent=2))




if __name__ == '__main__':
	main()