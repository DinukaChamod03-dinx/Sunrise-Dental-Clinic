package com.sunrise.dental.servlet;

import com.sunrise.dental.dao.AppointmentDAO;
import com.sunrise.dental.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

// Appointment Api


@WebServlet("/api/appointments/cancel")
public class CancelAppointmentServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        String appointmentNo = req.getParameter("appointmentNo");
        AppointmentDAO dao = new AppointmentDAO();

        if (appointmentNo == null || appointmentNo.trim().isEmpty() || dao.getByAppointmentNo(appointmentNo) == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{" + JsonUtil.pairBool("success", false) + "," +
                    JsonUtil.pair("message", "Appointment not found.") + "}");
            return;
        }

        //could not cancel the appointment
        boolean cancelled = dao.cancelAppointment(appointmentNo.trim());
        if (cancelled) {
            resp.getWriter().write("{" + JsonUtil.pairBool("success", true) + "," +
                    JsonUtil.pair("message", "Appointment cancelled.") + "}");
        } else {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{" + JsonUtil.pairBool("success", false) + "," +
                    JsonUtil.pair("message", "Could not cancel the appointment.") + "}");
        }
    }
}
