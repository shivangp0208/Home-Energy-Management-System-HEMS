package com.project.hems.auth_service_hems.controller;

import com.project.hems.auth_service_hems.model.User;
import com.project.hems.auth_service_hems.service.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Controller", description = "Endpoints for user creation and authentication")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
// @Controller("/auth")
public class UserController {

    private final UserServiceImpl userService;

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

    @Operation(
            summary = "Create or login user",
            description = "Creates a new user or logs in an existing user based on JWT claims"
    )
    @ApiResponse(responseCode = "201", description = "User created or logged in successfully")
    @GetMapping("/create-user")
    public String createUser(@AuthenticationPrincipal Jwt jwt) {

        String email = jwt.getClaimAsString("http://hems.com/email");
        log.info("recived create user request for email {} ",email);

        String subject = jwt.getSubject();
        log.info("subject of the current request is {}", subject);

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

    @Operation(summary = "Check service health", description = "Returns a simple working status")
    @GetMapping("/checking")
    public String check() {
        log.info("recived request for checking route");
        return "Working";
    }

    // here we add one controller to check token is valid or not ..
    // log.info("checkToken: Validating JWT token before request processing");

    // then ek filter mukvu padse request ni pehla gateway per ave eni pehla ke
    // Bearer thi start thayyy che ke nai em tem..
    // log.debug("authFilter: Checking Authorization header starts with Bearer");
}
