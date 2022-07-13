package me.jazzyjake.embeds;

import me.jazzyjake.data.Activity;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class ActivityRequestEmbedBuilder extends EmbedBuilder {
    public ActivityRequestEmbedBuilder(Activity activity) {
        this.setAuthor("Possible Activity");
        this.setTitle(activity.getActivity());
        this.setDescription("Using the reactions below, please accept this request to send it to the planning channel, skip in order to get a different activity, or cancel the request altogether");
        this.setColor(Color.WHITE);
    }
}
