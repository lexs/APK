import sys
from collections import defaultdict, namedtuple

import xml.etree.ElementTree as ET

schema = [
	'_id',
	'name',
	'name2',
	'price',
	'volume',
	'price_per_liter',
	'product_group',
	'packaging',
	'origin',
	'origin_country',
	'producer',
	'distributor',
	'year',
	'alcohol_percentage',
	'ingredients'
]

def string(value):
	# replace ' with '' and wrap in ''
	return '\''+value.replace('\'', '\'\'')+'\''

def percentage(value):
	value = value.replace('%', '')
	return float(value)/100

mappings = {
	'Artikelid': ('_id', int),
	'Namn': ('name', string),
	'Namn2': ('name2', string),
	'Prisinklmoms': ('price', float),
	'Volymiml': ('volume', float),
	'PrisPerLiter': ('price_per_liter', float),
	'Varugrupp': ('product_group', string),
	'Forpackning': ('packaging', string),
	'Ursprung': ('origin', string),
	'Ursprunglandnamn': ('origin_country', string),
	'Producent': ('producer', string),
	'Leverantor': ('distributor', string),
	'Argang': ('year', int),
	'Alkoholhalt': ('alcohol_percentage', percentage),
	'RavarorBeskrivning': ('ingredients', string)
}

def convert(input, output):
	tree = ET.parse(input)
	articles = tree.getiterator('artikel')
	for article in articles:
		values = get_values(article)
		statement = get_insert_statement(values)

		output.write(statement.encode('utf-8'))

	output.close()

def get_values(article):
	values = defaultdict(lambda: 'NULL')

	for element in article:
		if element.tag in mappings:
			(key, type) = mappings[element.tag]
			value = element.text

			if value:
				values[key] = type(value)

	return values

def calculate_apk(values):
	# volume in cl
	volume = values['volume']/10
	percentage = values['alcohol_percentage']
	price = values['price']

	if percentage == 0:
		return 'NULL'
	else:
		alcohol = (volume*percentage)/0.4
		return price/alcohol

def get_insert_statement(values):
	apk = calculate_apk(values)

	sorted = []

	for column in schema:
		value = values[column]
		sorted.append(unicode(value))

	# add apk
	sorted.append(str(apk))

	return 'INSERT INTO articles VALUES('+','.join(sorted)+');\n'

def convert_percentage(value):
	return value

if __name__ == "__main__":
	input = sys.stdin
	output = sys.stdout
	if len(sys.argv) >= 2:
		input = sys.argv[1]
		input = open(input)
	if len(sys.argv) == 3:
		output = sys.argv[2]
		f = open(output, 'w')

	convert(input, output)
