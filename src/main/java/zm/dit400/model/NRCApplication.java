package zm.dit400.model;

import zm.dit400.exception.InvalidRecordException;
import java.util.Locale;
import java.util.Set;

public class NRCApplication extends CitizenRecord {
    private final String recordType = "NRC_APPLICATION";
    private String applicationStatus;

    private static final Set<String> VALID_STATUSES = Set.of("PENDING", "PROCESSING", "COMPLETED", "REJECTED");

    public NRCApplication() {}

    public NRCApplication(String name, String dob, String province, String status) {
        super(name, dob, province);
        this.applicationStatus = status;
    }

    @Override
    public String getRecordType() {
        return "NRC_APPLICATION";
    }

    @Override
    public void validateDetails() {
        if (applicationStatus == null || applicationStatus.isBlank()) {
            throw new InvalidRecordException("applicationStatus is required");
        }
        String normalized = applicationStatus.trim().toUpperCase(Locale.ROOT);
        if (!VALID_STATUSES.contains(normalized)) {
            throw new InvalidRecordException("applicationStatus must be PENDING, PROCESSING, COMPLETED, or REJECTED");
        }
        this.applicationStatus = normalized;
    }

    @Override
    public String getDetailsSummary() {
        return "Application status: " + applicationStatus;
    }

    public String getApplicationStatus() { return applicationStatus; }
    public void setApplicationStatus(String v) { applicationStatus = v; }
}
