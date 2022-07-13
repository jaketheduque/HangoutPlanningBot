package me.jazzyjake.embeds;

import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class ConfirmationEmbedBuilder extends EmbedBuilder {
    public ConfirmationEmbedBuilder(String title, String description) {
        this.setTitle(title);
        this.setDescription(description);
        this.setColor(Color.GREEN);
    }
}
