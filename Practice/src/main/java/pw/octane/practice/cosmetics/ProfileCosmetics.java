package pw.octane.practice.cosmetics;

import com.google.gson.annotations.Expose;
import lombok.Data;

public @Data class ProfileCosmetics {

    private @Expose EliminateAnimation eliminateAnimation = EliminateAnimation.BLOOD;
    private @Expose WinAnimation winAnimation = WinAnimation.FIREWORKS;
}
