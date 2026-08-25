package com.sunrise.dental.servlet;

import com.sunrise.dental.dao.AppointmentDAO;
import com.sunrise.dental.dao.BillDAO;
import com.sunrise.dental.dao.TreatmentDAO;
import com.sunrise.dental.model.Appointment;
import com.sunrise.dental.model.Bill;
import com.sunrise.dental.model.Treatment;
import com.sunrise.dental.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Calculates and saves the bill for a given appointment:
 * total = treatment cost (by treatment type) + fixed consultation fee.
 */

//apibill

@WebServlet("/api/bill/generate")
public class BillServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        String appointmentNo = req.getParameter("appointmentNo");

        if (appointmentNo == null || appointmentNo.trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{" + JsonUtil.pairBool("success", false) + "," +
                    JsonUtil.pair("message", "Appointment number is required.") + "}");
            return;
        }

        AppointmentDAO appointmentDAO = new AppointmentDAO();
        Appointment appointment = appointmentDAO.getByAppointmentNo(appointmentNo.trim());

        if (appointment == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{" + JsonUtil.pairBool("success", false) + "," +
                    JsonUtil.pair("message", "No appointment found with number " + appointmentNo) + "}");
            return;
        }

        if ("CANCELLED".equals(appointment.getStatus())) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().write("{" + JsonUtil.pairBool("success", false) + "," +
                    JsonUtil.pair("message", "Cannot bill a cancelled appointment.") + "}");
            return;
        }

        TreatmentDAO treatmentDAO = new TreatmentDAO();
        Treatment treatment = treatmentDAO.getByName(appointment.getTreatmentType());
        double treatmentCost = (treatment != null) ? treatment.getCost() : 0.0;
        double consultationFee = TreatmentDAO.CONSULTATION_FEE;
        double total = treatmentCost + consultationFee;

        Bill bill = new Bill();
        bill.setAppointmentNo(appointment.getAppointmentNo());
        bill.setPatientName(appointment.getPatientName());
        bill.setTreatmentType(appointment.getTreatmentType());
        bill.setTreatmentCost(treatmentCost);
        bill.setConsultationFee(consultationFee);
        bill.setTotalAmount(total);

        BillDAO billDAO = new BillDAO();
        billDAO.saveBill(bill);

        String json = "{" + JsonUtil.pairBool("success", true) + "," +
                JsonUtil.pair("appointmentNo", appointment.getAppointmentNo()) + "," +
                JsonUtil.pair("patientName", appointment.getPatientName()) + "," +
                JsonUtil.pair("dentistName", appointment.getDentistName()) + "," +
                JsonUtil.pair("treatmentType", appointment.getTreatmentType()) + "," +
                JsonUtil.pair("appointmentDate", appointment.getAppointmentDate()) + "," +
                JsonUtil.pairNum("treatmentCost", treatmentCost) + "," +
                JsonUtil.pairNum("consultationFee", consultationFee) + "," +
                JsonUtil.pairNum("totalAmount", total) +
                "}";
        resp.getWriter().write(json);
    }
}
