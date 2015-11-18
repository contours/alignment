#! /usr/bin/sed -f

# Ensure that there is a space after a series of dashes.
s/\([^ ]\)\(\-\{2,\}\)\([a-zA-Z0-9]\)/\1\2 \3/g

# Changes slashes to commas.
s/\([^ ]\)\/\([^ ]\)/\1, \2/g

# Remove commas inside numbers.
s/\([0-9]\),\([0-9]\)/\1\2/g
