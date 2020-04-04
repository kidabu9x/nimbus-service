package vn.com.nimbus.common.model.response;

import vn.com.nimbus.common.exception.AppException;
import vn.com.nimbus.common.exception.AppExceptionCode;
import vn.com.nimbus.common.exception.AppSecurityException;
import vn.com.nimbus.common.exception.HttpClientException;
import vn.com.nimbus.common.exception.AppValidatorException;
import vn.com.nimbus.common.model.paging.LimitOffsetPageable;
import vn.com.nimbus.common.model.paging.Pageable;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

public class BaseResponseAdapterImpl implements BaseResponseAdapter{
    private Object singleObject;
    private Collection items;
    private Pageable pageable;
    private LimitOffsetPageable limitOffsetPageable;
    private RuntimeException throwable;

    private boolean singleResponse = false;

    public BaseResponseAdapterImpl() {
        this.singleResponse = true;
        this.singleObject = null;
    }

    public BaseResponseAdapterImpl(Object singleObject) {
        this.singleObject = singleObject;
        this.singleResponse = true;
    }

    public BaseResponseAdapterImpl(Collection items, Pageable pageable) {
        this.items = items;
        this.pageable = pageable;
        this.singleResponse = false;
    }

    public BaseResponseAdapterImpl(Collection items, LimitOffsetPageable pageable) {
        this.items = items;
        this.limitOffsetPageable = pageable;
        this.singleResponse = false;
    }

    public BaseResponseAdapterImpl(AppException throwable) {
        this.throwable = throwable;
    }

    public BaseResponseAdapterImpl(HttpClientException throwable) { this.throwable = throwable; }

    public BaseResponseAdapterImpl(AppValidatorException throwable) { this.throwable = throwable; }

    public BaseResponseAdapterImpl(AppSecurityException throwable) {
        this.throwable = throwable;
    }

    @Override
    public BaseResponse getBaseResponse() {
        BaseResponse response = new BaseResponse();
        // make error response
        if (Objects.nonNull(throwable)) {
            AppExceptionCode AppExceptionCode;
            if (throwable instanceof AppException) {
                AppException exception = (AppException) throwable;
                AppExceptionCode = exception.getCode();
            } else if (throwable instanceof AppSecurityException) {
                AppSecurityException exception = (AppSecurityException) throwable;
                AppExceptionCode = exception.getCode();
            } else if (throwable instanceof AppValidatorException) {
                AppValidatorException exception = (AppValidatorException) throwable;
                AppExceptionCode = exception.getCode();
            } else {
                HttpClientException exception = (HttpClientException) throwable;
                AppExceptionCode = exception.getCode();
            }
            boolean isParentErrorCode = Objects.isNull(AppExceptionCode.getSubErrorCode());

            BaseResponse.Meta.MetaBuilder metaBuilder = new BaseResponse.Meta.MetaBuilder()
                    .code(AppExceptionCode.getParentErrorCode())
                    .message(AppExceptionCode.getParentErrorMessage());

            if (!isParentErrorCode) {
                metaBuilder.errors(Collections.singletonList(new BaseResponse.ErrorMessageCode(AppExceptionCode.getSubErrorCode(),
                        AppExceptionCode.getSubErrorMessage())));
            }
            if (throwable instanceof AppValidatorException) {
                metaBuilder.errors(((AppValidatorException) throwable).getErrorMessages()
                        .stream()
                        .map(validatorErrorMessage -> new BaseResponse.ErrorMessageCode(validatorErrorMessage.getFieldName(),
                                validatorErrorMessage.getErrorMessage()))
                        .collect(Collectors.toList()));
            }
            response.setMeta(metaBuilder.build());

        } else {
            // if the response is single response
            if (singleResponse) {
                response.setData(singleObject);
                BaseResponse.Meta meta = new BaseResponse.Meta.MetaBuilder()
                        .code(200)
                        .build();
                response.setMeta(meta);
            } else {// if the response is array response
                response.setData(items);
                BaseResponse.Meta.MetaBuilder metaBuilder = new BaseResponse.Meta.MetaBuilder()
                        .code(200);

                // if it use page-pageSize pageable
                if (Objects.nonNull(pageable)) {
                    metaBuilder.page(pageable.getPage())
                            .pageSize(pageable.getPageSize())
                            .total(pageable.getTotal());
                }
                // if it use limit-offset pageable
                if (Objects.nonNull(limitOffsetPageable)) {
                    metaBuilder.limit(limitOffsetPageable.getLimit())
                            .offset(limitOffsetPageable.getOffset())
                            .total(limitOffsetPageable.getTotal());
                }

                response.setMeta(metaBuilder.build());
            }
        }

        return response;
    }
}
