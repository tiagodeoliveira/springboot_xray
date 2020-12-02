package sh.tiago.persistence_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazonaws.xray.spring.aop.XRayEnabled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@XRayEnabled
@RestController
public class PersistenceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceController.class);

    private final MessageRepository messageRepository;

    public PersistenceController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
        
    }
    
    @PostMapping("/message")
    public Boolean persistMessage(@RequestBody String message) {
        LOGGER.info("persisting message on DB!");
        this.messageRepository.save(new Message(message));
        LOGGER.info("message persisted!");
        return true;
    }
}
