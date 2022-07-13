package me.jazzyjake.listeners;

import me.jazzyjake.data.Activity;
import me.jazzyjake.data.DerbyInterface;
import me.jazzyjake.embeds.ActivityListEmbedBuilder;
import me.jazzyjake.embeds.ConfirmationEmbedBuilder;
import me.jazzyjake.embeds.ErrorEmbedBuilder;
import me.jazzyjake.main.HangoutPlanningBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.sql.SQLException;

public class CommandListener extends ListenerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(CommandListener.class);

    // Creates the embed for the help command once at startup
    private static final MessageEmbed HELP_EMBED = new EmbedBuilder()
            .setTitle("Hangout Bot Command List")
            .setDescription("All commands and their usages are shown below :)")
            .setColor(Color.WHITE)
            .addField("!help", "Usage: !help", false)
            .addField("!addactivity", "Usage: !addactivity <activity>", false)
            .addField("!removeactivity", "Usage: !removeactivity <activity>", false)
            .addField("!allactivities", "Usage: !allactivities", false)
            .build();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Checks if the message was sent by a user
        if (!event.getAuthor().isBot()) {
            // Checks if the message was sent in the commands channel or in a private channel
            if (event.getChannel().getId().equals(HangoutPlanningBot.PROPERTIES.getString("COMMAND_CHANNEL_ID"))) {
                // Checks if the message starts with '!'
                if (event.getMessage().getContentStripped().startsWith("!")) {
                    String[] params = event.getMessage().getContentStripped().split(" ");

                    // Runs the corresponding case for the command
                    String command = params[0];
                    if (command.equalsIgnoreCase("!addactivity")) { // Add idea command
                        // Params check
                        if (params.length < 2) {
                            MessageEmbed embed = new ErrorEmbedBuilder("Please provide an argument!!").build();
                            event.getChannel().sendMessageEmbeds(embed).queue();
                            return;
                        }

                        // Gets the full idea string by removing the command from in front
                        String activity = event.getMessage().getContentStripped().replaceAll("!addactivity ", "");

                        // Inserts activity into Derby database
                        try {
                            DerbyInterface.insertActivity(activity);
                        } catch (SQLException e) { // If SQL exception occurs
                            LOG.error("Error occurred when inserting new activity!", e);

                            MessageEmbed embed = new ErrorEmbedBuilder("Failed to insert new activity! Please contact bot developer").build();
                            event.getChannel().sendMessageEmbeds(embed).queue();
                            return;
                        }

                        MessageEmbed embed = new ConfirmationEmbedBuilder("Activity Added!", "Added **" + activity + "** to activity list!").build();
                        event.getChannel().sendMessageEmbeds(embed).queue();
                    } else if (command.equalsIgnoreCase("!removeactivity")) { // Remove idea command
                        // Params check
                        if (params.length < 2) {
                            MessageEmbed embed = new ErrorEmbedBuilder("Please provide an argument!").build();
                            event.getChannel().sendMessageEmbeds(embed).queue();
                            return;
                        }

                        // Gets the activity ID from the second argument
                        try {
                            Long id = Long.parseLong(params[1]);

                            // Removes the activity from the database
                            DerbyInterface.removeActivity(id);

                            MessageEmbed embed = new ConfirmationEmbedBuilder("Activity Removed!", "Removed activity with id **" + params[1] + "** from the activity list!").build();
                            event.getChannel().sendMessageEmbeds(embed).queue();
                        } catch (NumberFormatException e) { // If second argument is not a valid long
                            MessageEmbed embed = new ErrorEmbedBuilder("Invalid ID argument!").build();
                            event.getChannel().sendMessageEmbeds(embed).queue();
                        } catch (SQLException e) { // If SQL exception occurs
                            LOG.error("Error occurred when removing activity!", e);

                            MessageEmbed embed = new ErrorEmbedBuilder("Failed to remove activity! Please contact bot developer").build();
                            event.getChannel().sendMessageEmbeds(embed).queue();
                        }
                    } else if (command.equalsIgnoreCase("!allactivities")) {
                        try {
                            // Gets full activity list in the database and sends an embed to the channel containing them
                            Activity[] activities = DerbyInterface.getAllActivities();
                            MessageEmbed embed = new ActivityListEmbedBuilder(activities).build();
                            event.getChannel().sendMessageEmbeds(embed).queue();
                        } catch (SQLException e) {
                            LOG.error("Error occurred when retrieving activity list!", e);

                            MessageEmbed embed = new ErrorEmbedBuilder("Failed to retrieve activity list! Please contact bot developer").build();
                            event.getChannel().sendMessageEmbeds(embed).queue();
                            return;
                        }
                    } else if (command.equalsIgnoreCase("!help")) { // Help command
                        event.getChannel().sendMessageEmbeds(HELP_EMBED).queue();
                    } else { // If command was not found
                        MessageEmbed embed = new ErrorEmbedBuilder("Command not found!").build();
                        event.getChannel().sendMessageEmbeds(embed).queue();
                    }
                }
            }
        }
    }
}
