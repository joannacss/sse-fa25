from z3 import String, PrefixOf, Solver
from z3 import sat

def main():
    # Define a symbolic string variable
    s = String('s')

    # Initialize the Z3 solver
    solver = Solver()

    # Add a constraint that the string must start with "ls"
    solver.add(PrefixOf("ls", s))

    # Check for satisfiability
    if solver.check() == sat:
        model = solver.model()
        print(f"Found a string: {model[s]}")
    else:
        print("No string satisfies the constraints")


if __name__ == "__main__":
    main()
