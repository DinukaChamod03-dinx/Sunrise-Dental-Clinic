package com.sunrise.dental.servlet;

import com.sunrise.dental.dao.AppointmentDAO;
import com.sunrise.dental.model.Appointment;
import com.sunrise.dental.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/api/appointments/update")
public class UpdateAppointmentServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        AppointmentDAO dao = new AppointmentDAO();

        String appointmentNo = trim(req.getParameter("appointmentNo"));
        if (isEmpty(appointmentNo) || dao.getByAppointmentNo(appointmentNo) == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{" + JsonUtil.pairBool("success", false) + "," +
                    JsonUtil.pair("message", "Appointment not found.") + "}");
            return;
        }

        String patientName = trim(req.getParameter("patientName"));
        String address = trim(req.getParameter("address"));
        String contactNumber = trim(req.getParameter("contactNumber"));
        String dentistName = trim(req.getParameter("dentistName"));
        String treatmentType = trim(req.getParameter("treatmentType"));
        String appointmentDate = trim(req.getParameter("appointmentDate"));
        String appointmentTime = trim(req.getParameter("appointmentTime"));

        if (isEmpty(patientName) || isEmpty(address) || isEmpty(contactNumber) || isEmpty(dentistName)
                || isEmpty(treatmentType) || isEmpty(appointmentDate) || isEmpty(appointmentTime)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{" + JsonUtil.pairBool("success", false) + "," +
                    JsonUtil.pair("message", "All fields are required to update the appointment.") + "}");
            return;
        }

        if (dao.isDoubleBooked(dentistName, appointmentDate, appointmentTime, appointmentNo)) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().write("{" + JsonUtil.pairBool("success", false) + "," +
                    JsonUtil.pair("message", "Dr. " + dentistName + " already has another appointment at that time.") + "}");
            return;
        }


        //appointment list 

        
        Appointment a = new Appointment();
        a.setAppointmentNo(appointmentNo);
        a.setPatientName(patientName);
        a.setAddress(address);
        a.setContactNumber(contactNumber);
        a.setDentistName(dentistName);
        a.setTreatmentType(treatmentType);
        a.setAppointmentDate(appointmentDate);
        a.setAppointmentTime(appointmentTime);

        boolean updated = dao.updateAppointment(a);
        if (updated) {
            resp.getWriter().write("{" + JsonUtil.pairBool("success", true) + "," +
                    JsonUtil.pair("message", "Appointment updated successfully.") + "}");
        } else {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{" + JsonUtil.pairBool("success", false) + "," +
                    JsonUtil.pair("message", "Could not update the appointment.") + "}");
        }
    }

    private String trim(String s) { return s == null ? "" : s.trim(); }
    private boolean isEmpty(String s) { return s == null || s.trim().isEmpty(); }
}
