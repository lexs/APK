import sys
from collections import defaultdict

import xml.etree.ElementTree as ET

def fromAttribute(attr, type):
	def wrapper(attributes, values):
		value = attributes[attr]
		return type(value) if value else None

	return wrapper;

def computedBy(func):
	def wrapper(attributes, values):
		return func(values)

	return wrapper

def convert(input, tag_name, schema, mappings, output_func, filter=None):
	for event, elem in ET.iterparse(input):
		if elem.tag == tag_name:
			attributes = get_attributes(elem)
			values = defaultdict(lambda: None)

			for key in schema:
				func = mappings[key]
				values[key] = func(attributes, values)

			if not filter or not filter(values):
				output = output_func(values)
				sys.stdout.write(output.encode('utf-8'))

def get_attributes(parent):
	attributes = defaultdict(lambda: None)

	for element in parent:
		attributes[element.tag] = element.text

	return attributes

