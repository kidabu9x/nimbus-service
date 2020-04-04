package vn.com.nimbus.common.controller;

import reactor.core.publisher.Mono;
import vn.com.nimbus.common.model.paging.Paging;
import vn.com.nimbus.common.model.response.BaseResponse;
import vn.com.nimbus.common.model.response.BaseResponseAdapter;
import vn.com.nimbus.common.model.response.BaseResponseAdapterImpl;

import java.util.Objects;

public abstract class AbstractController {
    protected <T> Mono<BaseResponse> processBaseResponse(Mono<T> r) {
        return r.flatMap(data -> {
            if (Objects.isNull(data)) {
                BaseResponseAdapter responseAdapter = new BaseResponseAdapterImpl();
                return Mono.just(responseAdapter.getBaseResponse());
            } else if (data instanceof Paging) {
                Paging paging = (Paging) data;
                BaseResponseAdapter responseAdapter = Objects.nonNull(paging.getPageable()) ?
                        new BaseResponseAdapterImpl(paging.getItems(), paging.getPageable()) :
                        new BaseResponseAdapterImpl(paging.getItems(), paging.getLimitOffsetPageable());
                return Mono.just(responseAdapter.getBaseResponse());
            } else {
                BaseResponseAdapter responseAdapter = new BaseResponseAdapterImpl(data);
                return Mono.just(responseAdapter.getBaseResponse());
            }
        });
    }

    protected <T> Mono<BaseResponse> processBaseResponse() {
        BaseResponseAdapter responseAdapter = new BaseResponseAdapterImpl();
        return Mono.just(responseAdapter.getBaseResponse());
    }
}
