import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class MovieRecommendationSystem extends JFrame {
    private final JTextField searchField;
    private final JTextArea resultArea;

    public MovieRecommendationSystem() {
        setTitle("Movie Recommendation System");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // UI Components
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Recommend");
        resultArea = new JTextArea();
        resultArea.setEditable(false);

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Enter Movie Name:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        // Button Action Listener
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getRecommendations();
            }
        });
    }

    private void getRecommendations() {
        try {
            String movie_name = searchField.getText().trim();
            String apiUrl = "http://127.0.0.1:5000/recommend?movie_name=" + URLEncoder.encode(movie_name, StandardCharsets.UTF_8);

            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                resultArea.setText("Error: Could not fetch recommendations.");
                return;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            // Parse JSON response
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray recommendations = jsonResponse.getJSONArray("recommendations");

            resultArea.setText("Recommended Movies for User " + movie_name + ":\n");
            for (int i = 0; i < recommendations.length(); i++) {
                JSONObject movie = recommendations.getJSONObject(i);
                resultArea.append("- " + movie.getString("title") + " (ID: " + movie.getInt("movieId") + ")\n");
            }
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
            resultArea.setText("Error fetching recommendations.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MovieRecommendationSystem frame = new MovieRecommendationSystem();
            frame.setVisible(true);
        });
    }
}
