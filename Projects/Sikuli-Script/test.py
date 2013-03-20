sg = r"C:\Users\Raimund Hocke\Documents\GitHub\Sikuli12.11\Projects" + \
        r"\Sikuli-Guide\dist\Sikuli-Guide.jar"
load(sg)
import guide as gd

r=Region(0+0, 0, 300, 300)
p1 = r.getCenter()
r1 = p1.grow(100)
hover(p1)
r1.highlight(2)

gd.circle(r1)
r2 = gd.rectangle(r1.grow(50))
#gd.button(r1, "ClickMe")
#gd.button(r2, "ClickMe1", side="inside")
#print gd.show(2)