package com.sunrise.dental.servlet;

import com.sunrise.dental.dao.AppointmentDAO;
import com.sunrise.dental.model.Appointment;
import com.sunrise.dental.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@WebServlet("/api/appointments/register")
public class RegisterAppointmentServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");

        String patientName = trim(req.getParameter("patientName"));
        String address = trim(req.getParameter("address"));
        String contactNumber = trim(req.getParameter("contactNumber"));
        String dentistName = trim(req.getParameter("dentistName"));
        String treatmentType = trim(req.getParameter("treatmentType"));
        String appointmentDate = trim(req.getParameter("appointmentDate"));
        String appointmentTime = trim(req.getParameter("appointmentTime"));

        // ---- Server-side validation (never trust the client) ----
        StringBuilder errors = new StringBuilder();
        if (isEmpty(patientName)) errors.append("Patient name is required. ");
        if (isEmpty(address)) errors.append("Address is required. ");
        if (isEmpty(contactNumber) || !contactNumber.matches("^[0-9+\\-\\s]{7,15}$"))
            errors.append("Enter a valid contact number. ");
        if (isEmpty(dentistName)) errors.append("Dentist is required. ");
        if (isEmpty(treatmentType)) errors.append("Treatment type is required. ");
        if (isEmpty(appointmentDate)) errors.append("Appointment date is required. ");
        if (isEmpty(appointmentTime)) errors.append("Appointment time is required. ");

        if (!isEmpty(appointmentDate)) {
            try {
                LocalDate date = LocalDate.parse(appointmentDate);
                if (date.isBefore(LocalDate.now())) {
                    errors.append("Appointment date cannot be in the past. ");
                }
            } catch (DateTimeParseException e) {
                errors.append("Appointment date format is invalid. ");
            }
        }

        if (errors.length() > 0) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{" + JsonUtil.pairBool("success", false) + "," +
                    JsonUtil.pair("message", errors.toString().trim()) + "}");
            return;
        }

        AppointmentDAO dao = new AppointmentDAO();

        // ---- Prevent double booking ----
        if (dao.isDoubleBooked(dentistName, appointmentDate, appointmentTime, null)) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().write("{" + JsonUtil.pairBool("success", false) + "," +
                    JsonUtil.pair("message", "Dr. " + dentistName + " already has an appointment at " +
                            appointmentTime + " on " + appointmentDate + ". Please choose another slot.") + "}");
            return;
        }
        
        // appointmentDate, Time edit

        String appointmentNo = dao.generateAppointmentNo(appointmentDate);

        Appointment a = new Appointment();
        a.setAppointmentNo(appointmentNo);
        a.setPatientName(patientName);
        a.setAddress(address);
        a.setContactNumber(contactNumber);
        a.setDentistName(dentistName);
        a.setTreatmentType(treatmentType);
        a.setAppointmentDate(appointmentDate);
        a.setAppointmentTime(appointmentTime);

        boolean saved = dao.addAppointment(a);

        if (saved) {
            String json = "{" + JsonUtil.pairBool("success", true) + "," +
                    JsonUtil.pair("message", "Appointment registered successfully.") + "," +
                    JsonUtil.pair("appointmentNo", appointmentNo) + "}";
            resp.getWriter().write(json);
        } else {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{" + JsonUtil.pairBool("success", false) + "," +
                    JsonUtil.pair("message", "Could not save the appointment. Please try again.") + "}");
        }
    }

    private String trim(String s) { return s == null ? "" : s.trim(); }
    private boolean isEmpty(String s) { return s == null || s.trim().isEmpty(); }
}
