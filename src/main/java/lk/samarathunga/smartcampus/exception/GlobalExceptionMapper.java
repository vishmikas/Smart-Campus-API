package lk.samarathunga.smartcampus.exception;

import lk.samarathunga.smartcampus.model.ErrorResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Catch-all exception mapper.
 * Intercepts any unhandled Throwable and returns HTTP 500
 * without leaking internal stack traces to clients.
 *
 * Author : S.D.V. Samarathunga
 * UoW ID : 2151919w
 * IIT ID : 20241769
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable ex) {
        ErrorResponse body = new ErrorResponse(
            "An unexpected error occurred on the server. Please try again later.",
            500,
            "/api/v1"
        );
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(body)
                .build();
    }
}
