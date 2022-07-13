package me.jazzyjake.main;

import me.jazzyjake.listeners.CommandListener;
import me.jazzyjake.listeners.HangoutRequestListener;
import me.jazzyjake.listeners.PossibleActivityReactionListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HangoutPlanningBot {
    public static final ResourceBundle PROPERTIES = ResourceBundle.getBundle("application");

    private static JDA jda;
    private static final Logger LOG = LoggerFactory.getLogger(HangoutPlanningBot.class);

    public static final List<String> CURRENT_POSSIBLE_ACTIVITY_EMBEDS = new ArrayList<>();

    public static void main(String[] args) throws SQLException {
        // Builds the JDA bot instance for use across the project
        try {
            jda = JDABuilder
                    .createDefault(PROPERTIES.getString("TOKEN"))
                    .addEventListeners(new HangoutRequestListener(), new CommandListener(), new PossibleActivityReactionListener())
                    .build();
        } catch (LoginException e) {
            LOG.error("Error occurred during bot login! Please check exception", e);
        }
    }

    public static JDA getJDA() {
        return jda;
    }


}
