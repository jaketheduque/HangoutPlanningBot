package me.jazzyjake.embeds;

import me.jazzyjake.data.Activity;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class ActivityBroadcastEmbedBuilder extends EmbedBuilder {
    public ActivityBroadcastEmbedBuilder(Activity activity) {
        this.setAuthor("Possible Activity");
        this.setTitle(activity.getActivity());
        this.setDescription("Using the numbers below, please indicate your availability\n\nEach number corresponds to a day of the week (ex. 1️⃣ - Sunday, 2️⃣ - Monday, 3️⃣ - Tuesday, etc.)\n\nThe number corresponding to the current day of the week refers to the following week (a.k.a not today)\n\nOtherwise, use the ➡ reaction to vote for another activity");
        this.setColor(Color.WHITE);
    }
}
