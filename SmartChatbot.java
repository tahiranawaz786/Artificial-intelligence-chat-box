package smartchat;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.*;

public class SmartChatbot extends JFrame {

    private JTextArea chatArea;
    private JTextField userInput;
    private JButton sendButton, clearButton, toggleHistoryButton;
    private final Map<String, String> faqMap = new LinkedHashMap<>();
    private final List<String> reminders = new ArrayList<>();
    private String userName = "there";
    private JTextArea historyArea;
    private JPanel historyPanel;
    private boolean isHistoryVisible = false;

    public SmartChatbot() {
        setTitle("☁ Smart Chatbot AI");
        setSize(600, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initializeFAQs();
        initGUI();
    }

    private void initGUI() {
        getContentPane().setBackground(new Color(255, 248, 243));
        setLayout(new BorderLayout());

        JLabel header = new JLabel("☁ Smart Chatbot AI", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 22));
        header.setForeground(new Color(84, 82, 111));
        header.setBorder(new EmptyBorder(15, 10, 15, 10));
        add(header, BorderLayout.NORTH);

        chatArea = new JTextArea();
        chatArea.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 14));
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setBackground(Color.WHITE);
        chatArea.setForeground(new Color(60, 60, 60));
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(new EmptyBorder(10, 15, 10, 15));
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBackground(new Color(255, 248, 243));
        inputPanel.setBorder(new EmptyBorder(10, 15, 15, 15));

        userInput = new JTextField();
        userInput.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 14));
        userInput.setBackground(Color.WHITE);
        userInput.setForeground(new Color(60, 60, 60));
        userInput.setBorder(BorderFactory.createLineBorder(new Color(221, 221, 221)));

        sendButton = new JButton("Send");
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sendButton.setBackground(new Color(190, 215, 233));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorderPainted(false);
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        clearButton = new JButton("Clear");
        clearButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        clearButton.setBackground(new Color(255, 153, 153));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.setBorderPainted(false);
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        toggleHistoryButton = new JButton("Toggle History");
        toggleHistoryButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        toggleHistoryButton.setBackground(new Color(169, 204, 227));
        toggleHistoryButton.setForeground(Color.WHITE);
        toggleHistoryButton.setFocusPainted(false);
        toggleHistoryButton.setBorderPainted(false);
        toggleHistoryButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 5, 0));
        buttonPanel.setBackground(new Color(255, 248, 243));
        buttonPanel.add(sendButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(toggleHistoryButton);

        inputPanel.add(userInput, BorderLayout.CENTER);
        inputPanel.add(buttonPanel, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> processInput());
        userInput.addActionListener(e -> processInput());
        clearButton.addActionListener(e -> clearChat());
        toggleHistoryButton.addActionListener(e -> toggleHistoryPanel());

        setupHistoryPanel();

        loadChatHistory();
        appendBotMessage("Hello! I’m your AI Assistant. Type 'help' to get started.");
    }

    private void setupHistoryPanel() {
        historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBackground(new Color(240, 240, 240));
        historyPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);
        historyArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        historyArea.setBackground(Color.WHITE);
        historyArea.setForeground(Color.DARK_GRAY);

        JScrollPane historyScroll = new JScrollPane(historyArea);
        historyPanel.add(new JLabel("Chat + Reminder History"), BorderLayout.NORTH);
        historyPanel.add(historyScroll, BorderLayout.CENTER);
        historyPanel.setPreferredSize(new Dimension(580, 200));

        add(historyPanel, BorderLayout.EAST);
        historyPanel.setVisible(false);
    }

    private void toggleHistoryPanel() {
        if (!isHistoryVisible) {
            StringBuilder sb = new StringBuilder();
            sb.append("--- Chat History ---\n");
            try (BufferedReader reader = new BufferedReader(new FileReader("chat_history.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } catch (IOException e) {
                sb.append("(No chat history found)\n");
            }

            sb.append("\n--- Reminders ---\n");
            try (BufferedReader reader = new BufferedReader(new FileReader("reminders.txt"))) {
                String line;
                int i = 1;
                while ((line = reader.readLine()) != null) {
                    sb.append(i++).append(": ").append(line).append("\n");
                }
            } catch (IOException e) {
                sb.append("(No reminders found)\n");
            }

            historyArea.setText(sb.toString());
            historyPanel.setVisible(true);
        } else {
            historyPanel.setVisible(false);
        }
        isHistoryVisible = !isHistoryVisible;
    }

    private void initializeFAQs() {
        faqMap.put("hi", "Hello! How can I assist you today?");
        faqMap.put("hello", "Hi there! Need any help?");
        faqMap.put("how are you", "I'm running smoothly. Thanks for asking!");
        faqMap.put("what can you do", "I can answer FAQs, provide weather, currency info, set reminders, and more!");
        faqMap.put("bye", "Goodbye! Have a productive day.");
        faqMap.put("help", "Try things like:\n- What’s the weather?\n- Convert USD to PKR\n- Remind me to drink water\n- My name is Ali\n- Show reminders\n- Clear chat");
        faqMap.put("exit", "Session ended. You can now close the application.");
    }

    private void processInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) return;

        appendUserMessage(input);
        String response = generateResponse(input.toLowerCase());
        appendBotMessage(response);
        userInput.setText("");
        saveChatHistory();
    }

    private String generateResponse(String input) {
        if (input.contains("your name")) {
            return "I'm your personal AI, but you can also call me Chatty!";
        }

        for (Map.Entry<String, String> entry : faqMap.entrySet()) {
            if (input.contains(entry.getKey())) return entry.getValue();
        }

        if (input.contains("weather")) return "It's sunny, around 25°C. (Note: Real API not connected)";

        if (input.contains("convert")) {
            if (input.contains("usd") && input.contains("pkr")) return "1 USD ≈ 280 PKR";
            if (input.contains("pkr") && input.contains("usd")) return "1 PKR ≈ 0.0036 USD";
            if (input.contains("euro") && input.contains("usd")) return "1 Euro ≈ 1.08 USD";
            return "Supported: USD, PKR, Euro";
        }

        if (input.startsWith("remind me to")) {
            String reminder = input.substring(13).trim();
            reminders.add(reminder);
            saveReminders();
            return "Reminder saved: \"" + reminder + "\"";
        }

        if (input.contains("what are my reminders") || input.contains("show reminders")) {
            if (reminders.isEmpty()) return "You have no reminders yet.";
            StringBuilder sb = new StringBuilder("Here are your reminders:\n");
            for (int i = 0; i < reminders.size(); i++) {
                sb.append(i + 1).append(". ").append(reminders.get(i)).append("\n");
            }
            return sb.toString();
        }

        if (input.startsWith("my name is")) {
            userName = input.replace("my name is", "").trim();
            return "Nice to meet you, " + userName + "!";
        }

        if (input.contains("who am i")) return "You are " + userName + ", of course!";

        return "Hmm... I’m not trained on that yet. Try asking something else or type 'help'.";
    }

    private void clearChat() {
        chatArea.setText("");
        appendBotMessage("Chat cleared. Type anything to begin again.");
    }

    private void appendUserMessage(String message) {
        chatArea.append("[" + userName + " | " + currentTime() + "]: " + message + "\n");
    }

    private void appendBotMessage(String message) {
        chatArea.append("[Bot | " + currentTime() + "]: " + message + "\n\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    private String currentTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    private void saveChatHistory() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("chat_history.txt"))) {
            writer.write(chatArea.getText());
        } catch (IOException e) {
            appendBotMessage("Error saving chat history.");
        }
    }

    private void loadChatHistory() {
        File file = new File("chat_history.txt");
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                chatArea.append(line + "\n");
            }
            appendBotMessage("Previous chat history loaded.");
        } catch (IOException e) {
            appendBotMessage("Failed to load previous chat history.");
        }
    }

    private void saveReminders() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("reminders.txt"))) {
            for (String reminder : reminders) {
                writer.write(reminder);
                writer.newLine();
            }
        } catch (IOException e) {
            appendBotMessage("Error saving reminders.");
        }
    }

    private void loadReminders() {
        File file = new File("reminders.txt");
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                reminders.add(line);
            }
        } catch (IOException e) {
            appendBotMessage("Failed to load reminders.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SmartChatbot chatbot = new SmartChatbot();
            chatbot.loadReminders();
            chatbot.setVisible(true);
        });
    }
}
