package vn.com.nimbus.common.model.paging;

import lombok.AllArgsConstructor;
import lombok.Setter;

@Setter
@AllArgsConstructor
public class Pageable {

    public static final int DEFAULT_PAGE_SIZE = 10;
    private int page;
    private int pageSize;
    private Long total;

    public Pageable(Pageable pageable) {
        this.page = pageable.page;
        this.pageSize = pageable.pageSize;
        this.total = pageable.total;
    }

    public Pageable(int page) {
        this.page = page;
        this.pageSize = DEFAULT_PAGE_SIZE;
    }

    public Pageable(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }

    public int getPage() {
        return (page <= 0) ? 1 : page;
    }

    public int getPageSize() {
        return (pageSize < 0) ? DEFAULT_PAGE_SIZE : pageSize;
    }

    public Long getTotal() {
        return total;
    }
}
