import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;

public class AdminDashboard extends JFrame {

    private JTabbedPane tabbedPane;
    private Connection conn;
    private TableRowSorter<DefaultTableModel> sorter;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        // Connect to the database
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/footballapp", "postgres", "olehz17");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection error!");
            System.exit(1);
        }

        // Add tabs
        addAddPlayerTab();
        addAddTeamTab();
        addDisplayPlayersTab();
        addDisplayTeamsTab();
        addSearchTab();
        addDeleteTab();

        add(tabbedPane);
        setVisible(true);
    }

    private void addAddPlayerTab() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));  // Increased rows for photo

        JTextField playerNameField = new JTextField();
        JTextField positionField = new JTextField();
        JTextField birthDateField = new JTextField();
        JButton addButton = new JButton("Add Player");
        JButton uploadPhotoButton = new JButton("Upload Photo");  // New button for photo

        JLabel playerPhotoLabel = new JLabel();  // Label to display photo
        JTextField playerIdField = new JTextField();  // Add this line
        panel.add(new JLabel("Player ID:"));           // Add this line
        panel.add(playerIdField);                      // Add this line
        panel.add(new JLabel("Player Name:"));
        panel.add(playerNameField);
        panel.add(new JLabel("Position:"));
        panel.add(positionField);
        panel.add(new JLabel("Birth Date (YYYY-MM-DD):"));
        panel.add(birthDateField);
        panel.add(uploadPhotoButton);
        panel.add(playerPhotoLabel);  // Add label for photo
        panel.add(new JLabel());  // Empty label for spacing
        panel.add(addButton);

        uploadPhotoButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    FileInputStream fis = new FileInputStream(selectedFile);
                    byte[] photoBytes = new byte[(int) selectedFile.length()];
                    fis.read(photoBytes);
                    fis.close();
                    playerPhotoLabel.setIcon(new ImageIcon(photoBytes));
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error reading photo!");
                }
            }
        });

        addButton.addActionListener(e -> {
            try {
                String query = "INSERT INTO players (player_id, player_name, position, birth_date, player_photo) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, Integer.parseInt(playerIdField.getText()));  // Insert player_id
                pstmt.setString(2, playerNameField.getText());
                pstmt.setString(3, positionField.getText());
                pstmt.setDate(4, java.sql.Date.valueOf(birthDateField.getText()));
                ImageIcon icon = (ImageIcon) playerPhotoLabel.getIcon();
                byte[] imageBytes = null;
                if (icon != null) {
                    Image image = icon.getImage(); // Використовуйте Image без приведення типу
                    BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = bufferedImage.createGraphics();
                    g2d.drawImage(image, 0, 0, null);
                    g2d.dispose();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    try {
                        ImageIO.write(bufferedImage, "png", baos);
                        baos.flush();
                        imageBytes = baos.toByteArray();
                        baos.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                pstmt.setBytes(5, imageBytes);

                pstmt.setBytes(5, imageBytes);

                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Player added successfully!");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding player!");
            }
        });

        tabbedPane.addTab("Add Player", panel);
    }


    private void addAddTeamTab() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));  // Increased rows for photo

        JTextField teamIdField = new JTextField();  // Add this line
        JTextField teamNameField = new JTextField();
        JTextField coachField = new JTextField();
        JTextField foundedYearField = new JTextField();
        JButton addButton = new JButton("Add Team");
        JButton uploadPhotoButton = new JButton("Upload Photo");  // New button for photo

        JLabel teamPhotoLabel = new JLabel();  // Label to display photo

        panel.add(new JLabel("Team ID:"));           // Add this line
        panel.add(teamIdField);                      // Add this line
        panel.add(new JLabel("Team Name:"));
        panel.add(teamNameField);
        panel.add(new JLabel("Coach:"));
        panel.add(coachField);
        panel.add(new JLabel("Founded Year:"));
        panel.add(foundedYearField);
        panel.add(uploadPhotoButton);
        panel.add(teamPhotoLabel);  // Add label for photo
        panel.add(new JLabel());  // Empty label for spacing
        panel.add(addButton);

        uploadPhotoButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    FileInputStream fis = new FileInputStream(selectedFile);
                    byte[] photoBytes = new byte[(int) selectedFile.length()];
                    fis.read(photoBytes);
                    fis.close();
                    teamPhotoLabel.setIcon(new ImageIcon(photoBytes));
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error reading photo!");
                }
            }
        });

        addButton.addActionListener(e -> {
            try {
                String query = "INSERT INTO teams (team_id, team_name, coach, founded_year, team_photo) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, Integer.parseInt(teamIdField.getText()));  // Insert team_id
                pstmt.setString(2, teamNameField.getText());
                pstmt.setString(3, coachField.getText());
                pstmt.setInt(4, Integer.parseInt(foundedYearField.getText()));
                ImageIcon icon = (ImageIcon) teamPhotoLabel.getIcon();
                byte[] imageBytes = null;
                if (icon != null) {
                    Image image = icon.getImage();
                    BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = bufferedImage.createGraphics();
                    g2d.drawImage(image, 0, 0, null);
                    g2d.dispose();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    try {
                        ImageIO.write(bufferedImage, "png", baos);
                        baos.flush();
                        imageBytes = baos.toByteArray();
                        baos.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                pstmt.setBytes(5, imageBytes);

                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Team added successfully!");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding team!");
            }
        });

        tabbedPane.addTab("Add Team", panel);
    }


    class ImageRenderer extends DefaultTableCellRenderer {
        JLabel lbl = new JLabel();

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof ImageIcon) {
                lbl.setIcon((ImageIcon) value);
                lbl.setHorizontalAlignment(JLabel.CENTER); // Центрування зображення
                lbl.setMaximumSize(new Dimension(50, 150)); // Максимальний розмір для JLabel
            }
            return lbl;
        }
    }
    private void addDisplayPlayersTab() {
        JPanel panel = new JPanel(new BorderLayout());

        // Components for displaying players
        String[] columnNames = {"ID", "Name", "Position", "Birth Date", "Photo"};  // Додайте "ID" на початок
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 4) { // 4 - індекс колонки "Photo"
                    return ImageIcon.class;
                }
                return Object.class;
            }
        };
        JTable playersTable = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        sorter = new TableRowSorter<>(model);
        playersTable.setRowSorter(sorter);

        playersTable.setDefaultRenderer(ImageIcon.class, new ImageRenderer());  // Ваш ImageRenderer
        playersTable.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(playersTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        playersTable.setRowHeight(50);
        JComboBox<String> sortComboBox = new JComboBox<>(new String[]{"ID Asc", "ID Desc", "Name Asc", "Name Desc"});
        panel.add(sortComboBox, BorderLayout.NORTH);
        JButton refreshButton = new JButton("Refresh");
        panel.add(refreshButton, BorderLayout.SOUTH);
        sortComboBox.addActionListener(e -> {
            String selectedSort = (String) sortComboBox.getSelectedItem();
            if ("ID Asc".equals(selectedSort)) {
                sortPlayersByIdAsc();
            } else if ("ID Desc".equals(selectedSort)) {
                sortPlayersByIdDesc();
            } else if ("Name Asc".equals(selectedSort)) {
                sortPlayersByNameAsc();
            } else if ("Name Desc".equals(selectedSort)) {
                sortPlayersByNameDesc();
            }
        });

        refreshButton.addActionListener(e -> displayPlayers(playersTable));

        tabbedPane.addTab("Display Players", panel);
    }
    private void sortPlayersByIdAsc() {
        sorter.setRowFilter(RowFilter.regexFilter(".*"));
        sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(0, SortOrder.ASCENDING)));
    }

    private void sortPlayersByIdDesc() {
        sorter.setRowFilter(RowFilter.regexFilter(".*"));
        sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(0, SortOrder.DESCENDING)));
    }

    private void sortPlayersByNameAsc() {
        sorter.setRowFilter(RowFilter.regexFilter(".*"));
        sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(1, SortOrder.ASCENDING)));
    }

    private void sortPlayersByNameDesc() {
        sorter.setRowFilter(RowFilter.regexFilter(".*"));
        sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(1, SortOrder.DESCENDING)));
    }

    private void displayPlayers(JTable playersTable) {
        try {
            String query = "SELECT player_id, player_name, position, birth_date, player_photo FROM players";
            PreparedStatement pstmt = conn.prepareStatement(query);
            java.sql.ResultSet rs = pstmt.executeQuery();

            DefaultTableModel model = (DefaultTableModel) playersTable.getModel();
            model.setRowCount(0); // Clear existing rows

            while (rs.next()) {
                int playerId = rs.getInt("player_id");
                String playerName = rs.getString("player_name");
                String position = rs.getString("position");
                Date birthDate = rs.getDate("birth_date");

                byte[] photoBytes = rs.getBytes("player_photo");
                ImageIcon imageIcon = null;
                if (photoBytes != null && photoBytes.length > 0) {
                    Image image = new ImageIcon(photoBytes).getImage();
                    Image newImage = image.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                    imageIcon = new ImageIcon(newImage);
                }

                model.addRow(new Object[]{playerId, playerName, position, birthDate, imageIcon});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error displaying players!");
        }
    }
    private void addDisplayTeamsTab() {
        JPanel panel = new JPanel(new BorderLayout());

        // Components for displaying teams
        String[] columnNames = {"Team ID", "Name", "Coach", "Founded Year", "Photo"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 4) { // 4 - індекс колонки "Photo"
                    return ImageIcon.class;
                }
                return Object.class;
            }
        };
        JTable teamsTable = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        sorter = new TableRowSorter<>(model);
        teamsTable.setRowSorter(sorter);

        teamsTable.setDefaultRenderer(ImageIcon.class, new ImageRenderer());  // Ваш ImageRenderer
        teamsTable.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(teamsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        teamsTable.setRowHeight(50);
        JComboBox<String> sortComboBox = new JComboBox<>(new String[]{"ID Asc", "ID Desc", "Name Asc", "Name Desc"});
        panel.add(sortComboBox, BorderLayout.NORTH);
        JButton refreshButton = new JButton("Refresh");
        panel.add(refreshButton, BorderLayout.SOUTH);
        sortComboBox.addActionListener(e -> {
            String selectedSort = (String) sortComboBox.getSelectedItem();
            if ("ID Asc".equals(selectedSort)) {
                sortPlayersByIdAsc();
            } else if ("ID Desc".equals(selectedSort)) {
                sortPlayersByIdDesc();
            } else if ("Name Asc".equals(selectedSort)) {
                sortPlayersByNameAsc();
            } else if ("Name Desc".equals(selectedSort)) {
                sortPlayersByNameDesc();
            }
        });

        refreshButton.addActionListener(e -> displayTeams(teamsTable));

        tabbedPane.addTab("Display Teams", panel);
    }


    private void displayTeams(JTable teamsTable) {
        try {
            String query = "SELECT team_id, team_name, coach, founded_year, team_photo FROM teams";
            PreparedStatement pstmt = conn.prepareStatement(query);
            java.sql.ResultSet rs = pstmt.executeQuery();

            DefaultTableModel model = (DefaultTableModel) teamsTable.getModel();
            model.setRowCount(0); // Clear existing rows

            while (rs.next()) {
                int teamId = rs.getInt("team_id");
                String teamName = rs.getString("team_name");
                String coach = rs.getString("coach");
                int foundedYear = rs.getInt("founded_year");

                byte[] photoBytes = rs.getBytes("team_photo");
                ImageIcon imageIcon = null;
                if (photoBytes != null && photoBytes.length > 0) {
                    Image image = new ImageIcon(photoBytes).getImage();
                    Image newImage = image.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                    imageIcon = new ImageIcon(newImage);
                }

                model.addRow(new Object[]{teamId, teamName, coach, foundedYear, imageIcon});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error displaying teams!");
        }
    }
    private void addSearchTab() {
        JPanel panel = new JPanel(new BorderLayout());

        // Search components
        JComboBox<String> searchTypeComboBox = new JComboBox<>(new String[]{"Player", "Team"});
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search for: "));
        searchPanel.add(searchTypeComboBox);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        panel.add(searchPanel, BorderLayout.NORTH);

        // Display search results
        String[] columnNames = {"ID", "Name", "Position/Coach", "Birth Year/Founded Year", "Photo"};
        DefaultTableModel searchTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 4) {
                    return ImageIcon.class;
                }
                return Object.class;
            }
        };
        JTable searchTable = new JTable(searchTableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        searchTable.setDefaultRenderer(ImageIcon.class, new ImageRenderer());
        JScrollPane scrollPane = new JScrollPane(searchTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText();
            String searchType = (String) searchTypeComboBox.getSelectedItem();
            if ("Player".equals(searchType)) {
                displaySearchResultsForPlayer(searchTerm, searchTableModel, searchTable);
            } else if ("Team".equals(searchType)) {
                displaySearchResultsForTeam(searchTerm, searchTableModel, searchTable);
            }
        });


        tabbedPane.addTab("Search", panel);
    }

    private void displaySearchResultsForPlayer(String searchTerm, DefaultTableModel model, JTable searchTable) {
        model.setRowCount(0); // Очищення моделі перед новим пошуком
        searchTable.setRowHeight(50); // Встановлення висоти рядків

        try {
            String query = "SELECT player_id, player_name, position, birth_date, player_photo FROM players WHERE player_name LIKE ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int playerId = rs.getInt("player_id");
                String playerName = rs.getString("player_name");
                String position = rs.getString("position");
                Date birthDate = rs.getDate("birth_date");
                byte[] photoBytes = rs.getBytes("player_photo");

                ImageIcon imageIcon = null;
                if (photoBytes != null && photoBytes.length > 0) {
                    Image image = new ImageIcon(photoBytes).getImage();
                    Image newImage = image.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                    imageIcon = new ImageIcon(newImage);
                }

                model.addRow(new Object[]{playerId, playerName, position, birthDate, imageIcon});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching for players!");
        }
    }

    private void displaySearchResultsForTeam(String searchTerm, DefaultTableModel model, JTable searchTable) {
        model.setRowCount(0); // Очищення моделі перед новим пошуком
        searchTable.setRowHeight(50); // Встановлення висоти рядків

        try {
            String query = "SELECT team_id, team_name, coach, founded_year, team_photo FROM teams WHERE team_name LIKE ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int teamId = rs.getInt("team_id");
                String teamName = rs.getString("team_name");
                String coach = rs.getString("coach");
                int foundedYear = rs.getInt("founded_year");
                byte[] photoBytes = rs.getBytes("team_photo");

                ImageIcon imageIcon = null;
                if (photoBytes != null && photoBytes.length > 0) {
                    Image image = new ImageIcon(photoBytes).getImage();
                    Image newImage = image.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                    imageIcon = new ImageIcon(newImage);
                }

                model.addRow(new Object[]{teamId, teamName, coach, foundedYear, imageIcon});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching for teams!");
        }
    }
    private void addDeleteTab() {
        JPanel panel = new JPanel(new BorderLayout());

        // Delete components
        JComboBox<String> deleteTypeComboBox = new JComboBox<>(new String[]{"Player", "Team"});
        JTextField deleteIdField = new JTextField(20);
        JButton deleteButton = new JButton("Delete");

        JPanel deletePanel = new JPanel();
        deletePanel.add(new JLabel("Delete type: "));
        deletePanel.add(deleteTypeComboBox);
        deletePanel.add(new JLabel("ID: "));
        deletePanel.add(deleteIdField);
        deletePanel.add(deleteButton);

        panel.add(deletePanel, BorderLayout.NORTH);

        deleteButton.addActionListener(e -> {
            String deleteType = (String) deleteTypeComboBox.getSelectedItem();
            String deleteId = deleteIdField.getText();
            if ("Player".equals(deleteType)) {
                deletePlayerById(deleteId);
            } else if ("Team".equals(deleteType)) {
                deleteTeamById(deleteId);
            }
        });

        tabbedPane.addTab("Delete", panel);
    }

    private void deletePlayerById(String playerId) {
        try {
            String query = "DELETE FROM players WHERE player_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(playerId));
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Player deleted successfully!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting player!");
        }
    }

    private void deleteTeamById(String teamId) {
        try {
            String query = "DELETE FROM teams WHERE team_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(teamId));
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Team deleted successfully!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting team!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AdminDashboard();
        });
    }
}


