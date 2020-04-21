package vn.com.nimbus.common.service;

import reactor.core.publisher.Mono;
import vn.com.nimbus.common.model.paging.LimitOffsetPageable;
import vn.com.nimbus.common.model.paging.Paging;
import vn.com.nimbus.common.model.response.BasePublicResponse;
import vn.com.nimbus.common.model.response.FeatureResponse;

public interface BlogPublicService {
    Mono<Object> getBlog(String blogSlug, LimitOffsetPageable limitOffsetPageable);

    Mono<FeatureResponse> getFeature();

    Mono<Paging<BasePublicResponse>> searchBlog(String title, LimitOffsetPageable limitOffsetPageable);
}
