package ro.deiutzblaxo.cloud.data.cache.objects;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Value<X> {

    X value;
    long expirationEpoch;


}
