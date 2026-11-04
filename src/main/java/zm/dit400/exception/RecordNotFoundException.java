package zm.dit400.exception;
public class RecordNotFoundException extends RuntimeException { public RecordNotFoundException(String id){super("Record not found: "+id);} }
