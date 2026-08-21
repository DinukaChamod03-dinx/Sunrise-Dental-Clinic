package com.sunrise.dental.servlet;

import com.sunrise.dental.dao.TreatmentDAO;
import com.sunrise.dental.model.Treatment;
import com.sunrise.dental.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/** Returns the list of treatments and their prices, used to populate dropdowns. */
@WebServlet("/api/treatments")
public class TreatmentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        List<Treatment> treatments = new TreatmentDAO().getAllTreatments();

        StringBuilder arr = new StringBuilder("[");
        for (int i = 0; i < treatments.size(); i++) {
            Treatment t = treatments.get(i);
            arr.append("{").append(JsonUtil.pair("name", t.getTreatmentName())).append(",")
               .append(JsonUtil.pairNum("cost", t.getCost())).append("}");
            if (i < treatments.size() - 1) arr.append(",");
        }
        arr.append("]");

        resp.getWriter().write("{" + JsonUtil.pairBool("success", true) + "," +
                "\"treatments\":" + arr + "," +
                JsonUtil.pairNum("consultationFee", TreatmentDAO.CONSULTATION_FEE) + "}");
    }
}
