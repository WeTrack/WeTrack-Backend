package com.wetrack.service.user;

import com.wetrack.dao.UserPortraitRepository;
import com.wetrack.dao.UserRepository;
import com.wetrack.dao.UserTokenRepository;
import com.wetrack.model.UserPortrait;
import com.wetrack.model.UserToken;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static com.wetrack.util.ResponseUtils.*;

// TODO Write test cases for this service
@Path("/users/{username}/portrait")
public class UserPortraitService {
    private static final Logger LOG = LoggerFactory.getLogger(UserPortraitService.class);

    @Autowired private String localPortraitHome;
    @Autowired private UserRepository userRepository;
    @Autowired private UserTokenRepository userTokenRepository;
    @Autowired private UserPortraitRepository userPortraitRepository;

    @GET
    public Response getPortrait(@PathParam("username") String username,
                                @HeaderParam("If-Modified-Since") @DefaultValue("") String imsStr) {
        LOG.debug("GET  /users/{}/portrait", username);
        UserPortrait portrait = userPortraitRepository.findById(username);
        if (portrait == null) {
            if (userRepository.countByUsername(username) == 0)
                return notFound("User `" + username + "` does not exist.");
            return notFound("User `" + username + "` did not upload any portrait.");
        }
        if (!imsStr.isEmpty()) {
            LocalDateTime sinceTime;
            try {
                sinceTime = ZonedDateTime.parse(imsStr, DateTimeFormatter.RFC_1123_DATE_TIME)
                        .withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
                if (!portrait.getUpdatedAt().isAfter(sinceTime))
                    return notModified();
            } catch (DateTimeParseException ex) {
                ex.setStackTrace(new StackTraceElement[0]);
                LOG.debug("Failed to parse given `If-Modified-Since` header `" + imsStr + "`: ", ex);
            }
        }
        try {
            java.nio.file.Path localFilePath = Paths.get(localPortraitHome, username + portrait.getType().getSuffix());
            InputStream localFileStream = Files.newInputStream(localFilePath);
            String contentDisposition = String.format("inline; filename=\"%s\"", username + portrait.getType().getSuffix());
            return Response.ok(localFileStream, "application/" + portrait.getType().getSuffix().substring(1))
                    .header("Content-Disposition", contentDisposition)
                    .header("Last-Modified", portrait.getUpdatedAt().plusSeconds(1)
                            .atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC)
                            .toOffsetDateTime().format(DateTimeFormatter.RFC_1123_DATE_TIME)).build();
        } catch (IOException ex) {
            LOG.warn("IOException occurred when reading portrait from local file system: ", ex);
            return internalError("Server internal error occurred when reading user portrait: " + ex.getClass().getName());
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response postPortrait(@PathParam("username") String username,
                                 @QueryParam("token") @DefaultValue("") String token,
                                 @FormDataParam("data") InputStream uploadedInputStream,
                                 @FormDataParam("data") FormDataContentDisposition fileDetail) {
        LOG.debug("POST /users/{}/portrait", username);
        UserToken tokenInDB = userTokenRepository.findByTokenStr(token);
        if (tokenInDB == null || tokenInDB.getExpireTime().isBefore(LocalDateTime.now())
                || !tokenInDB.getUsername().equals(username))
            return unauthorized("The given token is invalid or has expired. Please log in again.");

        UserPortrait portrait = new UserPortrait(username, fileDetail.getFileName());
        try {
            java.nio.file.Path localFilePath = Paths.get(localPortraitHome, username + portrait.getType().getSuffix());
            Files.createDirectories(localFilePath);
            Files.copy(uploadedInputStream, localFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            LOG.warn("IOException occurred when saving uploaded portrait to local file system: ", ex);
            return internalError("Server internal error occurred when saving uploaded data: " + ex.getClass().getName());
        }
        userPortraitRepository.insert(portrait);

        return created("/users/" + username + "/portrait", "User portrait updated.");
    }
}
