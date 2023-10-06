package guild.api.security.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
public class HelloController {

    @GetMapping(value = "/v1")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String helloAdmin(){
        return "Accessing the ADMIN function successfully";
    }

    @GetMapping(value = "/v2")
    @PreAuthorize("hasAuthority('USER')")
    public String helloUser(){
        return "Accessing the USER function successfully";
    }
}
