import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class BagType {
    String color;

    public BagType(String color) {
        this.color = color;
    }

    public boolean equals(Object other) {
        return other instanceof BagType && this.color.equals(((BagType)other).color);
    }
}

class Connection {
    BagType container, contained;
    int number;

    public Connection(BagType container, int number, BagType contained) {
        this.container = container;
        this.number = number;
        this.contained = contained;
    }
}

class Constraints {
    Collection<Connection> connections = new HashSet<>();

    public void addConnection(Connection connection) {
        this.connections.add(connection);
    }

    public void addConnection(BagType container, int number, BagType contained) {
        this.connections.add(new Connection(container, number, contained));
    }

    public Collection<BagType> getContainers(BagType type) {
        final Collection<BagType> retVal = this.connections.stream()
                .filter(connection -> connection.contained.equals(type))
                .map(connection -> connection.container)
                .collect(Collectors.toSet());
        return retVal;
    }

    public Collection<BagType> getTransitiveContainers(BagType containedType) {
        final Queue<BagType> toVisit = new LinkedList<>();
        final Collection<BagType> visited = new HashSet<>();

        toVisit.add(containedType);

        while(!toVisit.isEmpty()) {
            final BagType currentContainedType = toVisit.remove();
            visited.add(currentContainedType);
            getContainers(currentContainedType).stream()
                    .filter(container -> !visited.contains(container))
                    .forEach(container -> toVisit.add(container));
        }

        visited.remove(containedType);
        return visited;
    }

    private Stream<Connection> getOutgoingConnections(BagType container) {
        return this.connections.stream()
                .filter(connection -> connection.container.equals(container));
    }

    public int getNumberContained(BagType containerType) {
        return this.getOutgoingConnections(containerType)
                .mapToInt(connection -> connection.number * getNumberContained(connection.contained))
                .sum() + 1;
    }
}

public class Day7 {
    public static void main(String[] args) throws IOException {
        final Constraints constraints = new Constraints();

        Files.readAllLines(Paths.get("input", "day7")).stream()
                .flatMap(Day7::parseLine)
                .forEach(constraints::addConnection);

        System.out.println(constraints.getTransitiveContainers(new BagType("shiny gold")).size());
        System.out.println(constraints.getNumberContained(new BagType("shiny gold")) - 1);
    }

    public static Stream<Connection> parseLine(String line) {
        final Matcher matcher = Pattern.compile("^([a-zA-Z ]*) bags contain (.*)$").matcher(line);
        matcher.find();
        final BagType containerColor = new BagType(matcher.group(1));
        if(matcher.group(2).contains("no other bags")) {
            return Stream.empty();
        }

        return Arrays.stream(matcher.group(2).split(", "))
            .map(String::trim)
            .map(entry -> {
                final Matcher entryMatcher = Pattern.compile("^.*([\\d]) ([a-zA-Z]* [a-zA-Z]*) bags?\\.?$").matcher(entry);
                entryMatcher.find();
                final int number = Integer.valueOf(entryMatcher.group(1));
                final BagType containedColor = new BagType(entryMatcher.group(2));
                return new Connection(containerColor, number, containedColor);
            });
    }
}
