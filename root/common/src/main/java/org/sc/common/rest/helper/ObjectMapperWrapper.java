package org.sc.common.rest.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

public class ObjectMapperWrapper {

    final ObjectMapper mapper = new ObjectMapper();

    @Bean
    public ObjectMapper getObjectMapper(){
        return mapper;
    }
}
