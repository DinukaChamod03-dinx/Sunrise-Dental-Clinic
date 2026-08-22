package com.sunrise.dental.servlet;

import com.sunrise.dental.dao.UserDAO;
import com.sunrise.dental.model.User;
import com.sunrise.dental.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

//apilogin

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            resp.getWriter().write("{" + JsonUtil.pairBool("success", false) + "," +
                    JsonUtil.pair("message", "Username and password are required.") + "}");
            return;
        }

        UserDAO userDAO = new UserDAO();
        User user = userDAO.validateLogin(username.trim(), password);

        if (user != null) {
            HttpSession session = req.getSession(true);
            session.setAttribute("user", user);
            session.setMaxInactiveInterval(30 * 60); // 30 minutes

            String json = "{" + JsonUtil.pairBool("success", true) + "," +
                    JsonUtil.pair("message", "Login successful. Welcome " + user.getFullName() + "!") + "," +
                    JsonUtil.pair("fullName", user.getFullName()) + "," +
                    JsonUtil.pair("role", user.getRole()) + "}";
            resp.getWriter().write(json);
        } else {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{" + JsonUtil.pairBool("success", false) + "," +
                    JsonUtil.pair("message", "Invalid username or password.") + "}");
        }
    }
}
