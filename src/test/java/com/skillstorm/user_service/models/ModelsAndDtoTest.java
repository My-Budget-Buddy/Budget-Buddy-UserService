package com.skillstorm.user_service.models;

import org.junit.jupiter.api.Test;
import org.meanbean.test.BeanVerifier;

import com.skillstorm.user_service.dtos.UserDto;

public class ModelsAndDtoTest {

    @Test
    public void ModelsAndDtoAllTest(){
        // Test getters, setters, constructors, equals, and hashcode
        BeanVerifier.verifyBeans(User.class, UserDto.class);
    }


}
