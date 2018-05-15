package usmanali.nephrohub;

/**
 * Created by HelloWorldSolution on 5/13/2018.
 */

public class Prescription {
    public String getMedicine_name() {
        return medicine_name;
    }

    public void setMedicine_name(String medicine_name) {
        this.medicine_name = medicine_name;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getDoctor_instructions() {
        return doctor_instructions;
    }

    public void setDoctor_instructions(String doctor_instructions) {
        this.doctor_instructions = doctor_instructions;
    }

    String medicine_name;
    String dosage;
    String doctor_instructions;
}
