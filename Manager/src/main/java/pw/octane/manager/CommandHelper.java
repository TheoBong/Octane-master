package pw.octane.manager;

import pw.octane.manager.utils.Colors;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CommandHelper {

    private String firstLine;
    private List<String> entries;
    public CommandHelper(String firstLine) {
        this.firstLine = firstLine;
        this.entries = new LinkedList<>();
    }

    public CommandHelper addEntry(String entry) {
        entries.add(entry);
        return this;
    }

    public List<String> getPage(int page) {
        List<String> list = new ArrayList<>();
        for(int i = page * 7; i < (page + 1) * 7; i++) {
            if(entries.size() > i) {
                list.add(entries.get(i));
            }
        }

        return list;
    }

    public String getMessage(int page) {
        if(getPage(page - 1).isEmpty()) {
            return Colors.get("&cPage not found.");
        }

        StringBuilder sb = new StringBuilder();
        sb.append(firstLine.replace("<page_number>", String.valueOf(page)));
        for(String s : getPage(page - 1)) {
            sb.append("\n" + s);
        }

        return Colors.get(sb.toString());
    }
}
