import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

enum CellState{
    EMPTY_SEAT,
    OCCUPIED_SEAT,
    NO_SEAT,
    UNDEFINED;

    public static CellState fromChar(char character) {
        switch (character) {
            case 'L':
                return EMPTY_SEAT;
            case '.':
                return NO_SEAT;
            default:
                throw new IllegalArgumentException();
        }
    }

    public char toChar() {
        switch (this) {
            case EMPTY_SEAT:
                return 'L';
            case OCCUPIED_SEAT:
                return '#';
            case NO_SEAT:
                return '.';
            default:
                throw new IllegalArgumentException();
        }

    }
}

interface EvolutionRule {
    boolean isApplicable(CellularAutomaton automaton, int x, int y);
    CellState evolve(CellularAutomaton automaton, int x, int y);
}

class SpawnRule implements EvolutionRule {
    @Override
    public boolean isApplicable(CellularAutomaton automaton, int x, int y) {
        return automaton.getState(x, y).equals(CellState.EMPTY_SEAT);
    }

    @Override
    public CellState evolve(CellularAutomaton automaton, int x, int y) {
        final long neighboringOccupiedSeats = automaton.countInVicinity(x, y, CellState.OCCUPIED_SEAT);
        return neighboringOccupiedSeats == 0 ? CellState.OCCUPIED_SEAT : CellState.EMPTY_SEAT;
    }
}

class SpawnRule2 implements EvolutionRule {
    @Override
    public boolean isApplicable(CellularAutomaton automaton, int x, int y) {
        return automaton.getState(x, y).equals(CellState.EMPTY_SEAT);
    }

    @Override
    public CellState evolve(CellularAutomaton automaton, int x, int y) {
        final long neighboringOccupiedSeats = automaton.countInSightline(x, y, CellState.OCCUPIED_SEAT);
        return neighboringOccupiedSeats == 0 ? CellState.OCCUPIED_SEAT : CellState.EMPTY_SEAT;
    }
}

class KillRule implements EvolutionRule {

    @Override
    public boolean isApplicable(CellularAutomaton automaton, int x, int y) {
        return automaton.getState(x, y).equals(CellState.OCCUPIED_SEAT);
    }

    @Override
    public CellState evolve(CellularAutomaton automaton, int x, int y) {
        final long neighboringOccupiedSeats = automaton.countInVicinity(x, y, CellState.OCCUPIED_SEAT);
        return neighboringOccupiedSeats >= 4 ? CellState.EMPTY_SEAT : CellState.OCCUPIED_SEAT;
    }
}

class KillRule2 implements EvolutionRule {

    @Override
    public boolean isApplicable(CellularAutomaton automaton, int x, int y) {
        return automaton.getState(x, y).equals(CellState.OCCUPIED_SEAT);
    }

    @Override
    public CellState evolve(CellularAutomaton automaton, int x, int y) {
        final long neighboringOccupiedSeats = automaton.countInSightline(x, y, CellState.OCCUPIED_SEAT);
        return neighboringOccupiedSeats >= 5 ? CellState.EMPTY_SEAT : CellState.OCCUPIED_SEAT;
    }
}

class CellularAutomaton {
    private CellState[][] state;

    private final List<EvolutionRule> rules = new LinkedList<>();

    public CellularAutomaton(List<String> lines) {
        this.state = lines.stream()
                .map(line -> {
                    // Taken from https://stackoverflow.com/a/31557292
                    final char[] charArray = line.toCharArray();
                    return IntStream.range(0, charArray.length).mapToObj(i -> charArray[i])
                            .map(CellState::fromChar)
                            .map(e -> {
                                if(e==null) {
                                    throw new IllegalStateException();
                                } else {
                                    return e;
                                }
                            })
                            .toArray(size -> new CellState[size]);
                }).toArray(size -> new CellState[size][]);
    }

    public CellState getState(int x, int y) {
        if(0 <= x && x < this.getWidth() && 0 <= y && y < this.getHeight()) {
            final CellState returnValue = this.state[y][x];
            if(returnValue == null) {
                throw new IllegalArgumentException();
            }
            return this.state[y][x];
        } else {
            return CellState.UNDEFINED;
        }
    }

    public void addRule(EvolutionRule rule) {
        this.rules.add(rule);
    }

    public void evolveUntilStability() {
        CellState[][] nextState = this.singleEvolutionRound();
        // As is turns out, Object[].equals does not do a deep compare
        while(!this.isSameState(nextState)) {
            this.state = nextState;
            nextState = this.singleEvolutionRound();
        }
    }

    public boolean isSameState(CellState[][] other) {
        for(int i = 0; i < other.length; ++i) {
            for(int j = 0; j < other[0].length; ++j) {
                if(!other[i][j].equals(this.state[i][j])) {
                    return false;
                }
            }
        }
        return true;
    }

    public long countOccupiedSeats() {
        return Arrays.stream(this.state)
                .flatMap(Stream::of)
                .filter(state -> state.equals(CellState.OCCUPIED_SEAT))
                .count();
    }


    private CellState[][] singleEvolutionRound() {
        final CellState[][] newState = new CellState[this.getHeight()][this.getWidth()];
        for(int xCur = 0; xCur < this.getWidth(); ++xCur) {
            for(int yCur = 0; yCur < this.getHeight(); ++yCur) {
                final int finalXCur = xCur;
                final int finalYCur = yCur;
                final List<EvolutionRule> applicableRules = this.rules.stream()
                        .filter(rule -> rule.isApplicable(this, finalXCur, finalYCur))
                        .collect(Collectors.toList());
                if(applicableRules.size() > 1) {
                    throw new IllegalStateException();
                }
                if(!applicableRules.isEmpty()) {
                    newState[yCur][xCur] = applicableRules.get(0).evolve(this, xCur, yCur);
                } else {
                    newState[yCur][xCur] = this.getState(xCur, yCur);
                }
            }
        }
        return newState;
    }

    private int getWidth() {
        return this.state[0].length;
    }

    private int getHeight() {
        return this.state.length;
    }

    CellState getSeatInSightline(int xStart, int yStart, int xOffset, int yOffset) {
        int currentX = xStart, currentY = yStart;
        CellState currentCellState;
        do {
            currentX += xOffset;
            currentY += yOffset;
            currentCellState = getState(currentX, currentY);
        } while (0 <= currentX && currentX < this.getWidth() && 0 <= currentY && currentY < this.getHeight() &&
            currentCellState.equals(CellState.NO_SEAT));
        return currentCellState;
    }

    public long countInVicinity(int x, int y, CellState stateToCount) {
        return Stream.of(
                this.getState(x - 1, y-1),
                this.getState(x, y-1),
                this.getState(x + 1, y - 1),
                this.getState(x-1, y), this.getState(x+1, y),
                this.getState(x - 1, y + 1),
                this.getState(x, y + 1),
                this.getState(x + 1, y + 1))
                .filter(state -> state.equals(stateToCount))
                .count();
    }

    public long countInSightline(int x, int y, CellState stateToCount) {
        return Stream.of(
                this.getSeatInSightline(x, y, -1, -1),
                this.getSeatInSightline(x, y, -1, 0),
                this.getSeatInSightline(x, y, -1, 1),
                this.getSeatInSightline(x, y, 0, -1),
                this.getSeatInSightline(x, y, 0, 1),
                this.getSeatInSightline(x, y, 1, -1),
                this.getSeatInSightline(x, y, 1, 0),
                this.getSeatInSightline(x, y, 1, 1))
                .filter(state -> state.equals(stateToCount))
                .count();
    }
}

public class Day11 {
    public static void main(String[] args) throws IOException {
        CellularAutomaton automaton = new CellularAutomaton(Files.readAllLines(Paths.get("input", "day11")));
        automaton.addRule(new SpawnRule());
        automaton.addRule(new KillRule());
        automaton.evolveUntilStability();

        System.out.println(automaton.countOccupiedSeats());

        CellularAutomaton automaton2 = new CellularAutomaton(Files.readAllLines(Paths.get("input", "day11")));
        automaton2.addRule(new SpawnRule2());
        automaton2.addRule(new KillRule2());
        automaton2.evolveUntilStability();

        System.out.println(automaton2.countOccupiedSeats());
    }

    // Used for debugging during development
    public static void printArray(CellState[][] state) {
        System.out.println(Arrays.stream(state)
                .map(array -> Arrays.stream(array)
                        .map(CellState::toChar)
                        .map(String::valueOf)
                        .collect(Collectors.joining()))
                .collect(Collectors.joining("\n"))
        );
    }
}
