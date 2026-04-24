package lk.samarathunga.smartcampus.exception;

import lk.samarathunga.smartcampus.model.ErrorResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Maps InvalidReferenceException to HTTP 422 Unprocessable Entity with a JSON body.
 *
 * Author : S.D.V. Samarathunga
 * UoW ID : 2151919w
 * IIT ID : 20241769
 */
@Provider
public class InvalidReferenceMapper implements ExceptionMapper<InvalidReferenceException> {

    @Override
    public Response toResponse(InvalidReferenceException ex) {
        ErrorResponse body = new ErrorResponse(ex.getMessage(), 422, "/api/v1");
        return Response.status(422)
                .type(MediaType.APPLICATION_JSON)
                .entity(body)
                .build();
    }
}
