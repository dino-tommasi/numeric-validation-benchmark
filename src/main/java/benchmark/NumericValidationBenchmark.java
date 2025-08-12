import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class NumericValidationBenchmark {

    private static final Pattern VALIDATE_NUMERIC_PATTERN_DECIMAL =
            Pattern.compile("^\\d+(\\.\\d+)?$");

    private String[] testValues;

    @Setup
    public void setup() {
        testValues = new String[] {
                "1234567890", "12345.67890", "123.45.67", "123a45"
        };
    }

    @Benchmark
    public int regexValidation() {
        int count = 0;
        for (String value : testValues) {
            if (VALIDATE_NUMERIC_PATTERN_DECIMAL.matcher(value).matches()) {
                count++;
            }
        }
        return count;
    }

    @Benchmark
    public int loopValidation() {
        int count = 0;
        for (String value : testValues) {
            if (isNumericLoop(value)) {
                count++;
            }
        }
        return count;
    }

    @Benchmark
    public int parseValidation() {
        int count = 0;
        for (String value : testValues) {
            if (isNumericParse(value)) {
                count++;
            }
        }
        return count;
    }

    private boolean isNumericLoop(String value) {
        if (value == null || value.isEmpty()) return false;
        boolean dotSeen = false;
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (ch == '.') {
                if (dotSeen) return false;
                dotSeen = true;
            } else if (!Character.isDigit(ch)) {
                return false;
            }
        }
        return true;
    }

    private boolean isNumericParse(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
