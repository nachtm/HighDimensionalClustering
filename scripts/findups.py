#given the output from clusterinfosingle.py in a text file, print out the spaces that appear
#more than once in that file.

import sys

with open(sys.argv[1]) as f:
    lines = f.readlines()
    last = ''
    dups = []
    for i in range(len(lines)):
        if lines[i][0] == '{':
            print(lines[i])
            if lines[i] == last:
                dups.append(lines[i][:-1])  
            else:
                last = lines[i]
                
for dup in dups:
    print(dup)

        