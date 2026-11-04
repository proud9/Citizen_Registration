package zm.dit400.service;

import zm.dit400.exception.RecordNotFoundException;
import zm.dit400.model.*;
import zm.dit400.repository.RecordRepository;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;

public class RecordService {
    private final RecordRepository repository;
    public RecordService(RecordRepository repository){this.repository=repository;}
    public List<CitizenRecord> all(){return repository.findAll();}
    public CitizenRecord get(String id){return repository.findById(id).orElseThrow(()->new RecordNotFoundException(id));}
    private String required(String v,String field){if(v==null||v.isBlank())throw new IllegalArgumentException(field+" is required");String s=v.trim();if(s.length()>120)throw new IllegalArgumentException(field+" must be 120 characters or fewer");return s;}
    private void validate(CitizenRecord r){required(r.getFullName(),"fullName");required(r.getProvince(),"province");
        String dob=required(r.getDateOfBirth(),"dateOfBirth");try{LocalDate d=LocalDate.parse(dob);if(d.isAfter(LocalDate.now()))throw new IllegalArgumentException("dateOfBirth cannot be in the future");}catch(DateTimeParseException e){throw new IllegalArgumentException("dateOfBirth must use YYYY-MM-DD");}
        if(r instanceof BirthRecord b){required(b.getPlaceOfBirth(),"placeOfBirth");required(b.getParentName(),"parentName");}
        if(r instanceof NRCApplication n){String status=required(n.getApplicationStatus(),"applicationStatus");if(!Set.of("PENDING","PROCESSING","COMPLETED","REJECTED").contains(status.toUpperCase(Locale.ROOT)))throw new IllegalArgumentException("applicationStatus must be PENDING, PROCESSING, COMPLETED, or REJECTED");n.setApplicationStatus(status.toUpperCase(Locale.ROOT));}
        r.setFullName(r.getFullName().trim());r.setProvince(r.getProvince().trim());r.setDateOfBirth(dob);
    }
    private String nextId(String prefix){int max=repository.findAll().stream().map(CitizenRecord::getId).filter(Objects::nonNull).filter(s->s.startsWith(prefix)).mapToInt(s->{try{return Integer.parseInt(s.substring(prefix.length()));}catch(NumberFormatException e){return 0;}}).max().orElse(0);return prefix+String.format("%04d",max+1);}
    public BirthRecord createBirth(BirthRecord r){validate(r);r.setId(nextId("BR-"));r.setCreatedAt(Instant.now().toString());return (BirthRecord)repository.save(r);}
    public NRCApplication createNrc(NRCApplication r){validate(r);r.setId(nextId("NRC-APP-"));r.setCreatedAt(Instant.now().toString());return (NRCApplication)repository.save(r);}
    public BirthRecord updateBirth(String id,BirthRecord input){CitizenRecord old=get(id);if(!(old instanceof BirthRecord))throw new IllegalArgumentException("Record is not a birth record");validate(input);input.setId(id);input.setCreatedAt(old.getCreatedAt());return (BirthRecord)repository.save(input);}
    public NRCApplication updateNrc(String id,NRCApplication input){CitizenRecord old=get(id);if(!(old instanceof NRCApplication))throw new IllegalArgumentException("Record is not an NRC application");validate(input);input.setId(id);input.setCreatedAt(old.getCreatedAt());return (NRCApplication)repository.save(input);}
    public void delete(String id){if(!repository.deleteById(id))throw new RecordNotFoundException(id);}
}
