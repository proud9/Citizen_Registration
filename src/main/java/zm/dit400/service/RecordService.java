package zm.dit400.service;

import zm.dit400.exception.InvalidRecordException;
import zm.dit400.exception.RecordNotFoundException;
import zm.dit400.model.*;
import zm.dit400.repository.RecordRepository;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;

public class RecordService {
    private final RecordRepository repository;

    public RecordService(RecordRepository repository) {
        this.repository = repository;
    }

    public List<CitizenRecord> all() {
        return repository.findAll();
    }

    public CitizenRecord get(String id) {
        return repository.findById(id).orElseThrow(() -> new RecordNotFoundException(id));
    }

    private String required(String v, String field) {
        if (v == null || v.isBlank()) {
            throw new InvalidRecordException(field + " is required");
        }
        String s = v.trim();
        if (s.length() > 120) {
            throw new InvalidRecordException(field + " must be 120 characters or fewer");
        }
        return s;
    }

    public void validate(CitizenRecord r) {
        required(r.getFullName(), "fullName");
        required(r.getProvince(), "province");
        String dob = required(r.getDateOfBirth(), "dateOfBirth");
        try {
            LocalDate d = LocalDate.parse(dob);
            if (d.isAfter(LocalDate.now())) {
                throw new InvalidRecordException("dateOfBirth cannot be in the future");
            }
        } catch (DateTimeParseException e) {
            throw new InvalidRecordException("dateOfBirth must use YYYY-MM-DD");
        }
        r.setFullName(r.getFullName().trim());
        r.setProvince(r.getProvince().trim());
        r.setDateOfBirth(dob);

        // Polymorphic validation: each subtype validates its own specific fields without instanceof checks!
        r.validateDetails();
    }

    private String nextId(String prefix) {
        int max = repository.findAll().stream()
                .map(CitizenRecord::getId)
                .filter(Objects::nonNull)
                .filter(s -> s.startsWith(prefix))
                .mapToInt(s -> {
                    try {
                        return Integer.parseInt(s.substring(prefix.length()));
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                })
                .max()
                .orElse(0);
        return prefix + String.format("%04d", max + 1);
    }

    public BirthRecord createBirth(BirthRecord r) {
        validate(r);
        r.setId(nextId("BR-"));
        r.setCreatedAt(Instant.now().toString());
        return (BirthRecord) repository.save(r);
    }

    public NRCApplication createNrc(NRCApplication r) {
        validate(r);
        r.setId(nextId("NRC-APP-"));
        r.setCreatedAt(Instant.now().toString());
        return (NRCApplication) repository.save(r);
    }

    public BirthRecord updateBirth(String id, BirthRecord input) {
        CitizenRecord old = get(id);
        if (!(old instanceof BirthRecord)) {
            throw new InvalidRecordException("Record is not a birth record: " + id);
        }
        validate(input);
        input.setId(id);
        input.setCreatedAt(old.getCreatedAt());
        return (BirthRecord) repository.save(input);
    }

    public NRCApplication updateNrc(String id, NRCApplication input) {
        CitizenRecord old = get(id);
        if (!(old instanceof NRCApplication)) {
            throw new InvalidRecordException("Record is not an NRC application: " + id);
        }
        validate(input);
        input.setId(id);
        input.setCreatedAt(old.getCreatedAt());
        return (NRCApplication) repository.save(input);
    }

    public void delete(String id) {
        if (!repository.deleteById(id)) {
            throw new RecordNotFoundException(id);
        }
    }
}
