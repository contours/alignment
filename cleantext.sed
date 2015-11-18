#! /usr/bin/sed -f

# Change single quotes to double quotes.
s/ '\([^']\{1,\}\)' / "\1" /g

# Space out letters in dotted acronyms.
s/\([A-Z]\)\.\([A-Z]\)/\1. \2/g

# Change two or more dashes between letters and numbers to a comma.
s/\([a-zA-Z0-9]\)\(\-\{2,\}\)\([a-zA-Z0-9]\)/\1, \3/g

# Remove two or more dashes before punctuation.
s/\-\{2,\}\([^a-zA-Z0-9]\)/\1/g

# Remove two or more dashes after punctuation.
s/\([^a-zA-Z0-9]\)\-\{2,\}/\1/g

# Changes slashes to commas.
s/\([^ ]\)\/\([^ ]\)/\1, \2/g

# Remove commas inside numbers.
s/\([0-9]\),\([0-9]\)/\1\2/g
