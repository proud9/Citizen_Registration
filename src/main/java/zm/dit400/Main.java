package zm.dit400;

import io.javalin.Javalin;
import io.javalin.json.JavalinGson;
import zm.dit400.repository.JsonRecordRepository;
import zm.dit400.service.RecordService;
import zm.dit400.web.RecordController;
import java.nio.file.Path;

public final class Main {
    private Main() {}
    public static void main(String[] args) {
        var repository = new JsonRecordRepository(Path.of("data", "records.json"));
        var controller = new RecordController(new RecordService(repository));
        Javalin app = Javalin.create(config -> {
            config.jsonMapper(new JavalinGson());
            config.staticFiles.add("/public");
        });
        controller.register(app);
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 7070;
        app.start(port);
        System.out.println("Citizen Record demo running at http://localhost:" + port);
    }
}
