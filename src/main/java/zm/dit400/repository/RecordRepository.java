package zm.dit400.repository;
import zm.dit400.model.CitizenRecord;
import java.util.List;
import java.util.Optional;
public interface RecordRepository {
    List<CitizenRecord> findAll(); Optional<CitizenRecord> findById(String id);
    CitizenRecord save(CitizenRecord record); boolean deleteById(String id);
}
