package me.jazzyjake.listeners;

import me.jazzyjake.data.Activity;
import me.jazzyjake.data.DerbyInterface;
import me.jazzyjake.embeds.ActivityRequestEmbedBuilder;
import me.jazzyjake.embeds.ErrorEmbedBuilder;
import me.jazzyjake.main.HangoutPlanningBot;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class HangoutRequestListener extends ListenerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(HangoutRequestListener.class);

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Checks if the message is from a private channel (Direct Message)
        if (event.isFromType(ChannelType.PRIVATE)) {
            String[] params = event.getMessage().getContentStripped().split(" ");

            // Checks if the message is the !request command
            if (params[0].equals("!request")) {
                // Checks if an activity ID was supplied with the request
                if (params.length > 1) {
                    // Parses the first parameter into a long
                    try {
                        long id = Long.parseLong(params[1]);

                        LOG.info("Activity request received for activity ID #{} from: {}",  id, event.getAuthor().getAsTag());

                        // Gets the activity with the specified ID
                        Activity activity = DerbyInterface.getActivityFromID(id);

                        // Gets the planning text channel using the guild and planning text channel IDs from the properties file
                        String guildID = HangoutPlanningBot.PROPERTIES.getString("MAIN_GUILD_ID");
                        String planningChannelID = HangoutPlanningBot.PROPERTIES.getString("PLANNING_CHANNEL_ID");
                        TextChannel channel = HangoutPlanningBot.getJDA().getGuildById(guildID).getTextChannelById(planningChannelID);

                        // Broadcasts the selected activity to the planning text channel
                        PossibleActivityReactionListener.broadcastActivity(channel, activity);

                        event.getChannel().sendMessage(String.format("**Activity \"%s\" with ID #%d has been broadcasted!**", activity.getActivity(), activity.getId())).queue();
                    } catch (NumberFormatException e) {
                        MessageEmbed embed = new ErrorEmbedBuilder("Invalid ID argument!").build();
                        event.getChannel().sendMessageEmbeds(embed).queue();
                    } catch (SQLException e) {
                        LOG.error("Error occurred when getting activity!", e);

                        MessageEmbed embed = new ErrorEmbedBuilder("Failed to get activity from database! Please contact bot developer").build();
                        event.getChannel().sendMessageEmbeds(embed).queue();
                    }
                } else {
                    LOG.info("Activity request received from: {}", event.getAuthor().getAsTag());

                    sendRandomPrivateActivityRequest(event.getChannel());
                }
            }
        }
    }

    public static void sendRandomPrivateActivityRequest(MessageChannel channel) {
        try {
            // Gets a random activity from the activity list
            Activity activity = DerbyInterface.getRandomActivity();

            // Creates the request embed and sends it to the user
            MessageEmbed embed = new ActivityRequestEmbedBuilder(activity).build();
            channel.sendMessageEmbeds(embed).queue(msg -> {
                // Adds the accept, cancel, and skip reactions to the message
                msg.addReaction(Emoji.fromUnicode("✅")).queue();
                msg.addReaction(Emoji.fromUnicode("❌")).queue();
                msg.addReaction(Emoji.fromUnicode("➡")).queue();

                // Adds the message ID to the global current possible activities ID list
                HangoutPlanningBot.CURRENT_POSSIBLE_ACTIVITY_EMBEDS.add(msg.getId());
                LOG.info("Message with ID \"{}\" added to current possible activities message ID list", msg.getId());
            });
        } catch (SQLException e) {
            LOG.error("Error occurred when retrieving activity list!", e);

            MessageEmbed embed = new ErrorEmbedBuilder("Failed to retrieve activity list! Please contact bot developer").build();
            channel.sendMessageEmbeds(embed).queue();
        }
    }
}
