package usmanali.nephrohub.Model;
import java.util.ArrayList;

/**
 * Created by SAJIDCOMPUTERS on 3/30/2018.
 */

public class Scanned_reports {
    String date;
    String ref_by;
    String report_title;

    public String getRef_by() {
        return ref_by;
    }

    public void setRef_by(String ref_by) {
        this.ref_by = ref_by;
    }

    public String getReport_title() {
        return report_title;
    }

    public void setReport_title(String report_title) {
        this.report_title = report_title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    ArrayList<String> tests;
ArrayList<String> results;

    public ArrayList<String> getTests() {
        return tests;
    }

    public void setTests(ArrayList<String> tests) {
        this.tests = tests;
    }

    public ArrayList<String> getResults() {
        return results;
    }

    public void setResults(ArrayList<String> results) {
        this.results = results;
    }
}
