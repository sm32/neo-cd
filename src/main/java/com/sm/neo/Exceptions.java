package com.sm.neo;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class Exceptions extends WebApplicationException {
    public Exceptions(int code, String error)  {
        super(new Throwable(error), Response.status(code)
                .entity("{\"error\":\"" + error + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build());

    }

    public static Exceptions invalidInput = new Exceptions(400, "Invalid Input");

    public static Exceptions missingRelationshipsParameter = new Exceptions(400, "Missing relationships Parameter.");
    public static Exceptions emptyRelationshipsParameter = new Exceptions(400, "Empty relationships Parameter.");
}