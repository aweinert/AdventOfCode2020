import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

class Map {
    private final List<String> lines;

    public Map(List<String> lines) {
        final IntSummaryStatistics statistics = lines.stream().collect(Collectors.summarizingInt(line -> line.length()));

        if(statistics.getMax() != statistics.getMin()) {
            throw new IllegalArgumentException("All lines must have the same length");
        }

        this.lines = lines;
    }

    public boolean isTreeAt(int x, int y) {
        x = x % this.getWidth();
        return this.lines.get(y).charAt(x) == '#';
    }

    private int getWidth() {
        return this.lines.get(0).length();
    }

    public int getHeight() {
        return this.lines.size();
    }

}

class Coordinate {
    public int x, y;
}

class Slope {
    private int offsetX, offsetY;

    public Slope(int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public Coordinate getNextCoordinate(Coordinate start) {
        final Coordinate next = new Coordinate();
        next.x = start.x + this.offsetX;
        next.y = start.y + this.offsetY;
        return next;
    }
}

public class Day3 {
    public static void main(String[] args) throws IOException {
        final List<String> lines = Files.readAllLines(Paths.get("input", "day3"));
        final Map map = new Map(lines);

        final List<Slope> slopesPart1 = new LinkedList<>();
        slopesPart1.add(new Slope(3,1));

        System.out.println(getProductOfTreesHit(map, slopesPart1));

        final List<Slope> slopesPart2 = new LinkedList<>();
        slopesPart2.add(new Slope(1,1));
        slopesPart2.add(new Slope(3,1));
        slopesPart2.add(new Slope(5,1));
        slopesPart2.add(new Slope(7,1));
        slopesPart2.add(new Slope(1,2));

        System.out.println(getProductOfTreesHit(map, slopesPart2));
    }

    private static BigInteger getProductOfTreesHit(Map map, List<Slope> slopes) {
        BigInteger accumulator = BigInteger.ONE;
        for(Slope slope : slopes) {

            int treesHit = getTreesHit(map, slope);
            accumulator = accumulator.multiply(BigInteger.valueOf(treesHit));
        }
        return accumulator;
    }

    private static int getTreesHit(Map map, Slope slope) {
        Coordinate current = new Coordinate();
        current.x = 0;
        current.y = 0;

        int treesHit = 0;
        while(current.y < map.getHeight()) {

            if(map.isTreeAt(current.x, current.y)) {
                treesHit += 1;
            }

            current = slope.getNextCoordinate(current);
        }
        return treesHit;
    }
}
