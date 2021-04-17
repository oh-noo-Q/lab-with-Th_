package entities.testdata.comparable;

public interface INullableComparable extends IComparable {
    String assertNull(String name);
    String assertNotNull(String name);
}
