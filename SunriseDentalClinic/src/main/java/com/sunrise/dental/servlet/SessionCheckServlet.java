package com.sunrise.dental.servlet;

import com.sunrise.dental.model.User;
import com.sunrise.dental.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/** Lets the frontend check (on page load) whether the user is still logged in. */
@WebServlet("/api/session")
public class SessionCheckServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            resp.getWriter().write("{" + JsonUtil.pairBool("success", false) + "}");
        } else {
            resp.getWriter().write("{" + JsonUtil.pairBool("success", true) + "," +
                    JsonUtil.pair("fullName", user.getFullName()) + "," +
                    JsonUtil.pair("role", user.getRole()) + "}");
        }
    }
}


//success for the bill updates

