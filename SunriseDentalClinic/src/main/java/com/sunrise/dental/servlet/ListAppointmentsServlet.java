package com.sunrise.dental.servlet;

import com.sunrise.dental.dao.AppointmentDAO;
import com.sunrise.dental.model.Appointment;
import com.sunrise.dental.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/** Lists all appointments, or just today's appointments when ?today=true is passed. */
@WebServlet("/api/appointments/list")
public class ListAppointmentsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        AppointmentDAO dao = new AppointmentDAO();
        boolean todayOnly = "true".equalsIgnoreCase(req.getParameter("today"));

        List<Appointment> list = todayOnly ? dao.getTodaysAppointments() : dao.getAllAppointments();

        StringBuilder arr = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            arr.append(SearchAppointmentServlet.toJson(list.get(i)));
            if (i < list.size() - 1) arr.append(",");
        }
        arr.append("]");

        resp.getWriter().write("{" + JsonUtil.pairBool("success", true) + ",\"appointments\":" + arr + "}");
    }
}
