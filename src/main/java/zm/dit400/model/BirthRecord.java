package zm.dit400.model;
public class BirthRecord extends CitizenRecord {
    private final String recordType = "BIRTH";
    private String placeOfBirth; private String parentName;
    public BirthRecord() {}
    public BirthRecord(String name,String dob,String province,String place,String parent){super(name,dob,province);placeOfBirth=place;parentName=parent;}
    @Override public String getRecordType(){return "BIRTH";}
    public String getPlaceOfBirth(){return placeOfBirth;} public void setPlaceOfBirth(String v){placeOfBirth=v;}
    public String getParentName(){return parentName;} public void setParentName(String v){parentName=v;}
}
