import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Passport {
    final Map<String, String> fields;

    public Passport(List<String> lines) {
        this.fields = new HashMap<>();

        final String[] fields = String.join(" ", lines).split(" ");
        for(String field : fields) {
            final String key = field.split(":")[0];
            final String value = field.split(":")[1];

            this.fields.put(key, value);
        }
    }

    public boolean hasField(String field) {
        return this.fields.containsKey(field);
    }

    public String getField(String field) {
        return this.fields.get(field);
    }
}

interface Validator {
    boolean isValid(Passport passport);
}

class SimpleValidator implements Validator {
    @Override
    public boolean isValid(Passport passport) {
        return passport.hasField("byr") &&
                passport.hasField("iyr") &&
                passport.hasField("eyr") &&
                passport.hasField("hgt") &&
                passport.hasField("hcl") &&
                passport.hasField("ecl") &&
                passport.hasField("pid");
    }

}

class StrictValidator implements Validator {
    @Override
    public boolean isValid(Passport passport) {
        if(!(new SimpleValidator().isValid(passport))) {
            return false;
        }

        return isBirthYearValid(passport.getField("byr")) &&
                isIssueYearValid(passport.getField("iyr")) &&
                isExpirationYearValid(passport.getField("eyr")) &&
                isHeightValid(passport.getField("hgt")) &&
                isHairColorValid(passport.getField("hcl")) &&
                isEyeColorValid(passport.getField("ecl")) &&
                isPassportIdValid(passport.getField("pid"));
    }

    private boolean isBirthYearValid(String byr) {
        return isFourDigitNumberInRange(byr, 1920,2002);
    }

    private boolean isIssueYearValid(String iyr) {
        return isFourDigitNumberInRange(iyr, 2010,2020);
    }

    private boolean isExpirationYearValid(String eyr) {
        return isFourDigitNumberInRange(eyr, 2020,2030);
    }

    private boolean isFourDigitNumberInRange(String input, int low, int high) {
        if(input.length() != 4) {
            return false;
        }

        try {
            final int value = Integer.parseInt(input);
            return low <= value && value <= high;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isHeightValid(String hgt) {
        final String unit = hgt.substring(hgt.length() - 2);
        if(unit.equals("cm") && hgt.length() == 5) {
            try {
                int integer = Integer.parseInt(hgt.substring(0, 3));
                return 150 <= integer && integer <= 193;
            } catch (NumberFormatException e) {
                return false;
            }
        } else if (unit.equals("in") && hgt.length() == 4) {
            try {
                int integer = Integer.parseInt(hgt.substring(0, 2));
                return 59 <= integer && integer <= 76;
            } catch (NumberFormatException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean isHairColorValid(String hcl) {
        if(Pattern.compile("#[0-9a-f]{6}").matcher(hcl).find()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isEyeColorValid(String ecl) {
        final List<String> validColors = List.of("amb", "blu", "brn", "gry", "grn", "hzl", "oth");
        return validColors.contains(ecl);
    }

    private boolean isPassportIdValid(String pid) {
        if(pid.length() != 9) {
            return false;
        }

        try {
            Integer.parseInt(pid);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }
}


public class Day4 {
public static void main(String[] args) throws IOException {
    final List<String> lines = Files.readAllLines(Paths.get("input", "day4"));

    final List<Passport> passports = new LinkedList<>();
    final List<String> accumulator = new LinkedList<>();
    for(String line : lines) {
        if(line.isEmpty()) {
            passports.add(new Passport(accumulator));
            accumulator.clear();
        } else {
            accumulator.add(line);
        }
    }
    passports.add(new Passport(accumulator));

    final Validator validator = new SimpleValidator();
    final int validPassports = new LinkedList<>(passports).stream()
            .mapToInt(passport -> validator.isValid(passport) ? 1 : 0)
            .sum();

    System.out.println("Valid Passports: " + validPassports);

    final Validator strictValidator = new StrictValidator();
    final int strictlyValidPassports = new LinkedList<>(passports).stream()
            .mapToInt(passport -> strictValidator.isValid(passport) ? 1 : 0)
            .sum();

    System.out.println("Strictly valid Passports: " + strictlyValidPassports);
}
}
