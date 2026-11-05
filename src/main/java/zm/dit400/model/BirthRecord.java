package zm.dit400.model;

import zm.dit400.exception.InvalidRecordException;

public class BirthRecord extends CitizenRecord {
    private final String recordType = "BIRTH";
    private String placeOfBirth;
    private String parentName;

    public BirthRecord() {}

    public BirthRecord(String name, String dob, String province, String place, String parent) {
        super(name, dob, province);
        this.placeOfBirth = place;
        this.parentName = parent;
    }

    @Override
    public String getRecordType() {
        return "BIRTH";
    }

    @Override
    public void validateDetails() {
        if (placeOfBirth == null || placeOfBirth.isBlank()) {
            throw new InvalidRecordException("placeOfBirth is required");
        }
        if (placeOfBirth.trim().length() > 120) {
            throw new InvalidRecordException("placeOfBirth must be 120 characters or fewer");
        }
        if (parentName == null || parentName.isBlank()) {
            throw new InvalidRecordException("parentName is required");
        }
        if (parentName.trim().length() > 120) {
            throw new InvalidRecordException("parentName must be 120 characters or fewer");
        }
        placeOfBirth = placeOfBirth.trim();
        parentName = parentName.trim();
    }

    @Override
    public String getDetailsSummary() {
        return "Born at " + placeOfBirth + " · Parent: " + parentName;
    }

    public String getPlaceOfBirth() { return placeOfBirth; }
    public void setPlaceOfBirth(String v) { placeOfBirth = v; }

    public String getParentName() { return parentName; }
    public void setParentName(String v) { parentName = v; }
}
