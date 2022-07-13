package me.jazzyjake.embeds;

import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class ErrorEmbedBuilder extends EmbedBuilder {
    public ErrorEmbedBuilder(String message) {
        this.setTitle("Error Occurred!");
        this.setDescription(message);
        this.setColor(Color.RED);
    }
}
