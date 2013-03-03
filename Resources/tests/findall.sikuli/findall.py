# this should run on https://launchpad.net/sikuli

top = "1352556855570.png"

# define a search region
r = find(top).below()
r.highlight(2)

img = "1352556890908.png"
# get the most probable matches
matches = list((m for m in list(r.findAll(img)) if m.getScore()>0.95))

# sort top down (y value)
matches.sort(key = lambda m : m.y)

# show them
for m in matches:
    m.highlight(1)