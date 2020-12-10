import javax.crypto.Cipher;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

class VirtualMachine {
    private int programCounter = 0;
    private int accumulator = 0;
    private final List<Instruction> program;

    public VirtualMachine(List<Instruction> program) {
        this.program = program;
    }

    public int getProgramCounter() {
        return this.programCounter;
    }

    public void incrementProgramCounter(int offset) {
        this.programCounter += offset;
    }

    public void incrementAccumulator(int offset) {
        this.accumulator += offset;
    }

    public void executeCycle() {
        this.program.get(this.programCounter).execute(this);
    }

    public int getAccumulator() {
        return this.accumulator;
    }

    public void executeUntilTermination() {
        final Collection<Integer> visitedLocations = new HashSet<>();
        do {
            visitedLocations.add(this.getProgramCounter());
            this.executeCycle();
        } while(!(visitedLocations.contains(this.getProgramCounter()) || this.getProgramCounter() >= this.program.size()));
    }
}

abstract class Instruction {
    public static Instruction parseString(String line) {
        final String[] parts = line.split(" ");
        final int argument = Integer.parseInt(parts[1]);
        switch (parts[0]) {
            case "nop":
                return new NoOpInstruction(argument);
            case "jmp":
                return new JumpInstruction(argument);
            case "acc":
                return new AccumulateInstruction(argument);
            default:
                throw new IllegalArgumentException();
        }
    }

    private final int argument;

    public Instruction(int argument) {
        this.argument = argument;
    }

    public abstract void execute(VirtualMachine machine);

    public int getArgument() {
        return(this.argument);
    }
}

class JumpInstruction extends Instruction {
    public JumpInstruction(int argument) {
        super(argument);
    }

    @Override
    public void execute(VirtualMachine machine) {
        machine.incrementProgramCounter(this.getArgument());
    }
}

class AccumulateInstruction extends Instruction {
    public AccumulateInstruction(int argument) {
        super(argument);
    }

    @Override
    public void execute(VirtualMachine machine) {
        machine.incrementAccumulator(this.getArgument());
        machine.incrementProgramCounter(1);
    }
}

class NoOpInstruction extends Instruction {
    public NoOpInstruction(int argument) {
        super(argument);
    }

    @Override
    public void execute(VirtualMachine machine) {
        machine.incrementProgramCounter(1);
    }
}

public class Day8 {
    public static void main(String[] args) throws IOException {
        final List<Instruction> originalProgram = Files.readAllLines(Paths.get("input", "day8")).stream()
                .map(Instruction::parseString)
                .collect(Collectors.toList());
        final VirtualMachine machine = new VirtualMachine(originalProgram);

        machine.executeUntilTermination();
        System.out.println(machine.getAccumulator());

        for (int i = 0; i < originalProgram.size(); ++i) {
            final Instruction originalInstruction = originalProgram.get(i);
            final List<Instruction> manipulatedProgram = new LinkedList<>(originalProgram);
            if (originalInstruction instanceof AccumulateInstruction) {
                continue;
            } else if (originalInstruction instanceof NoOpInstruction) {
                manipulatedProgram.set(i, new JumpInstruction(originalInstruction.getArgument()));
            } else {
                manipulatedProgram.set(i, new NoOpInstruction(originalInstruction.getArgument()));
            }

            final VirtualMachine manipulatedMachine = new VirtualMachine(manipulatedProgram);
            manipulatedMachine.executeUntilTermination();
            if (manipulatedMachine.getProgramCounter() == originalProgram.size()) {
                System.out.println(manipulatedMachine.getAccumulator());
            }
        }
    }
}
