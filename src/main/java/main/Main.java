package main;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.net.URI.*;

@SpringBootApplication
public class Main  {

    public static void main (String [] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(Main.class);
        builder.headless(false);
        builder.run(args);
    }

    @PostConstruct
    public void init () throws IOException, InterruptedException {
        JFrame frame = new JFrame();
        frame.setSize(new Dimension(1000, 600));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        JPanel content = new JPanel(new BorderLayout());
        JTextField searchbar = new JTextField();
        content.add(searchbar, BorderLayout.PAGE_START);
        JPanel page = new JPanel();
        page.setBackground(Color.GRAY);
        content.add(page, BorderLayout.CENTER);

        frame.setContentPane(content);
        frame.setVisible(true);


        var uri = create("https://www.york.ac.uk/teaching/cws/wws/webpage1.html");
        var client = HttpClient.newHttpClient();
        var request = HttpRequest
                .newBuilder()
                .uri(uri)
                .header("accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.statusCode());
        System.out.println(response.body());
    }

}
