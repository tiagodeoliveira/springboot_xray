package sh.tiago.manage_service;

import com.amazonaws.xray.proxies.apache.http.HttpClientBuilder;
import com.amazonaws.xray.spring.aop.XRayEnabled;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
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

    private final CloseableHttpClient httpclient;

    public ManageController() {
        this.httpclient = HttpClientBuilder.create().build();
    }

    @PostMapping("/reservations")
    public String createReservation(@RequestBody String body) throws IOException {
        persistMessage(body);
        if (body.startsWith("ERROR")) {
            throw new IOException("Impossible to process your message");
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
        final CloseableHttpResponse httpResponse = this.httpclient.execute(httpPost);
    }
}
