from lxml import etree
parser = etree.XMLParser() 
tree1 = etree.parse('resources/xxe.xml', parser)
root1 = tree1.getroot()
for r in root1:
	print(r.text)