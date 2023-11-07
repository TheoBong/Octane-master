package pw.octane.practice.parties;

import pw.octane.practice.PracticeModule;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PartyManager {

     private PracticeModule module;
     private Map<UUID, Party> parties;
     public PartyManager(PracticeModule module) {
         this.module = module;
         this.parties = new HashMap<>();
     }
}
