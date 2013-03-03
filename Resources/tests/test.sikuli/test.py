#Debug.setDebugLevel(0)
#exit()

r = Region(100+0,100,100,100)
r1 = Region(r.grow(200))
#r1 = r.grow(200)
r1.highlight(2)
r2 = r1.right()

"""
f = Finder(capture(SCREEN))
f.find(Pattern("1360601282378.png").similar(0.90))
#f.find("1360601282378.png")
if f.hasNext():
  m = f.next()
  print m
  Region(m).highlight(2)
else:
  print "not found"
"""
#exit()

def handler(e):
    if e.match: e.match.highlight(2)
    return

#for r in (r1, r1):
r1.onAppear(Pattern("1360601282378.png").similar(0.90), handler)
#r1.onAppear("1360601282378.png", handler)
r1.observe(3)

"""
setFindFailedResponse(PROMPT)
find("1351078082205.png")
Debug.user("Higlighting: %s", highlight(-2).getImageFilename())
"""

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

