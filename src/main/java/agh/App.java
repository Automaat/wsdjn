package agh;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

public class App {

    public static void main(String[] args) {
        try (Stream<String> stream = Files.lines(Paths.get("pap_stemmed.txt"))) {

            String[] knowledgeBase = stream.filter(line -> !line.startsWith("#"))
                    .flatMap(line -> Arrays.stream(line.split(" ")))
                    .toArray(String[]::new);

            Solver solver = new Solver(knowledgeBase);

            solver.computeAssociations();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
