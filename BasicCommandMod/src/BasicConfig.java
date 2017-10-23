import java.util.ArrayList;
import java.util.List;

public class BasicConfig {

    public List<CommandConfig> commands = new ArrayList<>();

    public static class CommandConfig {
        public String name = "";
        public String video = "";
    }
}
