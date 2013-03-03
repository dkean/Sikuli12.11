#load("SikuliGuide")
#dir = "/Users/rhocke/Library/Application Support/Sikuli/extensions/guide.jar"
#if not dir in sys.path: sys.path.append(dir)
for e in sys.path: print e
import guide
m = find("1353321906110.png").highlight(2)
guide.text(m, "Hallo")
guide.show(1)

