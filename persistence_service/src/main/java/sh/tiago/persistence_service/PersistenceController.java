package sh.tiago.persistence_service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.handlers.TracingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazonaws.xray.spring.aop.XRayEnabled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@XRayEnabled
@RestController
public class PersistenceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceController.class);

    private final MessageRepository messageRepository;
    private final AmazonDynamoDB dynamoDB;

    public PersistenceController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
        this.dynamoDB = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.fromName(System.getenv("AWS_REGION")))
                .withRequestHandlers(new TracingHandler(AWSXRay.getGlobalRecorder()))
                .build();
    }
    
    @PostMapping("/message")
    public Boolean persistMessage(@RequestBody String message) {
        final Message persistedMessage = this.persistOnMySQL(message);
        this.persistOnDynamoDB(message, persistedMessage.getId().toString());
        return true;
    }

    private Message persistOnMySQL(final String message) {
        LOGGER.info("persisting message on DB!");
        final Message persistedMessage = this.messageRepository.save(new Message(message));
        LOGGER.info("message persisted!");
        return persistedMessage;
    }

    private Boolean persistOnDynamoDB(final String message, final String id) {
        final Map<String, AttributeValue> dynamoAttributes = new HashMap<>();
        dynamoAttributes.put("id", new AttributeValue().withS(id));
        dynamoAttributes.put("message", new AttributeValue().withS(message));
        this.dynamoDB.putItem("xray_messages", dynamoAttributes);
        return true;
    }
}
