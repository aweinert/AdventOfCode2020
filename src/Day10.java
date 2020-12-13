import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Day10 {
    public static void main(String[] args) throws IOException {
        final List<Integer> inputList = Files.readAllLines(Paths.get("input", "day10")).stream()
                .map(Integer::parseInt)
                .sorted()
                .collect(Collectors.toList());

        printNumberOfJumps(inputList);
        printNumberOfPossibilities(inputList);
    }

    private static void printNumberOfPossibilities(List<Integer> inputList) {
        // We need a list of Long here since Int just cannot hold that many possibilities
        final List<Long> possibilities = new LinkedList<>();

        System.out.println(inputList);

        for(int i = 0; i < inputList.size(); ++i) {
            long currentPossibilities = 0;
            if(inputList.get(i) <= 3) {
                currentPossibilities += 1;
            }

            for (int j = Math.max(i - 3, 0); j < i; ++j) {
                if(inputList.get(j) >= inputList.get(i) - 3) {
                    currentPossibilities += possibilities.get(j);
                }
            }
            possibilities.add(currentPossibilities);
        }
        System.out.println(possibilities.get(possibilities.size() - 1));
    }

    private static void printNumberOfJumps(List<Integer> inputList) {
        int numberOfOneJumps = 0;
        int numberOfThreeJumps = 0;
        int currentJoltage = 0;
        for(Integer nextJoltage : inputList) {
            if(nextJoltage == currentJoltage + 1) {
                numberOfOneJumps += 1;
            } else if (nextJoltage == currentJoltage + 3) {
                numberOfThreeJumps += 1;
            }
            currentJoltage = nextJoltage;
        }
        numberOfThreeJumps += 1;

        System.out.println(numberOfOneJumps * numberOfThreeJumps);
    }
}
