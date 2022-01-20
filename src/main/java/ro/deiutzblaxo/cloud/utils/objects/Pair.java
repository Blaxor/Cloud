package ro.deiutzblaxo.cloud.utils.objects;

public class Pair<F, L> {
    //facts : FL is from fuck life...

    private F first;

    private L last;


    public Pair(F f, L l) {
        first = f;

        last = l;
    }

    public F getFirst() {
        return first;
    }

    public void setFirst(F first) {
        this.first = first;
    }

    public L getLast() {
        return last;
    }

    public void setLast(L last) {
        this.last = last;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "first=" + first +
                ", last=" + last +
                '}';
    }


}
