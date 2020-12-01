package sh.tiago.frontend_service;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import org.springframework.stereotype.Service;

@Service
@XRayEnabled
public class ValidatorService {
    public Boolean validateRequest(final String authHeader) {
        return true;
    }
}
