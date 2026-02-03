package com.project.hems.auth_service_hems.controller;

import com.project.hems.auth_service_hems.model.User;
import com.project.hems.auth_service_hems.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
// @Controller("/auth")
public class UserController {
    private final UserService userService;
    private final OAuth2AuthorizedClientService clientService;

    // change user to userDto in response
    // @PostMapping("/create-user")
    // public ResponseEntity<User> createUser(@RequestBody User user){
    // return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(user,
    // user.getEmail()));
    // }

    // @GetMapping("/create-user")
    // public String createUser(Model model, @AuthenticationPrincipal OidcUser
    // oidcUser){
    //
    // if(oidcUser==null){
    // return "index";
    // }
    //
    // // Load OAuth2 client
    // OAuth2AuthorizedClient client =
    // clientService.loadAuthorizedClient(
    // "auth0",
    // oidcUser.getName()
    // );
    //
    // if (client != null) {
    // System.out.println("ACCESS TOKEN = " +
    // client.getAccessToken().getTokenValue());
    //
    // if (client.getRefreshToken() != null) {
    // System.out.println("REFRESH TOKEN = " +
    // client.getRefreshToken().getTokenValue());
    // }
    //
    // System.out.println("Current time = " + LocalDateTime.now());
    // }
    //
    //
    // String email=oidcUser.getEmail();
    // String subject=oidcUser.getSubject();
    //
    // User user=userService.loginOrRegister(email,subject);
    //
    // model.addAttribute("user",user);
    // model.addAttribute("profile", oidcUser.getClaims());
    //
    // return "index";
    // }

    @GetMapping("/create-user")
    public String createUser(@AuthenticationPrincipal Jwt jwt) {

        String email = jwt.getClaimAsString("http://hems.com/email");
        log.info("recived create user request for email {} ",email);

        String subject = jwt.getSubject();
        log.info("subject of the current request is {}", subject);

        User user = userService.loginOrRegister(email, subject);

        if (user.getId() != null) {
            return "success created user :- " + user.getEmail();
        } else {
            return "user is not created";
        }
    }

    @GetMapping("/checking")
    public String check() {
        log.info("recived request for checking route");
        return "Working";
    }

    // here we add one controller to check token is valid or not ..

    // then ek filter mukvu padse request ni pehla gateway per ave eni pehla ke
    // Bearer thi start thayyy che ke nai em tem..

}
