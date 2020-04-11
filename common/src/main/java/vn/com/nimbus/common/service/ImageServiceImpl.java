package vn.com.nimbus.common.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import vn.com.nimbus.common.config.ConfigData;
import vn.com.nimbus.common.config.ConfigLoader;
import vn.com.nimbus.common.exception.AppException;
import vn.com.nimbus.common.exception.AppExceptionCode;
import vn.com.nimbus.common.model.response.UploadImageResponse;
import vn.com.nimbus.common.utils.FileUtils;
import vn.com.nimbus.common.utils.StreamUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class ImageServiceImpl implements ImageService {
    private Cloudinary uploader;

    private Uploader getUploader() {
        ConfigData.CloudinaryConfig cloudinaryConfig = ConfigLoader.getInstance().configData.getCloudinaryConfig();
        if (uploader == null) {
            uploader = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", cloudinaryConfig.getCloudName(),
                    "api_key", cloudinaryConfig.getApiKey(),
                    "api_secret", cloudinaryConfig.getApiSecret())
            );
        }
        return uploader.uploader();
    }

    @Override
    public Mono<UploadImageResponse> uploadImage(String filename, InputStream stream) {
        String url = this.upload(filename, stream);
        UploadImageResponse response = new UploadImageResponse();
        response.setUrl(url);
        return Mono.just(response);
    }

    private String upload(String filename, InputStream stream) {
        ConfigData.CloudinaryConfig config = ConfigLoader.getInstance().configData.getCloudinaryConfig();


        Optional<String> fileFormat = FileUtils.getExtension(filename);
        if (!(fileFormat.isPresent() && config.getSupportedFileFormats().contains(fileFormat.get())))
            throw new AppException(AppExceptionCode.UNSUPPORTED_IMAGE_FORMAT);

        try {
            File file = StreamUtils.stream2file(stream);

            Map<String, String> params = new HashMap<>();
            params.put("public_id", config.getFolder().concat("/").concat(Objects.requireNonNull(filename)));

            Map result = this.getUploader().upload(file, params);
            FileUtils.delete(file);
            return result.get("secure_url").toString();
        } catch (IOException e) {
            log.error("Fail to parse multipart to file, ex: {}", e.getMessage());
            throw new AppException(AppExceptionCode.FAIL_TO_CONVERT_FILE);
        } catch(Exception e) {
            e.printStackTrace();
            log.error("Fail to upload image. ex: {}", e.getMessage());
            throw new AppException(AppExceptionCode.FAIL_TO_UPLOAD_IMAGE);
        }

    }
}
