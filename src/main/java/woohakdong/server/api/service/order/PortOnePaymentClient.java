package woohakdong.server.api.service.order;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import woohakdong.server.api.controller.group.dto.PaymentInfoResponse;

@Component
@RequiredArgsConstructor
public class PortOnePaymentClient implements PaymentClient {

    private final IamportClient iamportClient;

    @Override
    public PaymentInfoResponse getPaymentInfo(String impUid) {
        try {
            return PaymentInfoResponse.from(iamportClient.paymentByImpUid(impUid).getResponse());
        } catch (IamportResponseException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
