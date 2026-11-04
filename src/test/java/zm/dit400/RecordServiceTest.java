package zm.dit400;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import zm.dit400.exception.RecordNotFoundException;
import zm.dit400.model.*;
import zm.dit400.repository.JsonRecordRepository;
import zm.dit400.service.RecordService;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class RecordServiceTest {
    @TempDir Path temp;
    @Test void createsAndPersistsDifferentSubtypes() {
        var repo=new JsonRecordRepository(temp.resolve("records.json"));var service=new RecordService(repo);
        var b=service.createBirth(new BirthRecord("Demo Person A","2000-01-15","Central","Demo Town","Demo Parent A"));
        var n=service.createNrc(new NRCApplication("Demo Person B","1999-05-20","Copperbelt","pending"));
        assertEquals("BR-0001",b.getId());assertEquals("NRC-APP-0001",n.getId());
        var loaded=new JsonRecordRepository(temp.resolve("records.json")).findAll();
        assertInstanceOf(BirthRecord.class,loaded.get(0));assertInstanceOf(NRCApplication.class,loaded.get(1));
        assertEquals("BIRTH",loaded.get(0).getRecordType());assertEquals("PENDING",((NRCApplication)loaded.get(1)).getApplicationStatus());
    }
    @Test void rejectsBadDatesAndMissingValues(){var s=new RecordService(new JsonRecordRepository(temp.resolve("x.json")));
        assertThrows(IllegalArgumentException.class,()->s.createBirth(new BirthRecord(" ","tomorrow","Central","X","Y")));
    }
    @Test void missingRecordHasCustomException(){var s=new RecordService(new JsonRecordRepository(temp.resolve("x.json")));assertThrows(RecordNotFoundException.class,()->s.get("BR-9999"));}
}
