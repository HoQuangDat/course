package guild.api.security.controller;

import guild.api.security.request.AuthenticationRequest;
import guild.api.security.request.RegisterRequest;
import guild.api.security.response.AuthenticationResponse;
import guild.api.security.service.interfaces.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    //store tokens after logout
    private final Set<String> tokenBlacklist = new HashSet<>();

    @Autowired
    private IAuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        // Extract the JWT token from the "Authorization" header
        String jwtToken = token.replace("Bearer ", "");

        // Add the token to the blacklist
        tokenBlacklist.add(jwtToken);

        return ResponseEntity.ok("Logged out successfully.");
    }

    // Endpoint to check if a token is blacklisted (optional)
    @GetMapping("/checkToken")
    public ResponseEntity<?> checkTokenLogout(@RequestParam String token) {
        //check token exists in black list
        if (tokenBlacklist.contains(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is blacklisted.");
        } else {
            return ResponseEntity.ok("Token is valid.");
        }
    }

    //validToken
    @GetMapping("/valid-token")
    public ResponseEntity<?> validToken(@RequestHeader("Authorization") String token, @RequestParam("role") String role) {
        return authService.isValidToken(token, role);
    }
}
