package vn.com.nimbus.common.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vn.com.nimbus.common.model.response.BlogPublicDetailResponse;
import vn.com.nimbus.common.model.response.BlogPublicResponse;

public interface BlogPublicService {
    Mono<BlogPublicDetailResponse> getBlog(String blogSlug);

    Flux<BlogPublicResponse> getBySlug(String slug);

}
