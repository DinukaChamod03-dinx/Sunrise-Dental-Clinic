package com.sunrise.dental.servlet;

import com.sunrise.dental.dao.AppointmentDAO;
import com.sunrise.dental.model.Appointment;
import com.sunrise.dental.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * Searches an appointment either by exact appointment number (?appointmentNo=...)
 * or by partial patient name (?patientName=...).
 */
@WebServlet("/api/appointments/search")
public class SearchAppointmentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        String appointmentNo = req.getParameter("appointmentNo");
        String patientName = req.getParameter("patientName");
        AppointmentDAO dao = new AppointmentDAO();

        if (appointmentNo != null && !appointmentNo.trim().isEmpty()) {
            Appointment a = dao.getByAppointmentNo(appointmentNo.trim());
            if (a == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{" + JsonUtil.pairBool("success", false) + "," +
                        JsonUtil.pair("message", "No appointment found with number " + appointmentNo) + "}");
                return;
            }
            resp.getWriter().write("{" + JsonUtil.pairBool("success", true) + "," +
                    "\"appointments\":[" + toJson(a) + "]}");
            return;
        }

        if (patientName != null && !patientName.trim().isEmpty()) {
            List<Appointment> list = dao.searchByPatientName(patientName.trim());
            if (list.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{" + JsonUtil.pairBool("success", false) + "," +
                        JsonUtil.pair("message", "No appointments found for \\\"" + patientName + "\\\".") + "}");
                return;
            }
            StringBuilder arr = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                arr.append(toJson(list.get(i)));
                if (i < list.size() - 1) arr.append(",");
            }
            arr.append("]");
            resp.getWriter().write("{" + JsonUtil.pairBool("success", true) + ",\"appointments\":" + arr + "}");
            return;
        }

        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        resp.getWriter().write("{" + JsonUtil.pairBool("success", false) + "," +
                JsonUtil.pair("message", "Provide an appointment number or a patient name to search.") + "}");
    }

    static String toJson(Appointment a) {
        return "{" +
                JsonUtil.pair("appointmentNo", a.getAppointmentNo()) + "," +
                JsonUtil.pair("patientName", a.getPatientName()) + "," +
                JsonUtil.pair("address", a.getAddress()) + "," +
                JsonUtil.pair("contactNumber", a.getContactNumber()) + "," +
                JsonUtil.pair("dentistName", a.getDentistName()) + "," +
                JsonUtil.pair("treatmentType", a.getTreatmentType()) + "," +
                JsonUtil.pair("appointmentDate", a.getAppointmentDate()) + "," +
                JsonUtil.pair("appointmentTime", a.getAppointmentTime()) + "," +
                JsonUtil.pair("status", a.getStatus()) + "," +
                JsonUtil.pair("createdAt", a.getCreatedAt()) +
                "}";
    }
}
