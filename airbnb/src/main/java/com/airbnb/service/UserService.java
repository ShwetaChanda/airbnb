package com.airbnb.service;

import com.airbnb.dto.LoginDto;
import com.airbnb.dto.PropertyUserDto;
import com.airbnb.entity.PropertyUser;
import com.airbnb.repository.PropertyUserRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private PropertyUserRepository userRepository;
    private JWTService jwtService;

    public UserService(PropertyUserRepository userRepository, JWTService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }
 public PropertyUser addUser(PropertyUserDto propertyUserDto){
 PropertyUser propertyUser= new PropertyUser();
     propertyUser.setFirstName(propertyUserDto.getFirstName());
     propertyUser.setLastName(propertyUserDto.getLastName());
     propertyUser.setUserName(propertyUserDto.getUserName());
     propertyUser.setUserRole(propertyUserDto.getUserRole());
     propertyUser.setEmail(propertyUserDto.getEmail());
     propertyUser.setPassword(BCrypt.hashpw(propertyUserDto.getPassword(),BCrypt.gensalt(10)));
     PropertyUser saved = userRepository.save(propertyUser);
     return saved;
 }

    public String verifyLogin(LoginDto loginDto) {
        Optional<PropertyUser> byUserName = userRepository.findByUserName(loginDto.getUserName());
        if (byUserName.isPresent()){
            PropertyUser user = byUserName.get();
            if (BCrypt.checkpw(loginDto.getPassword(),user.getPassword()))
             return jwtService.generateToken(user);
        }
        return null;
    }
}
