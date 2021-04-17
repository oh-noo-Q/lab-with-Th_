package entities.testdata.comparable;

public interface IValueComparable extends IComparable {
    String assertEqual(String expected, String actual);
    String assertNotEqual(String expected, String actual);
    String assertLower(String expected, String actual);
    String assertGreater(String expected, String actual);
    String assertLowerOrEqual(String expected, String actual);
    String assertGreaterOrEqual(String expected, String actual);
}
