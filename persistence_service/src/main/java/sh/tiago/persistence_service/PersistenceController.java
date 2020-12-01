package sh.tiago.persistence_service;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@XRayEnabled
@RestController
public class PersistenceController {
    private final MessageRepository messageRepository;

    public PersistenceController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }
    
    @PostMapping("/message")
    public Boolean persistMessage(@RequestBody String message) {
        this.messageRepository.save(new Message(message));
        return true;
    }
}
