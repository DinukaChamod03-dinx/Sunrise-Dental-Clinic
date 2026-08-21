package com.sunrise.dental.servlet;

import com.sunrise.dental.dao.DentistDAO;
import com.sunrise.dental.model.Dentist;
import com.sunrise.dental.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/** Returns the list of dentists, used to populate dropdowns on the frontend. */
@WebServlet("/api/dentists")
public class DentistServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        List<Dentist> dentists = new DentistDAO().getAllDentists();

        StringBuilder arr = new StringBuilder("[");
        for (int i = 0; i < dentists.size(); i++) {
            Dentist d = dentists.get(i);
            arr.append("{").append(JsonUtil.pair("name", d.getName())).append(",")
               .append(JsonUtil.pair("specialization", d.getSpecialization())).append("}");
            if (i < dentists.size() - 1) arr.append(",");
        }
        arr.append("]");

        resp.getWriter().write("{" + JsonUtil.pairBool("success", true) + ",\"dentists\":" + arr + "}");
    }
}
