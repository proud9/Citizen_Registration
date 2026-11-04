package zm.dit400.repository;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import zm.dit400.model.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;

public class JsonRecordRepository implements RecordRepository {
    private final Path file; private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Type listType = new TypeToken<List<JsonElement>>(){}.getType();
    public JsonRecordRepository(Path file){this.file=file;}
    private synchronized List<CitizenRecord> read(){
        if(!Files.exists(file)) return new ArrayList<>();
        try {
            JsonArray arr=JsonParser.parseString(Files.readString(file)).getAsJsonArray(); List<CitizenRecord> out=new ArrayList<>();
            for(JsonElement e:arr){JsonObject o=e.getAsJsonObject(); String type=o.has("recordType")?o.get("recordType").getAsString():"";
                CitizenRecord r="BIRTH".equals(type)?gson.fromJson(o,BirthRecord.class):"NRC_APPLICATION".equals(type)?gson.fromJson(o,NRCApplication.class):null;
                if(r!=null) out.add(r);
            } return out;
        } catch(Exception ex){throw new IllegalStateException("Could not read the record store",ex);}
    }
    private synchronized void write(List<CitizenRecord> records){
        try {Path parent=file.getParent(); if(parent!=null)Files.createDirectories(parent); JsonArray arr=new JsonArray();
            for(CitizenRecord r:records){JsonObject o=gson.toJsonTree(r).getAsJsonObject();o.addProperty("recordType",r.getRecordType());arr.add(o);}
            Path tmp=file.resolveSibling(file.getFileName()+".tmp");Files.writeString(tmp,gson.toJson(arr),StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING);
            try{Files.move(tmp,file,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);}catch(AtomicMoveNotSupportedException ex){Files.move(tmp,file,StandardCopyOption.REPLACE_EXISTING);}
        }catch(IOException ex){throw new IllegalStateException("Could not save the record store",ex);}
    }
    public List<CitizenRecord> findAll(){return List.copyOf(read());}
    public Optional<CitizenRecord> findById(String id){return read().stream().filter(r->Objects.equals(r.getId(),id)).findFirst();}
    public synchronized CitizenRecord save(CitizenRecord record){List<CitizenRecord> all=read();all.removeIf(r->Objects.equals(r.getId(),record.getId()));all.add(record);write(all);return record;}
    public synchronized boolean deleteById(String id){List<CitizenRecord> all=read();boolean changed=all.removeIf(r->Objects.equals(r.getId(),id));if(changed)write(all);return changed;}
}
