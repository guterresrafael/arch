package rs.pelotas.arch.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Rafael Guterres
 */
public class ResponseBuilder {
    
    public static Response ok() {
        return Response.status(Response.Status.OK).build();
    }
    
    public static Response ok(Object entity) {
        return Response.status(Response.Status.OK).entity(entity).build();
    }

    public static Response ok(List<Object> entities) {
        return Response.ok(entities, MediaType.APPLICATION_JSON).build();
    }
    
    public static Response created() {
        return Response.status(Response.Status.CREATED).build();
    }
    
    public static Response created(Object entity) {
        return Response.status(Response.Status.CREATED).entity(entity).build();
    }

    public static Response accepted() {
        return Response.status(Response.Status.ACCEPTED).build();
    }

    public static Response accepted(Object entity) {
        return Response.status(Response.Status.ACCEPTED).entity(entity).build();
    }
    
    public static Response deleted() {
        return Response.status(Response.Status.NO_CONTENT).build();
    }
    
    public static Response notModified() {
        return Response.status(Response.Status.NOT_MODIFIED).build();
    }
    
    public static Response badRequest() {
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
    
    public static Response badRequest(Exception exception) {
        String message = exception.getMessage();
        Map<String, String> responseObj = new HashMap<>();
        responseObj.put("error", message);
        return Response.status(Response.Status.BAD_REQUEST).entity(responseObj).build();
    }

    public static Response badRequest(ConstraintViolationException constraintViolationException) {
        Set<ConstraintViolation<?>> violations = constraintViolationException.getConstraintViolations();
        Map<String, String> responseObj = new HashMap<>();
        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }
        return Response.status(Response.Status.BAD_REQUEST).entity(responseObj).build();
    }
    
    public static Response unauthorized() {
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    
    public static Response forbidden() {
        return Response.status(Response.Status.FORBIDDEN).build();
    }
    
    public static Response notFound() {
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    public static Response timeout() {
        return Response.status(Response.Status.REQUEST_TIMEOUT).build();
    }
    
    public static Response conflict() {
        return Response.status(Response.Status.CONFLICT).build();
    }
    
    public static Response conflict(Exception exception) {
        String message = exception.getMessage();
        Map<String, String> responseObj = new HashMap<>();
        responseObj.put("error", message);
        return Response.status(Response.Status.CONFLICT).entity(responseObj).build();
    }
    
    public static Response notImplemented() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }
}