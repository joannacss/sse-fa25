import re
import time
regex = r"^(a+)+$"


for n in range(4, 32, 2):
	start = time.time()
	re.match(regex, "a"*n+"X") # 2^n possible paths	
	print(n,time.time()-start, sep="\t")

# re.match(regex, "a"*4+"X") # 2^4 possible paths
# re.match(regex, "a"*16+"X") # 2^16 possible paths
# re.match(regex, "a"*32+"X") # 2^32 possible paths