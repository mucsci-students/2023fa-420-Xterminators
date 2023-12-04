package xterminators.spellingbee.model;

import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;

/**
 * I hate this, but it's necessary to sort
 * the scores TreeMap by values rather than keys.
 */
class ValueComparator implements Comparator<String> {
    Map<String, Integer> base;

    public ValueComparator() {
        this.base = new HashMap<>();
    }

    public ValueComparator(Map<String, Integer> base) {
        this.base = base;
    }

    // Compare method to sort by values
    public int compare(String a, String b) {
        if (base.get(a) == null) return -1;
        if (base.get(b) == null) return 1;
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        }
    }
}