import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.Collector;

public class Day5 {
    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("input", "day5"));

        lines.sort((o1, o2) -> {
                for (int i = 0; i < o1.length(); ++i) {
                    final char char1 = o1.charAt(i), char2 = o2.charAt(i);
                    if (char1 == 'F' && char2 == 'B') {
                        return -1;
                    } else if (char1 == 'B' && char2 == 'F') {
                        return 1;
                    } else if (char1 == 'L' && char2 == 'R') {
                        return -1;
                    } else if (char1 == 'R' && char2 == 'L') {
                        return 1;
                    }
                }
                return 0;
            });

        System.out.println("Highest seat ID is : " + parseSeat(lines.get(lines.size() - 1)));

        int previousSeat = parseSeat(lines.get(0));
        for (int i = 1; i < lines.size(); ++i) {
            final int currentSeat = parseSeat(lines.get(i));
            if (currentSeat != previousSeat + 1) {
                System.out.println("Missing seat: " + (previousSeat + 1));
            }
            previousSeat = currentSeat;
        }

        // Alternative solution to part 2 using streams
        lines.stream()
                .map(Day5::parseSeat)
                .reduce((currentSeat, nextSeat) -> {
                        if (nextSeat != currentSeat + 1) {
                            System.out.println("Missing seat: " + (currentSeat + 1));
                        }
                        return nextSeat;
                    });
    }

    public static int parseSeat(String seat) {
        int accumulator = 0;
        for(int i = 0; i < seat.length(); ++i) {
            accumulator <<= 1;
            if (seat.charAt(i) == 'B' || seat.charAt(i) == 'R') {
                accumulator += 1;
            }
        }
        return accumulator;
    }

}
