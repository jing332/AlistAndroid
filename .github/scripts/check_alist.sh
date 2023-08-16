#!/bin/bash

function to_int() {
    echo $(echo "$1" | grep -oE '[0-9]+' | tr -d '\n')
}

LATEST_VER=$(curl -s https://api.github.com/repos/alist-org/alist/releases/latest | grep -o '"tag_name": ".*"' | cut -d'"' -f4)
LATEST_VER_INT=$(to_int $LATEST_VER)
echo "Latest AList version $LATEST_VER ${LATEST_VER_INT}"
echo "alist_version=$LATEST_VER" >> $GITHUB_ENV

# VERSION_FILE="$GITHUB_WORKSPACE/alist_version.txt"

VER=$(cat $VERSION_FILE)
if [ -z $VER ]; then
  VER="v3.25.1"
fi

VER_INT=$(to_int $VER)

echo "Current AList version: $VER ${VER_INT}"


if [ $VER_INT -ge $LATEST_VER_INT ]; then
    echo "Current >= Latest"
    echo "alist_update=false" >> $GITHUB_ENV
else
    echo "Current < Latest"
    echo "alist_update=true" >> $GITHUB_ENV
fi
