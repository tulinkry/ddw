/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ngrams.processor;

import gate.Document;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;
import gate.util.InvalidOffsetException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 *
 * @author tulinkry
 */
public class Processor {

    GateWrapper gate;

    public Processor() throws GateException, MalformedURLException {
        gate = new GateWrapper();
    }
    
    public void process (List<Document> documents) throws ResourceInstantiationException, ExecutionException {
        gate.run(documents);
    }

    public List<Document> getDocuments () {
        return gate.getDocuments();
    }
    
    public List<String> getTokens() throws ResourceInstantiationException, InvalidOffsetException, ExecutionException {
        return gate.getAnnotations("Token");
    }
    
    public List<String> getTokens(Document doc) throws ResourceInstantiationException, InvalidOffsetException, ExecutionException {
        return gate.getAnnotations("Token", doc);
    }

    public Document createDocument(String content) throws ResourceInstantiationException {
        return gate.createDocument(content);
    }

    public Document createDocument(File f) throws ResourceInstantiationException, FileNotFoundException, IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(f.getAbsolutePath()));
        return createDocument(new String(encoded, StandardCharsets.UTF_8));
    }

    public void clear() {
        gate.clear();
    }

}
