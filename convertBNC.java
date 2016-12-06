// Convert a BNC document loaded as XML to what we want it to look like.
// This does the following main steps:
// * convert information from the header to document features starting with bnc.
// * remove the document header
// * create Token annotations in the Key set based on the w and c annotations from the original XML
// * create Sentence annotations in the Key set based on the s annotations from the original XML

import gate.*;
import java.util.regex.*;

Pattern whiteSpaceEnd = Pattern.compile("\\s+$");
Pattern whiteSpaceBeg = Pattern.compile("^\\s+");

@Override
public void execute() {
  FeatureMap dfm = doc.getFeatures();
  AnnotationSet oms = doc.getAnnotations("Original markups");
  store4field(doc,oms,"availability");
  store4field(doc,oms,"bibl");
  store4field(doc,oms,"classCode");
  store4field(doc,oms,"date");
  store4field(doc,oms,"distributor");
  store4field(doc,oms,"edition");
  store4field(doc,oms,"extent");
  store4field(doc,oms,"imprint");
  store4field(doc,oms,"keywords");
  store4field(doc,oms,"profileDesc");
  store4field(doc,oms,"pubPlace");
  store4field(doc,oms,"publicationStmt");
  store4field(doc,oms,"publisher");
  store4field(doc,oms,"respStmt");
  store4field(doc,oms,"sourceDesc");
  store4field(doc,oms,"titleStmt");
  //add stuff that is special ...
  dfm.put("bnc.imprint.n",gate.Utils.getOnlyAnn(oms.get("imprint")).getFeatures().get("n"));
  dfm.put("bnc.id",gate.Utils.getOnlyAnn(oms.get("bncDoc")).getFeatures().get("xml:id"));
  dfm.put("bnc.catRef.targets",gate.Utils.getOnlyAnn(oms.get("catRef")).getFeatures().get("targets"));
  AnnotationSet changeAnns = oms.get("change");
  for(Annotation ann : changeAnns) {
    String text = gate.Utils.cleanStringFor(doc,ann);
    String date = (String)ann.getFeatures().get("date");
    String who = (String)ann.getFeatures().get("who");
    dfm.put("bnc.change.what."+date,text);
    dfm.put("bnc.change.who."+date,who);
  }
  AnnotationSet usageAnns = oms.get("tagUsage");
  for(Annotation ann : usageAnns) {
    String gi = (String)ann.getFeatures().get("gi");
    String occurs = (String)ann.getFeatures().get("occurs");
    dfm.put("bnc.tagUsage."+gi,Integer.parseInt(occurs));
  }

  // Delete the span where the header is located
  Annotation wtextAnn = gate.Utils.getOnlyAnn(oms.get("wtext"));
  Long startDoc = gate.Utils.start(wtextAnn);

  AnnotationSet keySet = doc.getAnnotations("Key");
  AnnotationSet sentAnns = oms.get("s");
  for(Annotation ann : sentAnns) {
    gate.Utils.addAnn(keySet,ann,"Sentence",gate.Utils.featureMap("n",ann.getFeatures().get("n")));
  }
  // Convert the w annotations to Token annotations, but make sure whiteSpace is removed
  AnnotationSet wAnns = oms.get("w");
  for(Annotation ann : wAnns) {
    String text = gate.Utils.stringFor(doc,ann);
    String wsAfter = "";
    String wsBefore = "";
    Matcher m = whiteSpaceEnd.matcher(text);
    if(m.find()) {
      wsAfter = m.group(0);
    }
    m = whiteSpaceBeg.matcher(text);
    if(m.find()) {
      wsBefore = m.group(0);
    }
    Long start = gate.Utils.start(ann);
    Long end = gate.Utils.end(ann);
    // add the actual token
    FeatureMap fmAnn = ann.getFeatures();
    gate.Utils.addAnn(keySet,start+wsBefore.length(),end-wsAfter.length(),"Token",
      gate.Utils.featureMap("c5",fmAnn.get("c5"),"pos",fmAnn.get("pos"),"lemma",fmAnn.get("hw"),"kind","word"));
    if(!wsAfter.isEmpty()) {
      gate.Utils.addAnn(keySet,end-wsAfter.length(),end,"SpaceToken",gate.Utils.featureMap());      
    }
    if(!wsBefore.isEmpty()) {
      gate.Utils.addAnn(keySet,start,start+wsBefore.length(),"SpaceToken",gate.Utils.featureMap());
    }
  }
  AnnotationSet cAnns = oms.get("c");
  for(Annotation ann : cAnns) {
    String text = gate.Utils.cleanStringFor(doc,ann);
    gate.Utils.addAnn(keySet,ann,"Token",gate.Utils.featureMap("kind","punctuation","lemma",text));    
  }
}

private void store4field(Document doc, AnnotationSet set, String name) {
  AnnotationSet anns = set.get(name);
  if(anns.size() != 1) {
    System.err.println(doc.getName()+": not exactly one annotation for "+name);
  } else {
    Annotation ann = gate.Utils.getOnlyAnn(anns);
    String text = gate.Utils.cleanStringFor(doc,ann);
    doc.getFeatures().put("bnc."+name,text);
  }
}



