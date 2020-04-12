package vn.com.nimbus.blog.internal.controller;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vn.com.nimbus.common.controller.AbstractController;
import vn.com.nimbus.common.model.response.BaseResponse;
import vn.com.nimbus.common.service.ImageService;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/images")
public class ImageController extends AbstractController {

    @Resource
    private ImageService imageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Flux<Object> upload(
            @RequestBody Flux<Part> parts
    ) {
        Flux<Object> results = parts.filter(part -> part instanceof FilePart).ofType(FilePart.class).flatMap(i -> imageService.uploadImage(i));
        return results;
    }
}
