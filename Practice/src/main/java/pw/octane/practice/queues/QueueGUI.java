package pw.octane.practice.queues;

import pw.octane.practice.PracticeModule;
import pw.octane.practice.kits.Kit;
import pw.octane.practice.profiles.Profile;
import xyz.leuo.gooey.button.Button;
import xyz.leuo.gooey.gui.GUI;

public class QueueGUI extends GUI {
    public QueueGUI(PracticeModule module, Profile profile, PracticeQueue.Type type) {
        super(type.toString()+ " Queue", 9);

        this.setAutoRefresh(true);
        this.setUpdate(gui -> {
            gui.getButtons().clear();
            for(PracticeQueue queue : module.getQueueManager().getPracticeQueues()) {
                if(queue.getType().equals(PracticeQueue.Type.UNRANKED)) {
                    Kit kit = queue.getKit();
                    Button button = new Button(kit.getIcon(), kit.getColor() + kit.getDisplayName());
                    int playing = module.getOccupationManager().getInGame(kit, queue.getType());

                    button.setAmount(Math.min(playing + 1, 64));
                    button.setLore(
                            "&bIn Queue: &f" + queue.size(),
                            "&bPlaying: &f" + playing,
                            "",
                            "&7Click to queue for " + queue.getType().toString() + " " + kit.getDisplayName() +  "."
                    );
                    button.setButtonAction((player1, gui1, button1, inventoryClickEvent) -> {
                        queue.join(new QueueMember(queue, player1.getUniqueId()));
                        profile.playerItems();
                    });
                    button.setCloseOnClick(true);

                    gui.setButton(kit.getUnrankedPosition(), button);
                }
            }
        });

        this.update();
    }
}
