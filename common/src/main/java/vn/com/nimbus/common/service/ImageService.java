package vn.com.nimbus.common.service;

import reactor.core.publisher.Mono;
import vn.com.nimbus.common.model.response.UploadImageResponse;

import java.io.InputStream;

public interface ImageService {
    Mono<UploadImageResponse> uploadImage(String filename, InputStream stream);
}
