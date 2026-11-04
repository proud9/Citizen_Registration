package zm.dit400.model;
public class NRCApplication extends CitizenRecord {
    private final String recordType = "NRC_APPLICATION";
    private String applicationStatus;
    public NRCApplication() {}
    public NRCApplication(String name,String dob,String province,String status){super(name,dob,province);applicationStatus=status;}
    @Override public String getRecordType(){return "NRC_APPLICATION";}
    public String getApplicationStatus(){return applicationStatus;} public void setApplicationStatus(String v){applicationStatus=v;}
}
