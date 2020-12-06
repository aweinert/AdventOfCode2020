import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

interface Statistics {
    void addPassenger(final String line);
    void finalizeGroup();
    int getSum();
}

class InclusiveStatistics implements Statistics {
    int sum = 0;
    final Set<Character> currentCharacters = new HashSet<>();

    public void finalizeGroup() {
        this.sum += currentCharacters.size();
        this.currentCharacters.clear();
    }

    public void addPassenger(final String line) {
        for(int i = 0; i < line.length(); ++i) {
            this.currentCharacters.add(line.charAt(i));
        }
    }

    public int getSum() {
        return this.sum;
    }

}

class ExclusiveStatistics implements Statistics {
    int sum = 0;
    Set<Character> currentCharacters = new HashSet<>();
    boolean firstPassengerInGroup = true;

    public void finalizeGroup() {
        this.sum += currentCharacters.size();
        this.currentCharacters.clear();
        firstPassengerInGroup = true;
    }

    public void addPassenger(final String line) {
        System.out.println(line);
        final Set<Character> chars = line.trim().chars().mapToObj(i -> (char)i).collect(Collectors.toSet());
        System.out.println(chars);
        if (firstPassengerInGroup) {
            this.currentCharacters = chars;
            firstPassengerInGroup = false;
        } else {
            this.currentCharacters.retainAll(chars);
        }

    }

    public int getSum() {
        return this.sum;
    }

}

public class Day6 {
    public static void main(String[] args) throws IOException {
        final List<String> lines = Files.readAllLines(Paths.get("input", "day6"));

        System.out.println(computeStatistics(lines, new InclusiveStatistics()).getSum());
        System.out.println(computeStatistics(lines, new ExclusiveStatistics()).getSum());

    }

    private static Statistics computeStatistics(List<String> lines, Statistics stats) {
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                stats.finalizeGroup();
            } else {
                stats.addPassenger(line);
            }
        }
        stats.finalizeGroup();
        return stats;
    }
}
