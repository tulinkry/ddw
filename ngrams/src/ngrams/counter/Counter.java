/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ngrams.counter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author tulinkry
 */
public class Counter {

    private String word ( String s, int which ) {
        String[] split = s.split(" ");
        return split[which];
    }
    
    public HashMap<String, Integer> countNgrams(List<String> words) {
        HashMap<String, Integer> map = new HashMap<>();
        for ( String w : words ) {
            if ( map . get(w) == null )
                map . put (w, 1);
            else
                map . put (w, map.get(w) + 1);
        }
        return map;
    }
    
    public HashMap<String, Double> probabilityNgrams(HashMap<String, Integer> words, int n) {
        HashMap<String, Double> map = new HashMap<>();
        int len = words.size();
        int sum;
        for (String ngram : words.keySet()) {
            switch (n) {
                case 1:
                    map.put(ngram, -Math.log(words.get(ngram)/(double) len));
                    break;
                case 2:
                    sum = 0;
                   
                    for (String ngram2 : words.keySet()) {
                        if (word(ngram, 0).equals(word(ngram2, 0))) {
                            sum += words.get(ngram);
                        }
                    }
                    map.put(ngram, -Math.log(words.get(ngram)/(double) sum));
                    break;
                case 3:
                    sum = 0;
                    for (String ngram2 : words.keySet()) {
                        if (word(ngram, 0).equals(word(ngram2, 0)) &&
                            word(ngram, 1).equals(word(ngram2, 1))) {
                            sum += words.get(ngram);
                        }
                    }
                    map.put(ngram, -Math.log(words.get(ngram)/(double) sum));
                    break;
                default:
                    throw new UnsupportedOperationException("Ngrams only 1 <= x <= 3");
            }
        }
        return map;
    }
        
    public List<String> generateNgrams(List<String> words, int n) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i<words.size()-n+1; i ++) {
            StringBuilder ngram = new StringBuilder();
            for (int j = i; j<i+n; j ++) {
                ngram.append(words.get(j));
                if (j!=i+n-1) {
                    ngram.append(' ');
                }
            }
            list.add(ngram.toString());
        }

        return list;
    }
}
