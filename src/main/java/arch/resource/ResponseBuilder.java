package arch.resource;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;

/**
 *
 * @author Rafael Guterres
 */
public final class ResponseBuilder implements Serializable {

    private static final long serialVersionUID = -4360335602897527345L;

    private ResponseBuilder() {
    }

    public static Response ok() {
        return Response.status(Response.Status.OK).build();
    }

    public static Response ok(Object entity) {
        return Response.status(Response.Status.OK).entity(entity).build();
    }

    public static Response ok(List<Object> entities) {
        return Response.ok(entities).build();
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

    public static Response redirect(String uri) {
        try {
            return Response.seeOther(new URI(uri)).build();
        } catch (URISyntaxException e) {
            Logger.getAnonymousLogger().warning(e.getMessage());
            return null;
        }
    }
}
