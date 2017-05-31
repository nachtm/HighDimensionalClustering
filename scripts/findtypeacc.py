#Get the single-type similarity for each cluster in a cluster log file. Print out information about said clusters, generally sorted by 
#the similarity, though this is a pretty flexible file. 

import sys

#NOTE THAT THIS FILE USES eval() on a text file input, which is UNSAFE!

types = [{'GK'}, {'CB','RCB','LCB','CDM','RB','LB'}, {'CDM','CM','CAM','RM','LM'},
        {'CF','RW','LW','RF','LF','RS','LS','ST'}]
typenames = ['Goal', 'Defense', 'Midfield', 'Offense']

def counts(data):
    count = {}
    players = data[1:-2].split(",")
    for player in players:
        pos = player[-3:].strip()
        try:
            count[pos] += 1
        except KeyError:
            count[pos] = 1
    return count 

def combinecounts(poscounts):
    ans = {}
    for pos in poscounts.keys():
        added = False
        for i in range(len(types)):
            if pos in types[i]:
                try:
                    ans[typenames[i]] += poscounts[pos]
                except KeyError:
                    ans[typenames[i]] = poscounts[pos]
                added = True
    return ans


def getacc(typecounts):
    assert len(typecounts) != 0
    s = 0
    maxcount = -1
    maxtype = None
    for t in typecounts.keys():
        if maxcount < typecounts[t]:
            maxcount = typecounts[t]
            maxtype = t
        s += typecounts[t]
    return (float(maxcount)/s, maxtype)
    
with open(sys.argv[1]) as f:
    fit = iter(f.readlines())
    output = []
    try:
        while True:
            info = next(fit)
            space = info[:info.index("}")+1]
            contents = next(fit)
#            print(counts(contents))
            c = combinecounts(counts(contents))
            acc, t = getacc(c)
            output.append((acc, t, c, space))
    except StopIteration:
        pass
    
    inacc = 0
    accur = 0
    threshold = float(sys.argv[3])
    minSpace = int(sys.argv[2])
    
    for point in output:
        spaceSet = eval(point[3])
        if len(spaceSet) == minSpace:
#            print('{} {}'.format(point[3], len(spaceSet)))
#            print point[0]
#            print point[2]
#            print point[3]
#            print '-----------'
            if point[0] >= threshold:
                accur += 1
            else:
                inacc += 1
    print(float(accur)/(accur + inacc))
#    for point in sorted(output):
#        print(point[0])
#        print(point[1])
#        print(point[2])
#        print(point[3])
#        print('------------')