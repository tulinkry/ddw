/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ngrams.processor;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.CreoleRegister;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.Node;
import gate.ProcessingResource;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.SerialAnalyserController;
import gate.util.GateException;
import gate.util.InvalidOffsetException;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tulinkry
 */
public class GateWrapper {

    // corpus pipeline
    private static SerialAnalyserController annotationPipeline = null;

    public GateWrapper() throws GateException, MalformedURLException {

        // set GATE home folder
        // Eg. /Applications/GATE_Developer_7.0
        File gateHomeFile = new File("/usr/local/gate/GATE_Developer_8.1");
        Gate.setGateHome(gateHomeFile);

        // set GATE plugins folder
        // Eg. /Applications/GATE_Developer_7.0/plugins            
        File pluginsHome = new File("/usr/local/gate/GATE_Developer_8.1/plugins");
        Gate.setPluginsHome(pluginsHome);

        // set user config file (optional)
        // Eg. /Applications/GATE_Developer_7.0/user.xml
        Gate.setUserConfigFile(new File("/usr/local/gate/GATE_Developer_8.1/", "gate.xml"));
        // initialise the GATE library
        Gate.init();

        // load ANNIE plugin
        CreoleRegister register = Gate.getCreoleRegister();
        URL annieHome = new File(pluginsHome, "ANNIE").toURL();
        register.registerDirectories(annieHome);

        annotationPipeline = (SerialAnalyserController) Factory.createResource("gate.creole.SerialAnalyserController");
    }

    private void buildPipeline() throws ResourceInstantiationException {

// create an instance of a Document Reset processing resource
        ProcessingResource documentResetPR = (ProcessingResource) Factory.createResource("gate.creole.annotdelete.AnnotationDeletePR");

        // create an instance of a English Tokeniser processing resource
        ProcessingResource tokenizerPR = (ProcessingResource) Factory.createResource("gate.creole.tokeniser.DefaultTokeniser");

        annotationPipeline.add(documentResetPR);
        annotationPipeline.add(tokenizerPR);
    }

    private void addDocument(Document d) throws ResourceInstantiationException {
        Corpus c;
        if ((c = annotationPipeline.getCorpus())==null) {
            c = Factory.newCorpus("");
        }
        // create a corpus and add the document
        c.add(d);

        // set the corpus to the pipeline
        annotationPipeline.setCorpus(c);
    }
    
    public List<Document> getDocuments() {
        List<Document> list = new ArrayList<> ();
        Corpus corpus = annotationPipeline.getCorpus();
        // loop through the documents in the corpus
        for (int i = 0; i<corpus.size(); i ++) {
            Document doc = corpus.get(i);
            list.add(doc);
        }
        return list;
    }

    private void runPipeline(List<Document> documents) throws ResourceInstantiationException, ExecutionException {
        for (Document d : documents) {
            addDocument(d);
        }

        //run the pipeline
        annotationPipeline.execute();

    }
    
    public void clear() {
        annotationPipeline.setCorpus(null);
    }

    public void run(List<Document> documents) throws ResourceInstantiationException, ExecutionException {

        // create corpus pipeline
        // add the processing resources (modules) to the pipeline
        this.buildPipeline();
        this.runPipeline(documents);
    }

    public List<String> getAnnotations(String name) throws InvalidOffsetException {
        List<String> all = new ArrayList<>();
        Corpus corpus = annotationPipeline.getCorpus();
        // loop through the documents in the corpus
        for (int i = 0; i<corpus.size(); i ++) {
            Document doc = corpus.get(i);
            all.addAll(getAnnotations(name, doc));
        }
        return all;
    }

    public List<String> getAnnotations(String name, Document document) throws InvalidOffsetException {
        List<String> list = new ArrayList<>();
        // get the default annotation set
        AnnotationSet as_default = document.getAnnotations();
        FeatureMap map = null;
        // get all Token annotationst
        AnnotationSet tokens = as_default.get(name, map);
        ArrayList annotations = new ArrayList(tokens);

        // looop through the Token annotations
        for (int i = 0; i<annotations.size();  ++ i) {

            // get a token annotation
            Annotation token = (Annotation) annotations.get(i);

            // get the underlying string for the Token
            Node isaStart = token.getStartNode();
            Node isaEnd = token.getEndNode();
            String underlyingString = document.getContent().getContent(isaStart.getOffset(), isaEnd.getOffset()).toString();
            underlyingString = underlyingString.toLowerCase();

            FeatureMap annFM = token.getFeatures();
            if (annFM.get("kind").equals("word")) {
                list.add(underlyingString);
            }
        }
        return list;
    }

    public Document createDocument(String content) throws ResourceInstantiationException {
        return Factory.newDocument(content);
    }



}
