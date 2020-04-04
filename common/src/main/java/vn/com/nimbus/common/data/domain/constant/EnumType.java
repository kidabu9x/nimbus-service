package vn.com.nimbus.common.data.domain.constant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface EnumType<E extends Enum<E>> {
    Class<E> getDeclaringClass();

    default E lookup(String name) {
        try {
            return Enum.valueOf(getDeclaringClass(), name);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    default Collection<E> lookup(List<String> names) {
        Collection<E> collectionE = new ArrayList<>();
        for(String name : names) {
            collectionE.add(lookup(name));
        }
        return collectionE;
    }
}