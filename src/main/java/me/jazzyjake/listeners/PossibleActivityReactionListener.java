package me.jazzyjake.listeners;

import me.jazzyjake.data.Activity;
import me.jazzyjake.data.DerbyInterface;
import me.jazzyjake.embeds.ActivityBroadcastEmbedBuilder;
import me.jazzyjake.embeds.ErrorEmbedBuilder;
import me.jazzyjake.main.HangoutPlanningBot;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class PossibleActivityReactionListener extends ListenerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(PossibleActivityReactionListener.class);

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        // Checks if the message is in the current possible activities message list
        if (HangoutPlanningBot.CURRENT_POSSIBLE_ACTIVITY_EMBEDS.stream().anyMatch(s -> s.equals(event.getMessageId()))) {
            // Checks if the reaction was added in a private channel
            if (event.isFromType(ChannelType.PRIVATE)) {
                // Checks if the reaction was added by a user
                if (!event.retrieveUser().complete().isBot()) {

                    Emoji emoji = event.getEmoji();

                    // Checks if the reaction matches one of the three expected for accept, cancel, and skip
                    if (emoji.getName().equals("✅")) {
                        event.getChannel().sendMessage("**Request Accepted! Activity has been sent to planning channel**").queue();

                        // Gets the guild and text channel IDs from the properties file
                        String guildID = HangoutPlanningBot.PROPERTIES.getString("MAIN_GUILD_ID");
                        String planningChannelID = HangoutPlanningBot.PROPERTIES.getString("PLANNING_CHANNEL_ID");

                        // Gets the activity description from the message
                        Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
                        String description = message.getEmbeds().get(0).getTitle();

                        // Gets the planning channel specified in properties file
                        TextChannel channel = HangoutPlanningBot.getJDA().getGuildById(guildID).getTextChannelById(planningChannelID);

                        try {
                            // Gets the activity object from the database using the activity description
                            Activity activity = DerbyInterface.getActivityFromString(description);

                            broadcastActivity(channel, activity);
                        } catch (SQLException e) {
                            LOG.error("Error occurred when finding activity in database!", e);

                            MessageEmbed embed = new ErrorEmbedBuilder("Failed to retrieve activity from database! Please contact bot developer").build();
                            event.getChannel().sendMessageEmbeds(embed).queue();
                        }
                    } else if (emoji.getName().equals("❌")) {
                        event.getChannel().sendMessage("**Request Cancelled!**").queue();
                    } else if (emoji.getName().equals("➡")) {
                        event.getChannel().sendMessage("**Request Skipped!**").queue();

                        // Sends a new random activity request
                        HangoutRequestListener.sendRandomPrivateActivityRequest(event.getChannel());
                    }

                    // Removes the message ID from the global possible activities list to prevent duplicate reaction responses
                    HangoutPlanningBot.CURRENT_POSSIBLE_ACTIVITY_EMBEDS.remove(event.getMessageId());
                    LOG.info("Message with ID \"{}\" has been skipped and removed from current possible activities message ID list", event.getMessageId());
                }

                // Checks if the reaction is in the planning channel
            } else if (event.getChannel().getId().equals(HangoutPlanningBot.PROPERTIES.getString("PLANNING_CHANNEL_ID"))) {
                // Checks if the reaction added was the "skip" reaction
                if (event.getEmoji().getName().equals("➡")) {
                    // Gets the message reacted to
                    Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();

                    // Checks if the message reacted to is an actual activity broadcast
                    if (message.getEmbeds().get(0).getAuthor().getName().equals("Possible Activity")) {
                        int requiredVoteCount = Integer.parseInt(HangoutPlanningBot.PROPERTIES.getString("SKIP_ACTIVITY_VOTE_COUNT"));

                        // Checks to see if enough users have voted to skip this activity
                        if (message.getReaction(Emoji.fromUnicode("➡")).getCount() > requiredVoteCount) {
                            event.getChannel().sendMessage("**Adequate votes to skip! Broadcasting new random activity**").queue();

                            // Removes the message ID from the global possible activities list to prevent duplicate reaction responses
                            HangoutPlanningBot.CURRENT_POSSIBLE_ACTIVITY_EMBEDS.remove(message.getId());
                            LOG.info("Message with ID \"{}\" has been skipped and removed from current possible activities message ID list", message.getId());

                            // Broadcasts a new random activity to the planning channel
                            try {
                                broadcastActivity(event.getTextChannel(), DerbyInterface.getRandomActivity());
                            } catch (SQLException e) {
                                LOG.error("Error occurred when getting random activity from database!", e);

                                MessageEmbed embed = new ErrorEmbedBuilder("Failed to retrieve random activity from database! Please contact bot developer").build();
                                event.getChannel().sendMessageEmbeds(embed).queue();
                            }
                        }
                    }
                }
            }
        }
    }

    public static void broadcastActivity(TextChannel channel, Activity activity) {
        // Builds an activity broadcast embed and sends it to the planning channel
        MessageEmbed embed = new ActivityBroadcastEmbedBuilder(activity).build();
        channel.sendMessageEmbeds(embed).queue(msg -> {
            msg.addReaction(Emoji.fromUnicode("1️⃣")).queue();
            msg.addReaction(Emoji.fromUnicode("2️⃣")).queue();
            msg.addReaction(Emoji.fromUnicode("3️⃣")).queue();
            msg.addReaction(Emoji.fromUnicode("4️⃣")).queue();
            msg.addReaction(Emoji.fromUnicode("5️⃣")).queue();
            msg.addReaction(Emoji.fromUnicode("6️⃣")).queue();
            msg.addReaction(Emoji.fromUnicode("7️⃣")).queue();
            msg.addReaction(Emoji.fromUnicode("➡")).queue();

            // Adds the message ID to the global current possible activities ID list
            HangoutPlanningBot.CURRENT_POSSIBLE_ACTIVITY_EMBEDS.add(msg.getId());
            LOG.info("Message with ID \"{}\" added to current possible activities message ID list", msg.getId());
        });

        LOG.info("Activity \"{}\" with ID {} has been broadcasted!", activity.getActivity(), activity.getId());
    }
}
