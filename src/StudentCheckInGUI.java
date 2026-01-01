import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class StudentCheckInGUI {

    int step = 0;

    // Answers
    String collegeExperience, codingLevel, projectAnswer, projectDescription,
            githubLink, internshipAnswer, internshipCompany, internshipYear, stressLevel;

    JFrame frame;
    JPanel card;
    JLabel title;
    JTextArea input;
    JButton next;
    JRadioButton yesBtn, noBtn;
    ButtonGroup yesNoGroup;

    Connection conn;

    public StudentCheckInGUI() {
        connectDB();
        createTable();

        frame = new JFrame("Student Check-in");
        frame.setSize(900, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        frame.getContentPane().setBackground(new Color(240, 243, 248));

        card = new JPanel();
        card.setPreferredSize(new Dimension(600, 380));
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        title = new JLabel("Third year can get overwhelming.");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        input = new JTextArea(4, 40);
        input.setFont(new Font("SansSerif", Font.PLAIN, 14));
        input.setLineWrap(true);
        input.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(input);

        yesBtn = new JRadioButton("Yes");
        noBtn = new JRadioButton("No");

        yesNoGroup = new ButtonGroup();
        yesNoGroup.add(yesBtn);
        yesNoGroup.add(noBtn);

        JPanel radioPanel = new JPanel();
        radioPanel.add(yesBtn);
        radioPanel.add(noBtn);
        radioPanel.setVisible(false);

        next = new JButton("Next");
        next.setAlignmentX(Component.CENTER_ALIGNMENT);
        next.addActionListener(e -> nextStep(radioPanel));

        card.add(title);
        card.add(Box.createVerticalStrut(20));
        card.add(scroll);
        card.add(Box.createVerticalStrut(15));
        card.add(radioPanel);
        card.add(Box.createVerticalStrut(25));
        card.add(next);

        frame.add(card);
        frame.setVisible(true);
    }

    void connectDB() {
        try {
            Class.forName("org.sqlite.JDBC"); // 🔥 REQUIRED
            conn = DriverManager.getConnection("jdbc:sqlite:student_checkin.db");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS responses (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                college_experience TEXT,
                coding_level TEXT,
                project_status TEXT,
                project_description TEXT,
                github_link TEXT,
                internship_status TEXT,
                internship_company TEXT,
                internship_year TEXT,
                stress_level TEXT
            );
        """;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void nextStep(JPanel radioPanel) {

        if (step == 0) {
            collegeExperience = input.getText();
            title.setText("How would you describe your coding journey so far?");
            input.setText("");
            step++;
        }

        else if (step == 1) {
            codingLevel = input.getText();
            title.setText("Have you worked on any projects?");
            input.setText("");
            radioPanel.setVisible(true);
            step++;
        }

        else if (step == 2) {
            projectAnswer = yesBtn.isSelected() ? "Yes" : "No";
            radioPanel.setVisible(false);
            yesNoGroup.clearSelection();

            if (projectAnswer.equals("Yes")) {
                title.setText("Briefly describe your project:");
                step = 3;
            } else {
                title.setText("Have you done any internship?");
                radioPanel.setVisible(true);
                step = 5;
            }
            input.setText("");
        }

        else if (step == 3) {
            projectDescription = input.getText();
            title.setText("GitHub link (optional):");
            input.setText("");
            step++;
        }

        else if (step == 4) {
            githubLink = input.getText();
            title.setText("Have you done any internship?");
            input.setText("");
            radioPanel.setVisible(true);
            step++;
        }

        else if (step == 5) {
            internshipAnswer = yesBtn.isSelected() ? "Yes" : "No";
            radioPanel.setVisible(false);
            yesNoGroup.clearSelection();

            if (internshipAnswer.equals("Yes")) {
                title.setText("Which company?");
                step = 6;
            } else {
                title.setText("How stressful do you find engineering & the job market?");
                step = 8;
            }
            input.setText("");
        }

        else if (step == 6) {
            internshipCompany = input.getText();
            title.setText("In which year did you do the internship?");
            input.setText("");
            step++;
        }

        else if (step == 7) {
            internshipYear = input.getText();
            title.setText("How stressful do you find engineering & the job market?");
            input.setText("");
            step++;
        }

        else {
            stressLevel = input.getText();
            saveToDB();
            JOptionPane.showMessageDialog(frame, "Response saved successfully 💙");
            System.exit(0);
        }
    }

    void saveToDB() {
        String sql = """
        INSERT INTO responses
        (college_experience, coding_level, project_status,
         project_description, github_link,
         internship_status, internship_company, internship_year,
         stress_level)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, collegeExperience);
            ps.setString(2, codingLevel);
            ps.setString(3, projectAnswer);

            // SAFETY: set empty strings if null
            ps.setString(4, projectDescription == null ? "" : projectDescription);
            ps.setString(5, githubLink == null ? "" : githubLink);

            ps.setString(6, internshipAnswer);
            ps.setString(7, internshipCompany == null ? "" : internshipCompany);
            ps.setString(8, internshipYear == null ? "" : internshipYear);

            ps.setString(9, stressLevel);

            ps.executeUpdate();

            System.out.println("Saved to DB successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        new StudentCheckInGUI();
    }
}


