import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Day1 {
    public static void main(String[] args) throws IOException {
        final List<Integer> lines = Files.readAllLines(Paths.get("input", "day1"))
                .stream()
                .map(String::trim)
                .map(line -> Integer.valueOf(line))
                .collect(Collectors.toList());

        partOne(lines);
        partTwo(lines);

    }

    private static void partOne(List<Integer> lines) {
        for(int i = 0; i < lines.size(); ++i) {
            for (int j = i+1; j < lines.size(); ++j) {
                final int valueI = lines.get(i), valueJ = lines.get(j);
                if(valueI + valueJ == 2020) {
                    System.out.println(valueI * valueJ);
                    return;
                }
            }
        }
    }

    private static void partTwo(List<Integer> lines) {
        for(int i = 0; i < lines.size(); ++i) {
            for (int j = i+1; j < lines.size(); ++j) {
                for (int k = j+1; k < lines.size(); ++k) {
                    final int valueI = lines.get(i), valueJ = lines.get(j), valueK = lines.get(k);
                    if (valueI + valueJ +valueK == 2020) {
                        System.out.println(valueI * valueJ * valueK);
                        return;
                    }
                }
            }
        }
    }
}
