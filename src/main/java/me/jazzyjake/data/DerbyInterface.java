package me.jazzyjake.data;

import me.jazzyjake.main.HangoutPlanningBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DerbyInterface {
    private static final Logger LOG = LoggerFactory.getLogger(DerbyInterface.class);

    private static final String DERBY_URL = HangoutPlanningBot.PROPERTIES.getString("DERBY_URL");

    public static void insertActivity(String activity) throws SQLException {
        // Initiates a connection to the Derby database prepares a statement for activity insertion
        try (Connection conn = DriverManager.getConnection(DERBY_URL);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO Activities (activity) VALUES (?)")) {

            stmt.setString(1, activity);
            stmt.executeUpdate();

            LOG.info("New activity \"{}\" was inserted into database!", activity);
        }
    }

    public static void removeActivity(long id) throws SQLException {
        // Initiates a connection to the Derby database prepares a statement for activity deletion
        try (Connection conn = DriverManager.getConnection(DERBY_URL);
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM Activities WHERE id=?")) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

            LOG.info("Activity with ID \"{}\" was deleted from the database!", id);
        }
    }

    public static Activity[] getAllActivities() throws SQLException {
        List<Activity> ideas = new ArrayList<>();

        // Initiates a connection to the Derby database and gets all results from Ideas table
        try (Connection conn = DriverManager.getConnection(DERBY_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Activities");
             ResultSet rs = stmt.executeQuery()) {

            // Creates an Activity object for each row
            while (rs.next()) {
                ideas.add(new Activity(rs.getInt(1), rs.getString(2)));
            }
        }

        return ideas.toArray(new Activity[0]);
    }

    public static Activity getRandomActivity() throws SQLException {
        Activity[] activities = getAllActivities();

        return activities[new Random().nextInt(activities.length)];
    }

    public static Activity getActivityFromString(String activity) throws SQLException {
        // Initiates a connection to the Derby database and the matching result from Ideas table
        try (Connection conn = DriverManager.getConnection(DERBY_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Activities WHERE activity=?")) {

            stmt.setString(1, activity);

            try (ResultSet rs = stmt.executeQuery()) {
                // Creates the Activity object to be returned
                while (rs.next()) {
                    return new Activity(rs.getInt(1), rs.getString(2));
                }
            }
        }
        return null;
    }

    public static Activity getActivityFromID(long id) throws SQLException {
        // Initiates a connection to the Derby database and the matching result from Ideas table
        try (Connection conn = DriverManager.getConnection(DERBY_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Activities WHERE id=?")) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                // Creates the Activity object to be returned
                while (rs.next()) {
                    return new Activity(rs.getInt(1), rs.getString(2));
                }
            }
        }
        return null;
    }
}
