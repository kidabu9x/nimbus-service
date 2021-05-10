package vn.com.nimbus.common.model.paging;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class Paging<T> implements Serializable {

    private T item;
    private List<T> items;
    private Pageable pageable;
    private LimitOffsetPageable limitOffsetPageable;

    public Paging(Pageable pageable) {
        this.items = new ArrayList<>();
        this.pageable = pageable;
    }

    public Paging(List<T> items, Pageable pageable) {
        this(pageable);
        this.items = items;
    }

    public Paging(LimitOffsetPageable pageable) {
        this.items = new ArrayList<>();
        this.limitOffsetPageable = pageable;
    }

    public Paging(List<T> items, LimitOffsetPageable pageable) {
        this(pageable);
        this.items = items;
    }

    public Paging(T item, LimitOffsetPageable pageable) {
        this(pageable);
        this.item = item;
    }

    public Paging(T item, Pageable pageable) {
        this(pageable);
        this.item = item;
    }
}
