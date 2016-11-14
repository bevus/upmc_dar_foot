package init;

/**
 * Created by Hacene on 14/11/2016.
 */
public class Pair<T, K> {
    private T key;
    private K value;

    public Pair(T key, K value) {
        this.key = key;
        this.value = value;
    }

    public T getKey() {
        return key;
    }

    public void setKey(T key) {
        this.key = key;
    }

    public K getValue() {
        return value;
    }

    public void setValue(K value) {
        this.value = value;
    }
}
