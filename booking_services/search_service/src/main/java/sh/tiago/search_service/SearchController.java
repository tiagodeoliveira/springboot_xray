package sh.tiago.search_service;

import com.amazonaws.util.IOUtils;
import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.AWSXRayRecorder;
import com.amazonaws.xray.AWSXRayRecorderBuilder;
import com.amazonaws.xray.contexts.SegmentContextExecutors;
import com.amazonaws.xray.entities.Entity;
import com.amazonaws.xray.entities.Subsegment;
import com.amazonaws.xray.proxies.apache.http.HttpClientBuilder;
import com.amazonaws.xray.spring.aop.XRayEnabled;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@XRayEnabled
@RestController
public class SearchController {

    private final CloseableHttpClient httpclient;
    private final AWSXRayRecorder awsxRayRecorder;

    public SearchController() {
        this.httpclient = HttpClientBuilder.create().build();
        this.awsxRayRecorder = AWSXRayRecorderBuilder.standard().build();
    }

    @GetMapping("/reservations/search")
    public List<Object> searchSlots() throws IOException, InterruptedException {
        final ExecutorService threadpool = Executors.newFixedThreadPool(2);
        final Entity segment = awsxRayRecorder.getTraceEntity();

        threadpool.execute(() -> persistMessage("Searching for slot", segment));
        threadpool.execute(() -> listReservations(segment));
        threadpool.awaitTermination(1, TimeUnit.SECONDS);
        threadpool.shutdown();

        return Arrays.asList("slot 1", "slot 2");
    }

    private void listReservations(Entity segment) {
        segment.run(() -> {
            Subsegment subsegment = AWSXRay.beginSubsegment("## Listing Reservations");
            try {
                final CloseableHttpResponse httpResponse = this.httpclient.execute(new HttpGet("http://manage:8081/reservations"));
                IOUtils.toString(httpResponse.getEntity().getContent());
                httpResponse.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            subsegment.close();
            AWSXRay.endSubsegment();
        });
    }

    private void persistMessage(final String message, Entity segment) {
        segment.run(() -> {
            Subsegment subsegment = AWSXRay.beginSubsegment("## Persisting Message");
            try {
                final HttpPost httpPost = new HttpPost("http://persistence:8083/message");
                httpPost.setEntity(new StringEntity(message));
                final CloseableHttpResponse httpResponse = this.httpclient.execute(httpPost);
                httpResponse.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            AWSXRay.endSubsegment();
        });
    }
}
