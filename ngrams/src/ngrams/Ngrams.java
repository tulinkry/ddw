/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ngrams;

import gate.Document;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import ngrams.counter.Counter;
import ngrams.processor.Processor;
import org.json.JSONObject;

/**
 *
 * @author tulinkry
 */
public class Ngrams {

    /**
     * @param args the command line arguments
     * @throws gate.creole.ResourceInstantiationException
     * @throws java.net.MalformedURLException
     */
    public static void main(String[] args) throws ResourceInstantiationException, GateException, MalformedURLException, IOException {
        String path = "/var/www/ddw-ngrams/data";

        Processor p = new Processor();
        List<Document> docs = new ArrayList<>();
        JSONObject all = new JSONObject();

        File dir = new File(path);
        for (File f : dir.listFiles()) {
            if (f.getName().equals(".")||f.getName().equals("..")) {
                continue;
            }
            if ( ! f.isDirectory()) {
                continue;
            }
            String language = f.getName();

            for (File d : f.listFiles()) {
                if (d.isDirectory()) {
                    continue;
                }
                System.out.println("adding file "+f.getName()+"/"+d.getName());
                docs.add(p.createDocument(d));
            }

            p.process(docs);

            Counter c = new Counter();
            List<String> unigrams = new ArrayList<>();
            List<String> bigrams = new ArrayList<>();
            List<String> trigrams = new ArrayList<>();

            for (Document doc : p.getDocuments()) {

                List<String> annot = p.getTokens(doc);

                unigrams.addAll(c.generateNgrams(annot, 1));
                bigrams.addAll(c.generateNgrams(annot, 2));
                trigrams.addAll(c.generateNgrams(annot, 3));

            }
            JSONObject lang = new JSONObject();

            lang.put("unigrams", c.probabilityNgrams(c.countNgrams(unigrams), 1));
            lang.put("bigrams", c.probabilityNgrams(c.countNgrams(bigrams), 2));
            lang.put("trigrams", c.probabilityNgrams(c.countNgrams(trigrams), 3));

            all.put(language, lang);

            docs.clear();
            p.clear();

        }

        try (FileWriter file = new FileWriter("/var/www/ddw-ngrams/data/data.json")) {
            file.write(all.toString());
            System.out.println("File saved");
        }

        /*
        for (String n : c.generateNgrams(annot, 1)) {
            System.out.println(n);
        }

        for (String n : c.generateNgrams(annot, 2)) {
            System.out.println(n);
        }

        for (String n : c.generateNgrams(annot, 3)) {
            System.out.println(n);
        }
         */
    }
}
