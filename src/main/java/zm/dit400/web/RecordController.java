package zm.dit400.web;

import io.javalin.Javalin;
import io.javalin.http.Context;
import zm.dit400.exception.InvalidRecordException;
import zm.dit400.exception.RecordNotFoundException;
import zm.dit400.model.*;
import zm.dit400.service.RecordService;
import java.util.Map;

public class RecordController {
    private final RecordService service;

    public RecordController(RecordService service) {
        this.service = service;
    }

    public void register(Javalin app) {
        app.exception(RecordNotFoundException.class, (e, c) -> error(c, 404, e.getMessage()));
        app.exception(InvalidRecordException.class, (e, c) -> error(c, 400, e.getMessage()));
        app.exception(IllegalArgumentException.class, (e, c) -> error(c, 400, e.getMessage()));
        app.exception(Exception.class, (e, c) -> {
            System.err.println("Request failed: " + e.getClass().getSimpleName());
            error(c, 500, "Unexpected server error");
        });

        app.get("/api/records", c -> c.json(service.all()));
        app.get("/api/records/{id}", c -> c.json(service.get(c.pathParam("id"))));
        app.delete("/api/records/{id}", c -> {
            service.delete(c.pathParam("id"));
            c.status(204);
        });

        app.post("/api/birth-records", c -> {
            BirthRecord r = service.createBirth(c.bodyAsClass(BirthRecord.class));
            c.status(201).json(r);
        });
        app.put("/api/birth-records/{id}", c -> c.json(service.updateBirth(c.pathParam("id"), c.bodyAsClass(BirthRecord.class))));
        app.delete("/api/birth-records/{id}", c -> {
            service.delete(c.pathParam("id"));
            c.status(204);
        });

        app.post("/api/nrc-applications", c -> {
            NRCApplication r = service.createNrc(c.bodyAsClass(NRCApplication.class));
            c.status(201).json(r);
        });
        app.put("/api/nrc-applications/{id}", c -> c.json(service.updateNrc(c.pathParam("id"), c.bodyAsClass(NRCApplication.class))));
        app.delete("/api/nrc-applications/{id}", c -> {
            service.delete(c.pathParam("id"));
            c.status(204);
        });
    }

    private static void error(Context c, int status, String message) {
        c.status(status).json(Map.of("error", message == null ? "Request failed" : message));
    }
}
