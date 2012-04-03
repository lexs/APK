import sys

from xmlconverter import convert, fromAttribute, computedBy

def stringify(value):
	# replace ' with '' and wrap in ''
	return '\''+value.replace('\'', '\'\'')+'\''

def percentage(value):
	value = value.replace('%', '')
	return float(value)/100

def calculate_apk(values):
	# volume in cl
	volume = values['volume']/10
	percentage = values['alcohol_percentage']
	price = values['price']

	if percentage == 0:
		return None
	else:
		alcohol = (volume*percentage)/0.4
		return price/alcohol


def filter(values):
	# filter items with no apk, meaning they
	# are non-alcoholic
	return not values['apk']

def create_sql_insert(values):
	sorted = []

	for column in schema:
		value = values[column]

		if type(value) is str or type(value) is unicode:
			value = stringify(value)

		if not value:
			value = 'NULL'

		sorted.append(unicode(value))

	return 'INSERT INTO articles VALUES('+','.join(sorted)+');\n'

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

mappings = {
	'_id': fromAttribute('Artikelid', int),
	'name': fromAttribute('Namn', unicode),
	'name2': fromAttribute('Namn2', unicode),
	'price': fromAttribute('Prisinklmoms', float),
	'volume': fromAttribute('Volymiml', float),
	'price_per_liter': fromAttribute('PrisPerLiter', float),
	'product_group': fromAttribute('Varugrupp', unicode),
	'packaging': fromAttribute('Forpackning', unicode),
	'origin': fromAttribute('Ursprung', unicode),
	'origin_country': fromAttribute('Ursprunglandnamn', unicode),
	'producer': fromAttribute('Producent', unicode),
	'distributor': fromAttribute('Leverantor', unicode),
	'year': fromAttribute('Argang', int),
	'alcohol_percentage': fromAttribute('Alkoholhalt', percentage),
	'ingredients': fromAttribute('ingredients', unicode),
	'apk': computedBy(calculate_apk)
}

if __name__ == "__main__":
	input = sys.stdin
	if len(sys.argv) == 2:
		input = open(sys.argv[1])

	convert(input, 'artikel', schema, mappings, output_func=create_sql_insert, filter=filter)
