import z3

# Create symbols
x0 = z3.Int('x0')
y0 = z3.Int('y0')

# instantiates the solver
solver  = z3.Solver()

# Creates the constraints: (x0 = 2y0 ⋀ x0 > y0 + 10)
c1 = x0 == 2*y0
c2 = x0 > y0 + 10
c3 = z3.And(c1, c2, z3.Not(c1))

# attempt to solve the constraint and print the solution if satisfiable
solver.add(c3)
if solver.check() == z3.sat:
    print(solver.model())
else:
    print("UNSATISFIABLE")


