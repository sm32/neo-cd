package com.sm.neo;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;

public class Validators {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static HashMap getValidInput(String body) throws IOException {
        HashMap input;

        // Parse the input
        try {
            input = objectMapper.readValue(body, HashMap.class);
        } catch (Exception e) {
            throw Exceptions.invalidInput;
        }

        if (!input.containsKey("relationships")) {
            throw Exceptions.missingRelationshipsParameter;
        } else {
            if (input.get("relationships")== null){
                throw Exceptions.emptyRelationshipsParameter;
            }
        }

        return input;
    }
}
