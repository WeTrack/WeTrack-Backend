package com.wetrack.util;

import com.wetrack.model.CreatedMessage;
import com.wetrack.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Util class for generating {@link Response}s.
 */
public abstract class ResponseUtils {
    private static final Logger LOG = LoggerFactory.getLogger(ResponseUtils.class);

    private static final String NOT_FOUND = "Not Found";

    /**
     * Returns a {@code 404 Response} with JSON object as follows:
     * <pre>
     *     {
     *         status_code: 404,
     *         message: "Not Found",
     *         documentation_url: ${DOC_URL}
     *     }
     * </pre>
     *
     * @return the resulting {@code 404 Response}.
     *
     * @see #notFound(String)
     */
    public static Response notFound() {
        return Response.status(404).build();
    }

    public static Response notFoundMessage() {
        return response(404, NOT_FOUND);
    }

    /**
     * Returns a {@code Response} with the given status code and a JSON object as follows:
     * <pre>
     *     {
     *         status_code: ${statusCode},
     *         message: ${message},
     *         documentation_url: ${DOC_URL}
     *     }
     * </pre>
     *
     * @param statusCode variable {@code statusCode} for the resulting {@code Response}.
     * @param message variable {@code message} for the resulting {@code Response}.
     * @return the resulting {@code Response}.
     */
    public static Response response(int statusCode, String message) {
        return Response.status(statusCode)
                .entity(new Message(statusCode, message))
                .build();
    }

    /**
     * Returns a {@code 404 Response} with JSON object as follows:
     *
     * <pre>
     *     {
     *         message: ${message},
     *         documentation_url: ${DOC_URL}
     *     }
     * </pre>
     *
     * @param message variable {@code message} for the resulting {@code Response}.
     *
     * @return the resulting {@code 404 Response}.
     */
    public static Response notFound(String message) {
        return response(404, message);
    }

    public static Response notModified() {
        return Response.notModified().build();
    }

    /**
     * Returns a {@code 400 Response} with JSON object as follows:
     * <pre>
     *     {
     *         message: ${message},
     *         documentation_url: ${DOC_URL}
     *     }
     * </pre>
     *
     * @param message variable {@code message} for the resulting {@code Response}.
     *
     * @return the resulting {@code 400 Response}.
     */
    public static Response badRequest(String message) {
        return response(400, message);
    }

    public static Response internalError(String message) {
        return response(500, message);
    }

    /**
     * Returns a {@code 403 Response} with JSON object as follows:
     * <pre>
     *     {
     *         message: ${message},
     *         documentation_url: ${DOC_URL}
     *     }
     * </pre>
     *
     * @param message variable {@code message} for the resulting {@code Response}.
     *
     * @return the resulting {@code 403 Response}.
     */
    public static Response forbidden(String message) {
        return response(403, message);
    }

    /**
     * Returns a {@code 401 Response} with JSON object as follows:
     * <pre>
     *     {
     *         message: ${message},
     *         documentation_url: ${DOC_URL}
     *     }
     * </pre>
     *
     * @param message variable {@code message} for the resulting {@code Response}.
     *
     * @return the resulting {@code 401 Response}.
     */
    public static Response unauthorized(String message) {
        return response(401, message);
    }

    /**
     * Returns a {@code 201 Response} with the given location in URL setting in the {@code entity_url}
     * header field and the given message as its body content.
     * <p>
     * If the method failed to parse the given location as a valid URL, the given URL will <b>not</b>
     * be contained in the headers.
     *
     * @param location URL for the created entity.
     * @return a {@code 201 Response}.
     */
    public static Response created(String location, String message) {
        URI uri;

        try {
            uri = new URI(location);
        } catch (URISyntaxException e) {
            LOG.error("Failed to parse string `" + location + "` as a URI, returning empty");
            return Response.status(201)
                    .entity(new CreatedMessage(message, ""))
                    .build();
        }

        return Response.created(uri)
                .entity(new CreatedMessage(message, location))
                .build();
    }

    /**
     * Returns an empty {@code 200 Response}.
     */
    public static Response ok() {
        return Response.ok().build();
    }

    /**
     * Returns a {@code 200 Response} with the given entity as its body content.
     *
     * @param entity the given entity.
     * @return a {@code 200 Response} with the given message.
     */
    public static Response ok(String entity) {
        return Response.ok(entity).build();
    }

    public static Response okMessage(String entity) {
        return response(200, entity);
    }
}
