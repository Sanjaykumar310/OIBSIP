
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class Main {
    private static final String DEMO_USERNAME = "student";
    private static final String DEMO_PASSWORD = "1234";

    private static final String[][] QUESTIONS = {
        {"Which keyword is used to inherit a class in Java?",
         "implements", "extends", "inherits", "super", "B"},
        {"Which method is the entry point of a Java application?",
         "start()", "run()", "main()", "init()", "C"},
        {"Which collection allows duplicate elements?",
         "Set", "List", "Map", "None of these", "B"},
        {"Which data type stores true or false?",
         "int", "char", "boolean", "double", "C"},
        {"Which keyword creates an object in Java?",
         "class", "new", "this", "void", "B"},
        {"Which exception can occur when dividing an integer by zero?",
         "IOException", "ArithmeticException", "SQLException",
         "ClassNotFoundException", "B"},
        {"Which keyword prevents a method from being overridden?",
         "static", "final", "private", "void", "B"},
        {"Which interface is commonly used to sort objects?",
         "Runnable", "Serializable", "Comparable", "Cloneable", "C"},
        {"Which loop executes at least once?",
         "for", "while", "do-while", "enhanced for", "C"},
        {"Which package contains the Scanner class?",
         "java.io", "java.util", "java.lang", "java.net", "B"}
    };

    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField nameField;
    private JLabel questionLabel;
    private JLabel timerLabel;
    private JRadioButton[] options;
    private ButtonGroup optionGroup;
    private JButton previousButton;
    private JButton nextButton;
    private int currentQuestion = 0;
    private int secondsRemaining = 300;
    private final String[] answers = new String[QUESTIONS.length];
    private Timer timer;
    private boolean submitted = false;
    private String studentName = "Student";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().showLogin());
    }

    private void showLogin() {
        frame = new JFrame("Online Examination System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 350);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("ONLINE EXAMINATION SYSTEM",
                SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel("Username:"), gbc);
        usernameField = new JTextField(18);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField(18);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(loginButton, gbc);

        JLabel hint = new JLabel("Demo: student / 1234",
                SwingConstants.CENTER);
        gbc.gridy++;
        panel.add(hint, gbc);

        loginButton.addActionListener(e -> authenticate());

        frame.add(panel);
        frame.setVisible(true);
    }

    private void authenticate() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (DEMO_USERNAME.equals(username)
                && DEMO_PASSWORD.equals(password)) {
            showProfile();
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Invalid username or password.",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showProfile() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        nameField = new JTextField("Student");
        JTextField emailField = new JTextField("student@example.com");

        panel.add(new JLabel("Full name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);

        int result = JOptionPane.showConfirmDialog(frame, panel,
                "Update Profile", JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        studentName = nameField.getText().trim();

        if (studentName.isEmpty() || emailField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "Name and email cannot be empty.");
            showProfile();
            return;
        }

        showExam();
    }

    private void showExam() {
        frame.getContentPane().removeAll();
        frame.setTitle("Online Examination - " + studentName);
        frame.setSize(750, 500);
        frame.setLayout(new BorderLayout(12, 12));

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 0, 15));
        header.add(new JLabel("Candidate: " + studentName), BorderLayout.WEST);
        timerLabel = new JLabel("Time remaining: 05:00");
        header.add(timerLabel, BorderLayout.EAST);
        frame.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(6, 1, 5, 5));
        center.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        questionLabel = new JLabel();
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        center.add(questionLabel);

        options = new JRadioButton[4];
        optionGroup = new ButtonGroup();

        for (int i = 0; i < options.length; i++) {
            options[i] = new JRadioButton();
            optionGroup.add(options[i]);
            center.add(options[i]);

            final int optionIndex = i;
            options[i].addActionListener(e ->
                    answers[currentQuestion] =
                            String.valueOf((char) ('A' + optionIndex)));
        }

        frame.add(center, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        previousButton = new JButton("Previous");
        nextButton = new JButton("Next");
        JButton submitButton = new JButton("Submit Exam");
        JButton quitButton = new JButton("Quit");

        previousButton.addActionListener(e -> navigate(-1));
        nextButton.addActionListener(e -> navigate(1));
        submitButton.addActionListener(e -> confirmSubmit());
        quitButton.addActionListener(e -> confirmQuit());

        footer.add(previousButton);
        footer.add(nextButton);
        footer.add(submitButton);
        footer.add(quitButton);
        frame.add(footer, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                confirmQuit();
            }
        });

        displayQuestion();
        frame.revalidate();
        frame.repaint();
        frame.setLocationRelativeTo(null);

        timer = new Timer(1000, e -> {
            secondsRemaining--;

            if (secondsRemaining <= 0) {
                timer.stop();
                submitExam();
            } else {
                updateTimer();
            }
        });

        timer.start();
    }

    private void displayQuestion() {
        String[] q = QUESTIONS[currentQuestion];
        questionLabel.setText("Question " + (currentQuestion + 1)
                + " of " + QUESTIONS.length + ": " + q[0]);

        for (int i = 0; i < 4; i++) {
            options[i].setText((char) ('A' + i) + ". " + q[i + 1]);
        }

        optionGroup.clearSelection();

        if (answers[currentQuestion] != null) {
            int selected = answers[currentQuestion].charAt(0) - 'A';
            options[selected].setSelected(true);
        }

        previousButton.setEnabled(currentQuestion > 0);
        nextButton.setEnabled(currentQuestion < QUESTIONS.length - 1);
    }

    private void navigate(int direction) {
        int target = currentQuestion + direction;

        if (target >= 0 && target < QUESTIONS.length) {
            currentQuestion = target;
            displayQuestion();
        }
    }

    private void updateTimer() {
        int minutes = secondsRemaining / 60;
        int seconds = secondsRemaining % 60;
        timerLabel.setText(String.format("Time remaining: %02d:%02d",
                minutes, seconds));
    }

    private void confirmSubmit() {
        int result = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to submit the exam?",
                "Confirm Submission", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            submitExam();
        }
    }

    private void confirmQuit() {
        int result = JOptionPane.showConfirmDialog(frame,
                "Quit the exam? Your exam will be submitted.",
                "Confirm Quit", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            submitExam();
        }
    }

    private void submitExam() {
        if (submitted) {
            return;
        }

        submitted = true;

        if (timer != null) {
            timer.stop();
        }

        int score = 0;
        List<String> review = new ArrayList<>();

        for (int i = 0; i < QUESTIONS.length; i++) {
            String correct = QUESTIONS[i][5];
            String selected = answers[i];

            if (correct.equals(selected)) {
                score++;
            }

            review.add("Q" + (i + 1) + ": Your answer = "
                    + (selected == null ? "Not answered" : selected)
                    + " | Correct answer = " + correct);
        }

        StringBuilder resultText = new StringBuilder();
        resultText.append("Candidate: ").append(studentName).append("\n");
        resultText.append("Score: ").append(score).append(" / ")
                .append(QUESTIONS.length).append("\n\n");

        for (String item : review) {
            resultText.append(item).append("\n");
        }

        JTextArea resultArea = new JTextArea(resultText.toString(), 18, 55);
        resultArea.setEditable(false);
        resultArea.setCaretPosition(0);

        JOptionPane.showMessageDialog(frame,
                new JScrollPane(resultArea),
                "Exam Results", JOptionPane.INFORMATION_MESSAGE);

        int logout = JOptionPane.showConfirmDialog(frame,
                "Exam completed. Logout now?",
                "Logout", JOptionPane.YES_NO_OPTION);

        if (logout == JOptionPane.YES_OPTION) {
            frame.dispose();
            showLogin();
        } else {
            frame.dispose();
        }
    }
}
