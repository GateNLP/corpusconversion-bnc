#!/bin/bash

inDir="$1"
outDir="$2"
if [ "x${outDir}" == "x" ]
then
  echo 'Usage: ./convert.sh inDir outDir' 
  exit 1
fi

rm -rf ./tmp-in
mkdir ./tmp-in
find "${inDir}"/download/Texts/ -name '*.xml' | xargs -n 1 -I '{}' cp '{}' tmp-in/

mkdir "${outDir}" 
rm "${outDir}/*"

# Would be nice to store in FastInfoset but the document HHV will trigger
# the FastInfoset bug https://java.net/jira/browse/FI-16
runPipeline.sh -A -nl -o xml  convertBNC.xgapp tmp-in "${outDir}"
