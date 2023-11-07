package pw.octane.manager;

import lombok.Data;

import java.util.List;

public @Data class ModuleInformation {
    private final String name, version, description, author, mainClass;
    private final List<String> dependencies;
}
