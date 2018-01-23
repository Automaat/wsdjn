package agh;

import com.google.common.collect.ImmutableSet;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Solver {

    private static final double ALPHA = 0.66;
    private static final double BETA = 0.00002;
    private static final double GAMMA = 0.00002;
    private static final int EPSILON = 12;

    private String[] knowledgeBase;
    private Set<String> stimulusWords = ImmutableSet.of("matka", "dobra", "dom", "dziecko");
    private Map<String, Map<String, Integer>> connections = new HashMap<>();
    private Map<String, Integer> stimulusCounter = new HashMap<>();
    private Map<String, Integer> otherFrequencies = new HashMap<>();

    private Map<String, Map<String, Double>> associations = new HashMap<>();

    Solver(String[] knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }

    public void computeAssociations() {
        for (int i = 0; i < knowledgeBase.length; i++) {
            if (stimulusWords.contains(knowledgeBase[i])) {
                String stimulus = knowledgeBase[i];
                incrementCounter(stimulusCounter, stimulus);

                checkToTheLeft(stimulus, i);
                checkToTheRight(stimulus, i);
            }
        }

        Set<String> connectedWords = connections.values().stream()
                .flatMap(map -> map.keySet().stream())
                .collect(Collectors.toSet());

        updateFrequencies(connectedWords);

        stimulusWords.forEach(stimulus -> {
            associations.put(stimulus, new HashMap<>());
            fillAssociations(stimulus);
        });

        saveAssociationsToFiles();
    }

    private void checkToTheRight(String stimulus, int i) {
        for (int j = i + 1; j <= i + EPSILON && j < knowledgeBase.length; j++) {
            if (stimulus.equals(knowledgeBase[j])) {
                continue;
            }

            incrementCounter(connections, stimulus, knowledgeBase[j]);
        }
    }

    private void checkToTheLeft(String stimulus, int i) {
        for (int j = i - 1; j >= i - EPSILON && j >= 0; j--) {
            if (stimulus.equals(knowledgeBase[j])) {
                continue;
            }

            incrementCounter(connections, stimulus, knowledgeBase[j]);
        }
    }

    private void updateFrequencies(Set<String> words) {
        Arrays.stream(knowledgeBase)
                .filter(words::contains)
                .forEach(word -> incrementCounter(otherFrequencies, word));
    }

    private void fillAssociations(String stimulus) {
        Map<String, Integer> connectedWords = connections.get(stimulus);

        connectedWords.keySet().forEach(word -> {
            if (otherFrequencies.get(word) > BETA * knowledgeBase.length) {
                double association = connectedWords.get(word) / Math.pow(otherFrequencies.get(word), ALPHA);
                associations.get(stimulus).put(word, association);
            } else {
                double association = connectedWords.get(word) / (GAMMA * knowledgeBase.length);
                associations.get(stimulus).put(word, association);
            }
        });
    }

    private void saveAssociationsToFiles() {
        stimulusWords.forEach(stimulus -> {
            try {
                FileWriter fileWriter = new FileWriter(stimulus + ".txt");
                PrintWriter printWriter = new PrintWriter(fileWriter);
                Map<String, Double> words = associations.get(stimulus);
                words.entrySet().stream()
                        .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                        .map(Map.Entry::getKey)
                        .forEach(word -> printWriter.println(word + ": " + words.get(word).toString()));
                printWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void incrementCounter(Map<String, Integer> counter, String key) {
        if (counter.containsKey(key)) {
            counter.put(key, counter.get(key) + 1);
        } else {
            counter.put(key, 1);
        }
    }

    private static void incrementCounter(Map<String, Map<String, Integer>> counter, String key, String connection) {
        if (counter.containsKey(key)) {
            Map<String, Integer> stringIntegerMap = counter.get(key);
            incrementCounter(stringIntegerMap, connection);
        } else {
            counter.put(key, new HashMap<>());
            Map<String, Integer> stringIntegerMap = counter.get(key);
            incrementCounter(stringIntegerMap, connection);
        }
    }
}
