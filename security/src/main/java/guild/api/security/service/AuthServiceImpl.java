package guild.api.security.service;

import guild.api.security.entity.User;
import guild.api.security.repository.IUserRepository;
import guild.api.security.request.AuthenticationRequest;
import guild.api.security.request.RegisterRequest;
import guild.api.security.response.AuthenticationResponse;
import guild.api.security.service.interfaces.IAuthService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class AuthServiceImpl implements IAuthService {
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public ResponseEntity<Object> register(RegisterRequest request) {
        //check email exists in database
        var foundUser = userRepository.findByEmail(request.getEmail());

        //check user exists in table user
        if(!foundUser.isPresent())
        {
            var user = User.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .password(new BCryptPasswordEncoder().encode(request.getPassword()))
                    .role(request.getRole())
                    .build();

            userRepository.save(user);

            return ResponseEntity.ok().body("Register user successfully");
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("The Email already exists in the database");
    }

    //login user and generate token
    @Override
    public AuthenticationResponse login(AuthenticationRequest request)  {
        try {
            //send email and password from request to authenticationManager precess authentication
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            //get information user
            var user = userRepository.findByEmail(request.getEmail()).orElseThrow();

            //generate token
            var jwtToken = jwtService.generateToken(user);

            return AuthenticationResponse.builder().token(jwtToken).build();
        } catch (AuthenticationException e) {
            // return AuthenticationResponse.builder().error("Invalid credentials").build();
            throw new RuntimeException("Authentication failed", e);
        }
    }

    //Valid Token return Role
    @Override
    public ResponseEntity<Object> isValidToken(String token, String role) {
        //replace characters exists "Bearer"
        if(token == null || token.startsWith("Bearer ")){
            token = token.substring(7);
        }

        //get userName of user
        String userName = jwtService.extractUsername(token);

        //get information of user by UserName
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);

        //check token of user expired
        if(jwtService.isTokenValid(token, userDetails)){
            //get role of user
            Claims claims = jwtService.extractAllClaims(token);

            List<String> roles = claims.get("roles", List.class);
            
            if(checkRole(roles, role)){
                return ResponseEntity.ok().body("Access this API successfully");
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UNAUTHORIZED access this API");
    }

    //check roles
    public boolean checkRole(List<String> parentRoles, String subRole){
        for (String parentString : parentRoles) {
            if (parentString.contains(subRole)) {
                return true;
            }
        }
        return false;
    }
}
