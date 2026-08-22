package com.sunrise.dental.model;

public class Treatment {
    private int id;
    private String treatmentName;
    private double cost;

    public Treatment() {}

    public Treatment(int id, String treatmentName, double cost) {
        this.id = id;
        this.treatmentName = treatmentName;
        this.cost = cost;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTreatmentName() { return treatmentName; }
    public void setTreatmentName(String treatmentName) { this.treatmentName = treatmentName; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }
}


//treatmentName

