# Conversion of the original British National Corpus documents, XML edition to GATE

The files in this repository can be used to convert the original XML fiels from the BNC corpus
(see http://ota.ox.ac.uk/desc/2554) to usable GATE documents. 

NOTE: this depends on the following tools and software, not included here:
* the runPipeline.sh command from https://github.com/johann-petrak/gatetool-runpipeline and the bin directory of
  that tool must be on the binary path
* the Java plugin is added as a submodule (https://github.com/johann-petrak/gateplugin-Java)
* GATE (version 8.x)
* JAVA SDK
* Ant

## Preparation

* Make sure the Java submodule is actually fetched and compiled
  * `git submodule init`
  * `git submodule update`
  * `git submodule foreach ant`
* Make sure the British National Corpus is available in some directory in unzipped for, the directory is usually called "2554"
* The conversion script will copy the BNC corpus into a local temporary directory, so make sure there is enough disk space
  on the disk which contains the current directory (about 4.4G needed)
* The GATE documents will require about 114G of disk space


## Run the conversion

Just run the convert.sh script and pass the location of the BNC corpus and the desired output directory as arguments:

`./convert.sh bnccorpusdir outputdir`


## Overview of how the conversion is done:
* Load original files into GATE in XML format, but set the option
  "add space on markup unpack if needed" to false
* Now, in the "Original markups" set we get all the XML fields as annotations.
* Remeber the following fields for document-features (if nothiing else specified, the document text for the field):
  * availability
  * bibl
  * bncDoc.xml:id  gets converted to id
  * catRef.targets
  * change: doc text is documentation of change, features date amd who give additional info, should get 
    converted to single list? Should get converted to change.\<date\>.\<who\> and change.\<date\>
  * classCode: text and feature scheme
  * creation.date
  * date
  * distributor
  * edition
  * extent
  * imprint (text and feature n)
  * keywords
  * profileDesc
  * pubPlace
  * publicationStmt
  * publisher
  * respStmt
  * sourceDesc
  * tagUsage: empty span annots with features gi and occurs. Should get converted to features
    tagUsage.\<gi\>=\<occurs\> 
  * titleStmt

Annotations relevant for the actual text:
* wtext: covers the part we are interested in. Anything before should eventually get removed
* w: words, features: c5, hw (lemma), pos
* c: something about punctuation and quotes, needed in addition to "w". features: c5
* mw: overlaps w annotations for multi-word stuff like "up to" and has feature c5=??
* s: sentences, feature n

