#!/bin/bash

XML_FILE="http://www.systembolaget.se/Assortment.aspx?Format=Xml"

DB_NAME="articles.db"

# Make sure file is empty
cat /dev/null > $DB_NAME

sqlite3 $DB_NAME ".read schema.sql"
curl $XML_FILE | python systemet_to_sql.py | sqlite3 $DB_NAME

# Create indexes
sqlite3 $DB_NAME ".read indexes.sql"