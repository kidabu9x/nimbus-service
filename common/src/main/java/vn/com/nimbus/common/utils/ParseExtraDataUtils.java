package vn.com.nimbus.common.utils;

import org.springframework.util.StringUtils;
import vn.com.nimbus.data.domain.Blogs;
import vn.com.nimbus.common.model.extra.BlogExtraData;
import vn.com.nimbus.common.service.JsonParseService;

public class ParseExtraDataUtils {
    public static BlogExtraData parseBlogExtraData(Blogs blog) {
        BlogExtraData extraData;
        if (blog.getExtraData() != null) {
            JsonParseService<BlogExtraData> jsonParseService = new JsonParseService<>();
            extraData = jsonParseService.toEntityData(blog.getExtraData(), BlogExtraData.class);
            if (StringUtils.isEmpty(extraData.getFacebookPixelId())) {
                extraData.setFacebookPixelId("");
            }
        } else {
            extraData = new BlogExtraData();
            extraData.setFacebookPixelId("");
        }
        return extraData;
    }
}
