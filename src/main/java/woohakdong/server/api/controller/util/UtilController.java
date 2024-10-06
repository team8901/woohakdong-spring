package woohakdong.server.api.controller.util;

import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import woohakdong.server.api.controller.ListWrapperResponse;
import woohakdong.server.api.controller.util.dto.S3PresignedUrlResponse;
import woohakdong.server.api.service.util.UtilService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/utils")
public class UtilController implements UtilControllerDocs {

    private final UtilService utilService;

    @GetMapping("/images/urls")
    public ListWrapperResponse<S3PresignedUrlResponse> getPreSignedUrl(@RequestParam int imageCount) {
        return ListWrapperResponse.of(utilService.generatePresignedUrls(imageCount));
    }

}
