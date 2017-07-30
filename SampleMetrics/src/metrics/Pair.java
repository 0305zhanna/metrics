package metrics;

/**
 * Created by User on 29.07.2017.
 */
public class Pair {
    private Record record;
    private Reference reference;

    public Pair(Record record, Reference reference) {
        this.record = record;
        this.reference = reference;
    }

    public Record getRec(){
        return record;
    }

    public Reference getRef(){
        return reference;
    }
}
