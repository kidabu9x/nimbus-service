package vn.com.nimbus.blog.internal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import vn.com.nimbus.blog.internal.service.HachiumService;
import vn.com.nimbus.common.model.response.BaseResponse;

@RestController
@RequestMapping("/v1/hachium")
public class HachiumController {
    private final HachiumService hachiumService;

    @Autowired
    public HachiumController(HachiumService hachiumService) {
        this.hachiumService = hachiumService;
    }

    @PostMapping("/sync-data")
    public Mono<BaseResponse<Boolean>> syncData() {
        return Mono.just(hachiumService.syncData()).map(BaseResponse::ofSucceeded);
    }
}
