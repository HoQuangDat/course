package guild.api.security.service.interfaces;

import guild.api.security.request.AuthenticationRequest;
import guild.api.security.request.RegisterRequest;
import guild.api.security.response.AuthenticationResponse;
import org.springframework.http.ResponseEntity;

public interface IAuthService {
    public ResponseEntity<Object> register(RegisterRequest request);

    public AuthenticationResponse login(AuthenticationRequest request);

    public ResponseEntity<Object> isValidToken(String token, String role);
}
