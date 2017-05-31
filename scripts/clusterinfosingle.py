#Parses a clustering output file in order to get the information in it
#Prints the space, single-position similarity and most common position to the console if similarity
#is above some threshold, sorted by the space.

import sys

filename = sys.argv[1]

with open(filename) as f:
    lines = f.readlines()
    spaces = []
    try:
        it = iter(lines)
        line = 0
        while True:
            info = next(it)
            content = next(it)
            line += 2
            space = info[:info.index("}")+1]
            acc = float(info[info.index("}")+1:info.index("\t")])
            pos = info[info.index("\t")+1:]
            if acc > .9:
                spaces.append((space, acc, pos))
    except StopIteration:
        print("Done!")
        
for space in sorted(spaces):
#    print("{}: {}".format(space[0][:-1], space[1]))
    print(space[0])
    print(space[1])
    print(space[2])
    
print(len(spaces))