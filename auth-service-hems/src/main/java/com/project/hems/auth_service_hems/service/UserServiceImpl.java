package com.project.hems.auth_service_hems.service;

import com.project.hems.auth_service_hems.exception.JwtValidationException;
import com.project.hems.auth_service_hems.exception.UserNotFoundException;
import com.project.hems.auth_service_hems.model.User;
import com.project.hems.auth_service_hems.model.UserIdentitie;
import com.project.hems.auth_service_hems.repository.UserIdentitieRepo;
import com.project.hems.auth_service_hems.repository.UserRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    // @Autowired
    // private RedisTemplate<String,String> redisTemplate;//to talk with redis

    private final UserRepo userRepo;
    private final UserIdentitieRepo userIdentitieRepo;
    //
    // public User loginOrRegister(User user,String userSub){
    // //first we find if same user hase database ma with same method so entry nai
    // pade
    //// User savedUser=userRepo.findByProviderSub(user.getProviderSub()).
    //// map(existingUser ->{
    //// existingUser.setTime(LocalDateTime.now());
    //// return userRepo.save(existingUser);
    //// })
    //// .orElseGet(() -> {
    //// //new user → save fresh
    //// user.setTime(LocalDateTime.now());
    //// return userRepo.save(user);
    //// });
    //
    // Optional<UserIdentitie>
    // identity=userIdentitieRepo.findByProviderSub(userSub);
    // if(identity.isPresent()) {
    // User savedUser = identity.get().getUser();
    // user.setLastLogin(LocalDateTime.now());
    // userRepo.save(user);
    // return user;
    // }else{
    //
    // User userOpt=userRepo.findByEmail(user.getEmail()).get();
    // userIdentitieRepo.save(new UserIdentitie(userOpt,user.))
    //
    //
    //
    // }
    //
    //
    //
    //
    //
    // // ValueOperations<String,String> valueOps=redisTemplate.opsForValue();
    // // valueOps.set("user:"+user.getUserId(),savedUser.toString(),100,
    // TimeUnit.SECONDS);
    // return savedUser;
    // }
    //
    //
    // //get user so firat we try in redis if not found then it is called miss then
    // we go to database ..
    // //and if user found in redis then it called hit so apde database jode nai
    // jaisu

    @Transactional
    public User loginOrRegister(String email, String subject) {

        if (email == null || email.isBlank()) {
            throw new JwtValidationException("email missing or empty in jwt");
        }
        if (subject == null || subject.isBlank()) {
            throw new JwtValidationException("subject missing or empty");
        }


        // first we check in user identit table
        log.info("starting process for creating login/register");

        String[] parts = subject.split("\\|");
        //parts aaj format ma hovu joiee  A|B like auth|2e34r33e3
        if (parts.length < 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new JwtValidationException("invaid subject format: expected provider|id");
        }
        String provider = parts[0];
        String providerUserId = parts[1];

        Optional<UserIdentitie> identity = userIdentitieRepo.findByProviderSub(subject);
        if (identity.isPresent()) {
            User user = identity.get().getUser();
            if (user == null) {
                //if user identite ma che pan user table ma mnuall delete kari dedho toh error avse
                //ke identity che but user nathi
                throw new UserNotFoundException("User missing for identity subject=" + subject);
            }
            // if present hoy toh only last login change karsu
            user.setLastLogin(LocalDateTime.now());
            return userRepo.save(user);
        }
        log.info("indentity find by request subject {}", identity);

        // user identity table ma present nai hoy it means e login haji first time thayo
        // che e method thi

        // first user table mathi email vade find karsu .
        Random random = new Random();
        long user_id = random.nextLong();
        long bound_id = Math.abs(user_id % 10000);// bound to 4 digit

        log.info("random 4 digit user_id {}  generated for user {} ", bound_id, email);
        Optional<User> userOpt = userRepo.findByEmail(email);
        User user;
        if (userOpt.isPresent()) {
            user = userOpt.get();
            log.info("user is present {} in database ", user.getEmail());
        } else {
            log.info("user is login first time. start creating user object bound id is {}", bound_id);
            user = User.builder()
                    .email(email)
                    .lastLogin(LocalDateTime.now())
                    .userId(bound_id)
                    .build();
            user = userRepo.save(user);
            log.info("user {} is successfully save in database", user.getEmail());
        }


        try {
                UserIdentitie userIdentitie = UserIdentitie.builder()
                        .created_time(LocalDateTime.now())
                        .providerSub(subject)
                        .provider(provider)
                        .user(user)
                        .build();

                userIdentitieRepo.save(userIdentitie);
                log.info("userIdentity saved for subject={}", subject);
                user.setLastLogin(LocalDateTime.now());
                return userRepo.save(user);


        } catch (DataIntegrityViolationException ex) {
            // This happens if providerSub is unique and concurrent request inserted first
            throw new JwtValidationException("IDENTITY_ALREADY_EXISTS (race condition)");
        }
    }

}
/*
DataIntegrityViolationException aa tyare occur thayy jyare
1.Unique constraint breaks
2.Foreign key breaks
3.Not null constraint breaks
so in our case 1st is break..
Two concurrent inserts of same identity so race condition
Two login requests came at same time from same user  → one inserted first → second failed due to UNIQUE constraint
→ exception handled safely.
so Thread A save kari dese thai jase Thread B e ej time e karse toh code fati jase.. so like unique key constaint..
because banne thread e joyu hase id null che so banne save j karse evu nai thayy ke ek save karyu and
biju save call karse toh update km nai thatu..

and ama sync e na use kari sakay becuase one instanc hoy toh brbr
but if app na multipl instance hoy toh ?? banne instance per ek ek thread e same time e req gayi toh
pachu fati jase ne
 */
