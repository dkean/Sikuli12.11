print SCREEN
setROI(0,0,200,200)
print SCREEN
find("1351078082205.png")
hover(highlight(-2))
exit()
"""
print "----- sys.argv - length:", len(sys.argv)
for e in sys.argv: print e
"""
print "----- this is from test now"
def listp():
    print "----- ImagePath"
    pl = getImagePath()
    for i in range(len(pl)): print i, pl[i]
def lists():
    print "----- sys.path"
    for i in range(len(sys.path)):
        print i, sys.path[i]
addImagePath("dist:build"); listp()
#removeImagePath("dist"); listp()
resetImagePath("nbproject"); listp()

setBundlePath("../RaiManSikuli2012-Script/test.sikuli"); listp()

import sub
#reload(sub)
lists()
listp()
sub.hello()
resetImagePath(""); listp()

exit()

m = find(capture(20,30,100,100))
print "m:", m
r = Region(m.right(200))
print "r:", r
print "+++ 1st with"
with r:
    checkWith()
    highlight(2)
checkWith()
print "+++ 2nd with"
with r:
    checkWith()
    highlight(2)
    hover(r)
checkWith()
print "Bye for now -----"
#exit(1)

