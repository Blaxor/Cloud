package ro.deiutzblaxo.cloud.utils.objects;

public class Triplet<F, M, L> {
    //facts : FML is from fuck my life...

    private F first;
    private M middle;
    private L last;


    public Triplet(F f, M m, L l) {
        first = f;
        middle = m;
        last = l;
    }

    public F getFirst() {
        return first;
    }

    public void setFirst(F first) {
        this.first = first;
    }

    public M getMiddle() {
        return middle;
    }

    public void setMiddle(M middle) {
        this.middle = middle;
    }

    public L getLast() {
        return last;
    }

    public void setLast(L last) {
        this.last = last;
    }

    @Override
    public String toString() {
        return "Triplet{" +
                "first=" + first +
                ", middle=" + middle +
                ", last=" + last +
                '}';
    }
}
