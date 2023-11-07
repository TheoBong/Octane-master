package pw.octane.practice.parties;

import lombok.Data;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public @Data class PartyInvite {

    private final UUID uuid;
    private Party party;
    private Date expires;
    public PartyInvite(UUID uuid) {
        this.uuid = uuid;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, 30);
        this.expires = calendar.getTime();
    }
}
