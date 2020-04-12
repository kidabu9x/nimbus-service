package vn.com.nimbus.common.service;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;
import vn.com.nimbus.common.model.response.UploadImageResponse;

import java.io.InputStream;

public interface ImageService {
    Mono<UploadImageResponse> uploadImage(FilePart file);
}
