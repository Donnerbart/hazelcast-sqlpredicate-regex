import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.SqlPredicate;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Main {
    private static final String regex = "[^abc].{3}$";

    public static void main(String[] args) {
        // Create Hazelcast instance
        final Config cfg = new Config();
        final HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(cfg);
        final HazelcastInstance instance2 = Hazelcast.newHazelcastInstance(cfg);

        try {
            // Initialize map
            final IMap<Integer, ExampleMapEntry> map1 = instance1.getMap("sqlRegex");
            final IMap<Integer, ExampleMapEntry> map2 = instance2.getMap("sqlRegex");

            int index = 0;
            map1.put(index++, new ExampleMapEntry(index, "a"));
            map2.put(index++, new ExampleMapEntry(index, "b"));
            map1.put(index++, new ExampleMapEntry(index, "c"));
            map2.put(index++, new ExampleMapEntry(index, "abc"));
            map1.put(index++, new ExampleMapEntry(index, "abcd"));
            map2.put(index++, new ExampleMapEntry(index, "abcde"));
            map1.put(index++, new ExampleMapEntry(index, "abcdef"));
            map2.put(index++, new ExampleMapEntry(index, "abcabc"));
            map1.put(index++, new ExampleMapEntry(index, "defdef"));
            map2.put(index++, new ExampleMapEntry(index, "abcfabc"));
            map1.put(index++, new ExampleMapEntry(index, "asdaffas"));
            map2.put(index++, new ExampleMapEntry(index, "aabc"));
            map1.put(index++, new ExampleMapEntry(index, "babc"));
            map2.put(index++, new ExampleMapEntry(index, "cabc"));
            map1.put(index++, new ExampleMapEntry(index, "dabc"));
            map2.put(index++, new ExampleMapEntry(index, "eabc"));
            map1.put(index++, new ExampleMapEntry(index, "fabc"));
            map2.put(index++, new ExampleMapEntry(index, "adef"));
            map1.put(index++, new ExampleMapEntry(index, "bdef"));
            map2.put(index++, new ExampleMapEntry(index, "cdef"));
            map1.put(index++, new ExampleMapEntry(index, "ddef"));
            map2.put(index++, new ExampleMapEntry(index, "edef"));
            map1.put(index++, new ExampleMapEntry(index, "fdef"));

            // Filter with native Java regular expression
            final List<String> expectedValues = new LinkedList<String>();
            for (ExampleMapEntry exampleMapEntry : map1.values()) {
                final String name = exampleMapEntry.getName();
                if (exampleMapEntry.getNr() < 20 && name.matches(regex)) {
                    expectedValues.add(name);
                }
            }

            // Filter with Hazelcast SQLPredicate
            final List<String> actualValues = new LinkedList<String>();
            Set<ExampleMapEntry> subset = (Set<ExampleMapEntry>) map2.values(new SqlPredicate("nr < 20 AND name REGEX '" + regex + "'"));
            for (ExampleMapEntry exampleMapEntry : subset) {
                actualValues.add(exampleMapEntry.getName());
            }

            // Sort results to compare them
            Collections.sort(expectedValues);
            Collections.sort(actualValues);

            // Show results
            System.out.println("Total entries in map:");
            System.out.println(index);
            System.out.println();
            System.out.println("Filtered results with native Java:");
            System.out.println(expectedValues);
            System.out.println();
            System.out.println("Filtered results with Hazelcast SQLPredicate:");
            System.out.println(actualValues);
            System.out.println();
            System.out.println("Do the results match?");
            System.out.println(expectedValues.equals(actualValues) ? "yes" : "no");
            System.out.println();

        } finally {
            // Shutdown all Hazelcast instance
            Hazelcast.shutdownAll();
        }
    }
}
