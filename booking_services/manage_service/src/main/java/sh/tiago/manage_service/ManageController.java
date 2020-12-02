package sh.tiago.manage_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazonaws.xray.proxies.apache.http.HttpClientBuilder;
import com.amazonaws.xray.spring.aop.XRayEnabled;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

@XRayEnabled
@RestController
public class ManageController {

    private static final int REQUEST_TIMEOUT_IN_SECS = 2;
    private static final Logger LOGGER = LoggerFactory.getLogger(ManageController.class);

    private final CloseableHttpClient httpclient;

    public ManageController() {
        final RequestConfig config = RequestConfig.custom()
            .setConnectTimeout(REQUEST_TIMEOUT_IN_SECS * 1000)
            .setConnectionRequestTimeout(REQUEST_TIMEOUT_IN_SECS * 1000)
            .setSocketTimeout(REQUEST_TIMEOUT_IN_SECS * 1000).build();

        this.httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
    }

    @PostMapping("/reservations")
    public String createReservation(@RequestBody String body) throws IOException {
        LOGGER.info("Creating reservation");
        persistMessage(body);
        if (body.startsWith("ERROR")) {
            throw new IOException(String.format("Hey hey, it was not possible to process your message. %s", body));
        }
        return String.format("%s - done at %d", body, System.currentTimeMillis());
    }

    @GetMapping("/reservations")
    public List<Object> listReservations() throws IOException {
        persistMessage("List Reservation");
        return Arrays.asList("reservation 1", "reservation 2");
    }

    @DeleteMapping("/reservations/{id}")
    public String deleteReservation(final String id) throws IOException {
        persistMessage(String.format("Delete Reservation: %s", id));
        return String.format("Deleted: %s", id);
    }

    private void persistMessage(final String message) throws IOException {
        final HttpPost httpPost = new HttpPost("http://persistence:8083/message");
        httpPost.setEntity(new StringEntity(message));
        LOGGER.info("Persisting message");
        final CloseableHttpResponse httpResponse = this.httpclient.execute(httpPost);
        httpResponse.close();
    }
}
