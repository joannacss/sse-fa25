from lxml import etree

print("NON-COMPLIANT")
parser = etree.XMLParser(resolve_entities=True) 
tree1 = etree.parse('resources/xxe.xml', parser)
root1 = tree1.getroot()
print(root1[0].text)




print("COMPLIANT")
parser = etree.XMLParser(resolve_entities=False, no_network=True) # Compliant
tree1 = etree.parse('resources/xxe.xml', parser)
root1 = tree1.getroot()
print(root1[0].text)