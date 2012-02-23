#!/bin/bash

# Writes a property file to current directory without unused property keys.

# Run this program from root of a web project after cleaning target dir.

# First argument is the properties file to investigate.

# Requires a line-break after the last line in property file to investigate.

if [[ $# -lt 1 ]]; then
  echo "Usage: $0 properties-file"
  exit 1
fi

PROP_FILE="cleaned.properties"
FILENAME=$1
IGNORED_PROPS=0
RETAINED_PROPS=0
INVALID_LINES=0
TOTAL_LINES=0

rm -f ${PROP_FILE}
touch ${PROP_FILE}

echo "Keys not in use in file: $1"
echo

while read -r LINE
do
  if [[ ${LINE} =~ (^.*)(=| =).* ]]; then
    KEY=${BASH_REMATCH[1]}    
    MATCH=`find . -name "*.java" -o -name "*.vm" -o -name "*.js" -o -name "*.html" | xargs grep $KEY`
    LENGTH_OF_MATCH=${#MATCH}
    if [[ ${LENGTH_OF_MATCH} == 0 ]] && [[ ${KEY} != intro_* ]]; then
      echo "Ignoring unused key: ${KEY}"
      let IGNORED_PROPS++
    else
      echo ${LINE} >> ${PROP_FILE}
      let RETAINED_PROPS++
    fi
  else
    echo "Line has no valid property key: ${LINE}"
    let INVALID_LINES++
  fi
  let TOTAL_LINES++
done < ${FILENAME}

echo "" >> ${PROP_FILE}

echo "- Done"
echo "- Ignored ${IGNORED_PROPS} unused keys and ${INVALID_LINES} invalid lines, retained ${RETAINED_PROPS} properties, looked at ${TOTAL_LINES} lines"
echo "- Cleaned properties written to file ${PROP_FILE}"
