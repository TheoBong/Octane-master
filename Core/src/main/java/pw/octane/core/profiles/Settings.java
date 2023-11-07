package pw.octane.core.profiles;

import com.google.gson.annotations.Expose;
import lombok.Data;

import java.io.Serializable;

public @Data class Settings {
    private @Expose boolean globalChat = true, privateMessages = true, staffChat = false, staffMessages = true;
}
