package sh.tiago.frontend_service;

import com.amazonaws.util.IOUtils;
import com.amazonaws.xray.AWSXRayRecorder;
import com.amazonaws.xray.AWSXRayRecorderBuilder;
import com.amazonaws.xray.proxies.apache.http.HttpClientBuilder;
import com.amazonaws.xray.spring.aop.XRayEnabled;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@XRayEnabled
@RestController
public class AdmissionController {

    private final ValidatorService validatorService;
    private final CloseableHttpClient httpclient;
    private final AWSXRayRecorder awsxRayRecorder;

    public AdmissionController(ValidatorService validatorService) {
        this.validatorService = validatorService;
        this.httpclient = HttpClientBuilder.create().build();
        this.awsxRayRecorder = AWSXRayRecorderBuilder.standard().build();
    }

    @GetMapping("/reservations/search")
    public ResponseEntity searchSlots(@RequestHeader(name = "x-someheader") String authHeader) throws IOException {
        this.validatorService.validateRequest(authHeader);
        final CloseableHttpResponse httpResponse = this.httpclient.execute(new HttpGet("http://search:8082/reservations/search"));
        return createResponse(IOUtils.toString(httpResponse.getEntity().getContent()));
    }

    @PostMapping("/reservations")
    public ResponseEntity createReservation(@RequestHeader(name = "x-someheader") String authHeader, @RequestBody String body) throws IOException {
        this.validatorService.validateRequest(authHeader);
        final HttpPost httpPost = new HttpPost("http://manage:8081/reservations");
        httpPost.setEntity(new StringEntity(body));
        final CloseableHttpResponse httpResponse = this.httpclient.execute(httpPost);
        return createResponse(IOUtils.toString(httpResponse.getEntity().getContent()));
    }

    @GetMapping("/reservations")
    public ResponseEntity listReservations(@RequestHeader(name = "x-someheader") String authHeader) throws IOException {
        this.validatorService.validateRequest(authHeader);
        final CloseableHttpResponse httpResponse = this.httpclient.execute(new HttpGet("http://manage:8081/reservations"));
        return createResponse(IOUtils.toString(httpResponse.getEntity().getContent()));
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity deleteReservation(@RequestHeader(name = "x-someheader") String authHeader, @PathVariable("id") final String id) throws IOException {
        this.validatorService.validateRequest(authHeader);
        final CloseableHttpResponse httpResponse = this.httpclient.execute(new HttpDelete(String.format("http://manage:8081/reservations/%s", id)));
        return createResponse(IOUtils.toString(httpResponse.getEntity().getContent()));
    }

    private ResponseEntity createResponse(final Object content) {
        final HttpHeaders responseHeaders = new HttpHeaders();
        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(content);
    }
}