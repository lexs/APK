import sys
import fileinput
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
	'ingredients',
	'apk'
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
	for event, elem in ET.iterparse(input):
		if elem.tag == 'artikel':
			values = get_values(elem)

			if not filter(values):
				statement = get_insert_statement(values)
				output.write(statement.encode('utf-8'))

	if output is not sys.stdout:
		output.close()

def get_values(article):
	values = defaultdict(lambda: 'NULL')

	for element in article:
		if element.tag in mappings:
			(key, type) = mappings[element.tag]
			value = element.text

			if value:
				values[key] = type(value)

	values['apk'] = calculate_apk(values)

	return values

def filter(values):
	# filter items with no apk, meaning they
	# are non-alcoholic
	return values['apk'] == 'NULL'

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
	sorted = []

	for column in schema:
		value = values[column]
		sorted.append(unicode(value))

	return 'INSERT INTO articles VALUES('+','.join(sorted)+');\n'

if __name__ == "__main__":
	input = sys.stdin
	if len(sys.argv) == 2:
		input = open(sys.argv[1])

	convert(input, sys.stdout)
