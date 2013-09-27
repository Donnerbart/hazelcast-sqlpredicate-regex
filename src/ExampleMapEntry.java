import java.io.Serializable;

public class ExampleMapEntry implements Serializable {
    private int nr;
    private String name;

    public ExampleMapEntry(int nr, String name) {
        this.nr = nr;
        this.name = name;
    }

    public int getNr() {
        return nr;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "ExampleMapEntry{" +
                "nr=" + nr +
                ", name='" + name + '\'' +
                '}';
    }
}
