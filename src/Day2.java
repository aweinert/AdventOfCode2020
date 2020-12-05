import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

class Policy {
    private int lowIndex, highIndex;
    private char character;

    public Policy(String input) {
        final String[] split = input.split(" ");
        this.lowIndex = Integer.valueOf(split[0].split("-")[0]);
        this.highIndex = Integer.valueOf(split[0].split("-")[1]);
        if(split[1].length() != 1) {
            throw new IllegalStateException("Not a single character: " + split[1]);
        }
        this.character = split[1].charAt(0);
    }

    public boolean conforms1(String password) {
        int matchingChars = 0;
        for (int i = 0; i < password.length(); ++i) {
            if(password.charAt(i) == this.character) {
                matchingChars += 1;
            }
        }
        return this.lowIndex <= matchingChars && matchingChars <= this.highIndex;
    }

    public boolean conforms2(String password) {
        final char char1 = password.charAt(this.lowIndex - 1);
        final char char2 = password.charAt(this.highIndex - 1);
        return char1 == this.character ^ char2 == this.character;
    }
}

class Line {
    private final Policy policy;
    private final String password;

    public Line(String line) {
        this.policy = new Policy(line.split(":")[0].trim());
        this.password = line.split(":")[1].trim();
    }

    public boolean passwordConforms1(){
        return this.policy.conforms1(password);
    }

    public boolean passwordConforms2(){
        return this.policy.conforms2(password);
    }
}

public class Day2 {
    public static void main(String[] args) throws IOException {
        final int conformingPasswords = Files.readAllLines(Paths.get("input", "day2")).stream()
                .map(line -> new Line(line))
                .map(line -> line.passwordConforms1())
                .mapToInt(input -> input ? 1 : 0).sum();
        System.out.println("Conforming passwords (part1): " + conformingPasswords);

        final int conformingPasswords2 = Files.readAllLines(Paths.get("input", "day2")).stream()
                .map(line -> new Line(line))
                .map(line -> line.passwordConforms2())
                .mapToInt(input -> input ? 1 : 0).sum();
        System.out.println("Conforming passwords (part2): " + conformingPasswords2);
    }
}
