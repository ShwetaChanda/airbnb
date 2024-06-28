package com.airbnb.service;

import com.airbnb.entity.PropertyUser;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class JWTService {
    @Value("${jwt.algorithm.key}")
    private String algorithmKey;
    @Value("${jwt.issuer}")
    private String issuer;
    @Value("${jwt.expiry.duration}")
    private int expiryTime;
    private final static String USER_NAME = "userName";
    private Algorithm algorithm;

    @PostConstruct
    public void postConstruct() {
        algorithm = Algorithm.HMAC256(algorithmKey);
    }

    public String generateToken(PropertyUser propertyUser) {
        return JWT.create().withClaim(USER_NAME, propertyUser.getUserName())
                .withExpiresAt(new Date(System.currentTimeMillis() + expiryTime))
                .withIssuer(issuer)
                .sign(algorithm);

    }

    //
    public String getUserName(String token) {//it takes the token
        DecodedJWT decodedJWT = JWT.require(algorithm).withIssuer(issuer).build().verify(token);

        //from token it will apply the algorithm and secret key to decript,it will check the issuer
        //the verify token will check the expiry type
        // shortcut: rosie with Bonie v

        return decodedJWT.getClaim(USER_NAME).asString();

        // if everthing is correct , decoded token(decodedJWT) using that to get the username.
    }


    }



