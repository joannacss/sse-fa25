import re


random_string = "-"*900000000 + "@-"
regex = r".+@.+"
re.match(regex, random_string)


