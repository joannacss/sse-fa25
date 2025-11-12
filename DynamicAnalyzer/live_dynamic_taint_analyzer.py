#!/usr/bin/python
# -*- coding: utf8 -*-
import ast
import inspect
import linecache
import re
import sys

from examples.shell_example import execute_cmd

SINK_REGEX = re.compile(r"[ \t]+os\.system\((.*)\)")

class TaintAnalyzer:

    def __init__(self):
        # TODO: initialize the analyzer
        self.tainted_variables = set()
        self.inputs = []
        self.vulnerable_paths = []



    def mark_as_tainted(self, var):
        # TODO: mark a specific variable as tainted
        self.tainted_variables.add(id(var))




    def generate_inputs(self):
        # TODO: generates inputs (hardcoded)
        input1 = "ls"
        input2 = "echo 'This is vulnerable'"
        self.inputs.append(input1)
        self.inputs.append(input2)
        # TODO: mark generated inputs as tainted
        self.mark_as_tainted(input1)
        self.mark_as_tainted(input2)



    def check_taint(self, frame, event, arg, code_line):
        # TODO: checks whether a tainted variable reached a sink!
        if event == "return":
            m = SINK_REGEX.match(code_line)
            if m:
                var_name = m.group(1)
                var_id = id(frame.f_locals[var_name])
                if var_id in self.tainted_variables:
                    self.vulnerable_paths.append(self.current_path)

    def run(self):
        def tracer(frame, event, arg):
            # TODO: implement the tracer function
            function_code = frame.f_code
            function_name = function_code.co_name
            lineno = frame.f_lineno
            executed_line = linecache.getline(function_code.co_filename, lineno)
            executed_line = executed_line.rstrip()
            self.current_path.append(executed_line)
            print(executed_line)
            self.check_taint(frame, event, arg,executed_line)
            return tracer


        # TODO: generate inputs
        self.generate_inputs()
        for input in self.inputs:
            self.current_path = []
            sys.settrace(tracer)
            execute_cmd(input)
            sys.settrace(None)


        # TODO: return the vulnerable paths
        return self.vulnerable_paths




if __name__ == '__main__':
    analyzer = TaintAnalyzer()
    vuln_paths = analyzer.run()
    if len(vuln_paths) > 0:
        print("\033[95m❌ Vulnerable:\033[00m")
        for i in range(len(vuln_paths)):
            print(f"\033[91m\tPath {i + 1}\033[00m")
            for line in vuln_paths[i]:
                print(f"\033[94m\t\t» {line}\033[00m")
    else:
        print("\033[92m✅ Not vulnerable\033[00m")