package ro.deiutzblaxo.cloud.utils.objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

    @Override
    public String toString() {
        return "Triplet{" +
                "first=" + first +
                ", middle=" + middle +
                ", last=" + last +
                '}';
    }
}
