package lk.samarathunga.smartcampus.exception;

import lk.samarathunga.smartcampus.model.ErrorResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Maps RoomHasSensorsException to HTTP 409 Conflict with a JSON body.
 *
 * Author : S.D.V. Samarathunga
 * UoW ID : 2151919w
 * IIT ID : 20241769
 */
@Provider
public class RoomHasSensorsMapper implements ExceptionMapper<RoomHasSensorsException> {

    @Override
    public Response toResponse(RoomHasSensorsException ex) {
        ErrorResponse body = new ErrorResponse(ex.getMessage(), 409, "/api/v1");
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(body)
                .build();
    }
}
