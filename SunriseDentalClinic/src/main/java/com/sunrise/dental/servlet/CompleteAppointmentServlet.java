package com.sunrise.dental.servlet;

import com.sunrise.dental.dao.AppointmentDAO;
import com.sunrise.dental.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/** Marks an appointment as completed once treatment is finished (enhancement). */
@WebServlet("/api/appointments/complete")
public class CompleteAppointmentServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        String appointmentNo = req.getParameter("appointmentNo");
        AppointmentDAO dao = new AppointmentDAO();

        //appointment not found
        if (appointmentNo == null || appointmentNo.trim().isEmpty() || dao.getByAppointmentNo(appointmentNo) == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{" + JsonUtil.pairBool("success", false) + "," +
                    JsonUtil.pair("message", "Appointment not found.") + "}");
            return;
        }

        //Appointment marked as completed
        boolean done = dao.markCompleted(appointmentNo.trim());
        if (done) {
            resp.getWriter().write("{" + JsonUtil.pairBool("success", true) + "," +
                    JsonUtil.pair("message", "Appointment marked as completed.") + "}");
        } else {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{" + JsonUtil.pairBool("success", false) + "," +
                    JsonUtil.pair("message", "Could not update the appointment.") + "}");
        }
    }
}



//appointment as completed
