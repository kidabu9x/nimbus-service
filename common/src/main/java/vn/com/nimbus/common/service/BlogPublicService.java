package vn.com.nimbus.common.service;

import reactor.core.publisher.Mono;
import vn.com.nimbus.common.model.paging.LimitOffsetPageable;

public interface BlogPublicService {
    Mono<Object> getBlog(String blogSlug, LimitOffsetPageable limitOffsetPageable);

    Mono<Object> getFeature();
}
