#!/bin/bash

mkdir tmp-in
find 2554/download/Texts/ -name '*.xml' | xargs -n 1 -I '{}' cp '{}' tmp-in/

mkdir out

# Would be nice to store in FastInfoset but the document HHV will trigger
# the FastInfoset bug https://java.net/jira/browse/FI-16
runPipeline.sh -A -nl -o xml  convertBNC.xgapp tmp-in out
