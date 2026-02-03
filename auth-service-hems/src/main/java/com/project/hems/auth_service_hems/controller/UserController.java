package com.project.hems.auth_service_hems.controller;

import com.project.hems.auth_service_hems.model.User;
import com.project.hems.auth_service_hems.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
// @Controller("/auth")
public class UserController {

    private final UserService userService;

    // change user to userDto in response
    // @PostMapping("/create-user")
    // public ResponseEntity<User> createUser(@RequestBody User user){
    // log.info("createUser: Creating user with email={}", user.getEmail());
    // return ResponseEntity.status(HttpStatus.CREATED)
    // .body(userService.save(user, user.getEmail()));
    // }

    // @GetMapping("/create-user")
    // public String createUser(Model model, @AuthenticationPrincipal OidcUser
    // oidcUser){
    //
    // log.debug("createUser(OIDC): Entered create-user endpoint");
    //
    // if(oidcUser==null){
    // log.warn("createUser(OIDC): OidcUser is null, returning index");
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
    // log.debug("createUser(OIDC): ACCESS TOKEN = {}",
    // client.getAccessToken().getTokenValue());
    //
    // if (client.getRefreshToken() != null) {
    // log.debug("createUser(OIDC): REFRESH TOKEN = {}",
    // client.getRefreshToken().getTokenValue());
    // }
    //
    // log.debug("createUser(OIDC): Current time = {}", LocalDateTime.now());
    // }
    //
    // String email = oidcUser.getEmail();
    // String subject = oidcUser.getSubject();
    //
    // log.info("createUser(OIDC): Logging in or registering user, email={},
    // subject={}",
    // email, subject);
    //
    // User user = userService.loginOrRegister(email, subject);
    //
    // model.addAttribute("user", user);
    // model.addAttribute("profile", oidcUser.getClaims());
    //
    // log.info("createUser(OIDC): User processed successfully, userId={}",
    // user.getId());
    // return "index";
    // }

    @GetMapping("/create-user")
    public String createUser(@AuthenticationPrincipal Jwt jwt) {

        log.debug("createUser: Entered create-user API");
        log.debug("createUser: JWT received={}", jwt);

        String email = jwt.getClaimAsString("http://hems.com/email");
        log.debug("createUser: Extracted email={}", email);

        String subject = jwt.getSubject();
        log.debug("createUser: Extracted subject={}", subject);

        User user = userService.loginOrRegister(email, subject);
        log.info("createUser: loginOrRegister executed for email={}, userId={}",
                email, user.getId());

        if (user.getId() != null) {
            log.info("createUser: User created successfully, email={}", user.getEmail());
            return "success created user :- " + user.getEmail();
        } else {
            log.warn("createUser: User creation failed for email={}", email);
            return "user is not created";
        }
    }

    @GetMapping("/checking")
    public String check() {
        log.info("check: Health check endpoint called");
        return "Working";
    }

    // here we add one controller to check token is valid or not ..
    // log.info("checkToken: Validating JWT token before request processing");

    // then ek filter mukvu padse request ni pehla gateway per ave eni pehla ke
    // Bearer thi start thayyy che ke nai em tem..
    // log.debug("authFilter: Checking Authorization header starts with Bearer");
}
