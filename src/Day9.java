import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

class LongColumn {
    private List<Long> entries;

    public LongColumn() {
        this(new LinkedList<>());
    }

    public LongColumn(List<Long> entries) {
        this.entries = entries;
    }

    public void removeFirstEntry() {
        this.entries.remove(0);
    }

    public void appendEntry(long nextEntry) {
        this.entries.add(nextEntry);
    }

    public boolean contains(long iValue) {
        return this.entries.contains(iValue);
    }
}

class LongMatrix {
    private List<LongColumn> columns = new LinkedList<>();

    public void removeFirstColumn() {
        this.columns.remove(0);
    }

    public void appendColumn(List<Long> entries) {
        this.appendColumn(new LongColumn(entries));
    }

    public void appendColumn(LongColumn column) {
        this.columns.add(column);
    }

    public void removeFirstRow() {
        for(LongColumn column : this.columns) {
            column.removeFirstEntry();
        }
    }

    public void appendRow(List<Long> entries) {
        if(entries.size() != this.columns.size()) {
            throw new IllegalArgumentException();
        }
        for(int i = 0; i < entries.size(); ++i) {
            this.columns.get(i).appendEntry(entries.get(i));
        }
    }

    public boolean contains(long iValue) {
        for(LongColumn column : this.columns) {
            if(column.contains(iValue)) {
                return true;
            }
        }
        return false;
    }
}

class Sublist {
    List<Long> mainList;
    int low = 0, high = 1;
    long sublistSum;

    public Sublist(List<Long> mainList) {
        this.mainList = mainList;
        this.sublistSum = mainList.get(0) + mainList.get(1);
    }

    public void append() {
        this.high += 1;
        this.sublistSum += mainList.get(this.high);
    }

    public void prepend() {
        this.low -= 1;
        this.sublistSum += mainList.get(this.low);
    }

    public void popFront() {
        this.sublistSum -= mainList.get(this.low);
        this.low += 1;
    }

    public void popRight() {
        this.sublistSum -= mainList.get(this.high);
        this.high += 1;
    }

    public int getLow() { return this.low; }
    public int getHigh() { return this.high; }

    public long getSublistSum() {
        return this.sublistSum;
    }
}

public class Day9 {

    private final List<Long> inputList;

    private final int windowSize;

    public static void main(String[] args) throws IOException {
        final List<Long> input = Files.readAllLines(Paths.get("input", "day9")).stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());

        final Day9 day = new Day9(input, 25);
        day.printCorruptedIndices();
        day.findSublistSum(542529149);
    }

    public Day9(List<Long> inputList, int windowSize) {
        this.inputList = inputList;
        this.windowSize = windowSize;
    }

    public void printCorruptedIndices() {
        final LongMatrix matrix = new LongMatrix();
        for (int i = 1; i <= windowSize; ++i) {
            final long iValue = inputList.get(i);
            final LongColumn newColumn = new LongColumn();
            for (int j = 0; j < i; ++j) {
                final long jValue = inputList.get(j);
                newColumn.appendEntry(iValue + jValue);
            }
            matrix.appendColumn(newColumn);
        }

        for (int i = windowSize + 1; i < inputList.size(); ++i) {
            final long iValue = inputList.get(i);
            if (!matrix.contains(iValue)) {
                System.out.println("Value " + iValue + " at position " + i + " is not a sum of any previous pair in window");
            }
            matrix.removeFirstColumn();
            matrix.removeFirstRow();

            final List<Long> newRow = new LinkedList<>();
            for (int j = i - windowSize + 1; j < i; ++j) {
                final long jValue = inputList.get(j);
                newRow.add(iValue + jValue);
            }

            matrix.appendRow(newRow);
            matrix.appendColumn(this.createInitialColumn(i));
        }
    }

    public void findSublistSum(long target) {
        final Sublist sublist = new Sublist(this.inputList);

        while(sublist.getSublistSum() != target) {
            if(sublist.getHigh() % 2 == 1) {
                if(sublist.getHigh() == sublist.getLow() + 1) {
                    sublist.append();
                }
                sublist.popFront();
            } else {
                if (sublist.getLow() == 1 || sublist.getSublistSum() > target) {
                    sublist.append();
                } else {
                    sublist.prepend();
                }
            }
        }

        System.out.println("The sublist from position " + sublist.getLow() + " to position " + sublist.getHigh() + " sums to " + target);

        final long min = inputList.subList(sublist.getLow(), sublist.getHigh() + 1).stream().mapToLong(val -> val).min().getAsLong();
        final long max = inputList.subList(sublist.getLow(), sublist.getHigh() - 1).stream().mapToLong(val -> val).max().getAsLong();

        System.out.println("The sum of the extremal values is " + (min + max));
    }

    public LongColumn createInitialColumn(int index) {
        final List<Long> column = new LinkedList<>();
        final long addedValue = this.inputList.get(index);
        for (int i = index - windowSize + 1; i < index; ++i) {
            column.add(inputList.get(i) + addedValue);
        }
        return new LongColumn(column);
    }
}
