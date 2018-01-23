package agh;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class Solver {

    private String[] knowledgeBase;
    private Set<String> stimulusWords = ImmutableSet.of("word");
    private Map<Pair<String, String>, Integer> connections = new HashMap<>();
    private Map<String, Integer> stimulusCounter = new HashMap<>();
    private Map<String, Integer> otherFrequencies = new HashMap<>();

    public Solver(String[] knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }

    public void computeAssociations() {
        for (int i = 0; i < knowledgeBase.length; i++) {
            if (stimulusWords.contains(knowledgeBase[i])) {
                incrementCounter(stimulusCounter, knowledgeBase[i]);

                checkToTheLeft(knowledgeBase[i], i);
                checkToTheRight(knowledgeBase[i], i);
            }
        }


    }

    private void checkToTheRight(String stimulus, int i) {
        for (int j = i + 1; j <= i + 12 && j < knowledgeBase.length; j++) {
            Pair<String, String> pair = new ImmutablePair(stimulus, knowledgeBase[j]);
            incrementCounter(connections, pair);
        }
    }

    private void checkToTheLeft(String stimulus, int i) {
        for (int j = i - 1; j >= i - 12 && j >= 0; j--) {
            Pair<String, String> pair = new ImmutablePair(stimulus, knowledgeBase[j]);
            incrementCounter(connections, pair);
        }
    }

    private void getFrequency(Set<String> words) {
        Arrays.stream(knowledgeBase)
                .filter(words::contains)
                .forEach(word -> incrementCounter(otherFrequencies, word));
    }

    private static void incrementCounter(Map<String, Integer> counter, String key) {
        if (counter.containsKey(key)) {
            counter.put(key, counter.get(key) + 1);
        } else {
            counter.put(key, 0);
        }
    }

    private static void incrementCounter(Map<Pair<String, String>, Integer> counter, Pair<String, String> key) {
        if (counter.containsKey(key)) {
            counter.put(key, counter.get(key) + 1);
        } else {
            counter.put(key, 0);
        }
    }
}
