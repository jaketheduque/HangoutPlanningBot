package me.jazzyjake.embeds;

import me.jazzyjake.data.Activity;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class ActivityListEmbedBuilder extends EmbedBuilder {
    public ActivityListEmbedBuilder(Activity[] activities) {
        this.setTitle("Activity List");
        this.setColor(Color.BLUE);

        if (activities.length == 0) {
            this.setDescription("No current activites!");
        } else {
            // Creates a field for each individual activity in the list
            for (Activity activity : activities) {
                this.addField(activity.getActivity(), String.format("ID: %d", activity.getId()), false);
            }
        }
    }
}
