package zm.dit400.model;

public abstract class CitizenRecord {
    private String id;
    private String fullName;
    private String dateOfBirth;
    private String province;
    private String createdAt;

    protected CitizenRecord() {}

    protected CitizenRecord(String fullName, String dateOfBirth, String province) {
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.province = province;
    }

    public abstract String getRecordType();

    /**
     * Polymorphic validation for subtype-specific fields.
     * Implemented by each subclass without requiring instanceof checks.
     */
    public abstract void validateDetails();

    /**
     * Polymorphic summary for subtype-specific information.
     */
    public abstract String getDetailsSummary();

    public String getId() { return id; }
    public void setId(String v) { id = v; }

    public String getFullName() { return fullName; }
    public void setFullName(String v) { fullName = v; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String v) { dateOfBirth = v; }

    public String getProvince() { return province; }
    public void setProvince(String v) { province = v; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String v) { createdAt = v; }
}
