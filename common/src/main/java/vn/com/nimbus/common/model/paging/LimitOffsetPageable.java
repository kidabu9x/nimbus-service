package vn.com.nimbus.common.model.paging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@AllArgsConstructor
@ToString
public class LimitOffsetPageable {

    public static final int DEFAULT_PAGE_LIMIT = 10;
    public static final int DEFAULT_PAGE_OFFSET = 0;

    private int limit;
    private int offset;
    private Long total;

    public LimitOffsetPageable() {
        this.limit = DEFAULT_PAGE_LIMIT;
        this.offset = DEFAULT_PAGE_OFFSET;
    }

    public LimitOffsetPageable(LimitOffsetPageable pageable) {
        this.limit = pageable.limit;
        this.offset = pageable.offset;
        this.total = pageable.total;
    }

    public LimitOffsetPageable(int limit) {
        this.limit = limit;
        this.offset = DEFAULT_PAGE_OFFSET;
    }

    public LimitOffsetPageable(int limit, int offset) {
        this.limit = limit;
        this.offset = offset;
    }

    public int getOffset() {
        return Math.max(offset, 0);
    }

    public int getLimit() {
        return limit < 0 ? DEFAULT_PAGE_LIMIT : limit;
    }

    public Long getTotal() {
        return total;
    }
}
