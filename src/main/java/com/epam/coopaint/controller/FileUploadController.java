package com.epam.coopaint.controller;

import com.epam.coopaint.dao.FileSystemDAO;
import com.epam.coopaint.dao.impl.DAOFactory;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.ServiceException;
import com.epam.coopaint.service.UserService;
import com.epam.coopaint.service.impl.ServiceFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Paths;

import static com.epam.coopaint.command.impl.SessionAttribute.SESSION_UPLOAD_TYPE;
import static com.epam.coopaint.command.impl.SessionAttribute.SESSION_USER;
import static com.epam.coopaint.dao.impl.LocationData.SERVE_PATH_AVATAR;
import static com.epam.coopaint.dao.impl.LocationData.STORAGE_PATH_AVATAR;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10,      // 10MB
        maxRequestSize = 1024 * 1024 * 50)   // 50MB
@WebServlet(urlPatterns = {"/upload-file"})
public class FileUploadController extends HttpServlet {
    private static Logger logger = LogManager.getLogger();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // state dependent uploader
        // upload type controlled by ACL in dispatcher
        try (Writer out = response.getWriter()) {
            HttpSession session = request.getSession(false);
            if (session == null) {
                response.setStatus(SC_UNAUTHORIZED);
            } else {
                var uploadType = (FileUploadType) session.getAttribute(SESSION_UPLOAD_TYPE);
                FileSystemDAO fileSystemDAO = DAOFactory.INSTANCE.createFileSystemDAO();
                for (Part part : request.getParts()) {
                    try (InputStream in = part.getInputStream()) {
                        if (uploadType == FileUploadType.AVATAR) {
                            String newAvatarName = fileSystemDAO.save(in, STORAGE_PATH_AVATAR);
                            var user = (User) session.getAttribute(SESSION_USER);
                            UserService userService = ServiceFactory.INSTANCE.getUserService();
                            userService.updateAvatar(user.getUuid(), newAvatarName);
                            user.setAvatar(Paths.get(SERVE_PATH_AVATAR, newAvatarName).toString());
                            session.setAttribute(SESSION_USER, user);
                            out.write(new ObjectMapper().writeValueAsString(user));
                            break;
                        }
                    } catch (ServiceException | RuntimeException e2) {
                        logger.error(e2);
                        response.setStatus(SC_INTERNAL_SERVER_ERROR);
                        var err = new ObjectMapper().createObjectNode().put("body", "ಠ╭╮ಠ");
                        out.write(new ObjectMapper().writeValueAsString(err));
                    }
//                    if (uploadType == UploadType.BOARD) {
//                        String filePath = fileService.save(part.getInputStream(), UPLOAD_PATH_BOARD);
//                    }
                }
            }
        } catch (IOException | ServletException e1) {
            logger.error(e1);
        }
    }
}
