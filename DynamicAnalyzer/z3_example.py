import z3  

# Create symbols
x0 = z3.Int('x0')
y0 = z3.Int('y0')

# instantiates the solver
s = z3.Solver()

# Creates the constraints
# (x0 = 2y0 ⋀ x0 > y0 + 10)
c1 = x0 == 2*y0
c2 = x0 > y0 + 10
c3 = x0 < 0

s.add(z3.And(c1, c2, c3))
# attempt to solve the constraint
if s.check() == z3.sat:
   #  prints out solution
   print(s.model())
else:
   print("Unsatisfiable!")


