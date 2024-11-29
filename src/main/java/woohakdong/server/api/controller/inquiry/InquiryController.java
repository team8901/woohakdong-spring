package woohakdong.server.api.controller.inquiry;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import woohakdong.server.api.controller.ListWrapperResponse;
import woohakdong.server.api.controller.inquiry.dto.InquiryRequest;
import woohakdong.server.api.controller.inquiry.dto.InquiryResponse;
import woohakdong.server.api.service.inquiry.InquiryService;

import java.util.List;

@RestController
@RequestMapping("/v1/members/inquiries")
@RequiredArgsConstructor
public class InquiryController implements InquiryControllerDocs{
    private final InquiryService inquiryService;

    @PostMapping
    public void createInquiry(@RequestBody InquiryRequest inquiryRequest) {
        inquiryService.createInquiry(inquiryRequest);
    }

    @GetMapping
    public ListWrapperResponse<InquiryResponse> getInquiries() {
        List<InquiryResponse> inquiries = inquiryService.getInquiries();
        return ListWrapperResponse.of(inquiries);
    }
}
