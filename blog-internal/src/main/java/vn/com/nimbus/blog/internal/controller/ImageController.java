package vn.com.nimbus.blog.internal.controller;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import vn.com.nimbus.common.controller.AbstractController;
import vn.com.nimbus.common.model.response.BaseResponse;
import vn.com.nimbus.common.service.ImageService;

import javax.annotation.Resource;

@RestController
@RequestMapping("/v1/images")
public class ImageController extends AbstractController {

    @Resource
    private ImageService imageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<BaseResponse> upload(
            @RequestPart("file") FilePart file
    ) {
        String fileName = file.filename();
        return processBaseResponse(file.content()
                .map(DataBuffer::asInputStream)
                .map(it -> imageService.uploadImage(fileName, it))
                .next()
        );
    }
}
