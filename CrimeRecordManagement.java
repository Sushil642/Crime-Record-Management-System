import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

class CrimeRecord {
  
    private int serialNumber;
    private String crimeName;
    private String suspectName;
    private String date;

    public CrimeRecord(int serialNumber, String crimeName, String suspectName, String date) {
        this.serialNumber = serialNumber;
        this.crimeName = crimeName;
        this.suspectName = suspectName;
        this.date = date;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public String getCrimeName() {
        return crimeName;
    }

    public String getSuspectName() {
        return suspectName;
    }

    public String getDate() {
        return date;
    }

}

class CrimeRecordManagementSystem{
 private ArrayList<CrimeRecord> records;
    private int nextSerialNumber;
    private String filePath;

    public CrimeRecordManagementSystem(String filePath) {
        records = new ArrayList<>();
        nextSerialNumber = 1;
        this.filePath = filePath;
        loadRecords();
    }
    
    public boolean login(String username, String password) {
        return username.equals("admin") && password.equals("Police");
    }
     private void loadRecords() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int serialNumber = Integer.parseInt(parts[0]);
                String crimeName = parts[1];
                String suspectName = parts[2];
                String date = parts[3];
                CrimeRecord record = new CrimeRecord(serialNumber, crimeName, suspectName, date);
                records.add(record);
                nextSerialNumber = serialNumber + 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void saveRecords() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (CrimeRecord record : records) {
                writer.write(record.getSerialNumber() + "," + record.getCrimeName() + "," +
                        record.getSuspectName() + "," + record.getDate());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addRecord(String crimeName, String suspectName, String date) {
        CrimeRecord record = new CrimeRecord(nextSerialNumber, crimeName, suspectName, date);
        records.add(record);
        nextSerialNumber++;
        saveRecords();
    }

    public void deleteRecord(int serialNumber) {
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getSerialNumber() == serialNumber) {
                records.remove(i);
                saveRecords();
                break;
            }
        }
    }

    public void modifyRecord(int serialNumber, String crimeName, String suspectName, String date) {
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getSerialNumber() == serialNumber) {
                CrimeRecord modifiedRecord = new CrimeRecord(serialNumber, crimeName, suspectName, date);
                records.set(i, modifiedRecord);
                saveRecords();
                break;
            }
        }
    }

    public ArrayList<CrimeRecord> getRecords() {
        return records;
    }

}

class CrimeRecordManagementGUI extends JFrame {
    private CrimeRecordManagementSystem system;
    private JTextArea displayArea;

    public CrimeRecordManagementGUI(CrimeRecordManagementSystem system) {
        this.system = system;

        if (!showLoginDialog()) {
            System.exit(0);
        }

        setTitle("Crime Record Management System");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton addButton = new JButton("Add Record");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAddRecordDialog();
            }
        });
        buttonPanel.add(addButton);

        JButton deleteButton = new JButton("Delete Record");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showDeleteRecordDialog();
            }
        });
        buttonPanel.add(deleteButton);

        JButton modifyButton = new JButton("Modify Record");
        modifyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showModifyRecordDialog();
            }
        });

        buttonPanel.add(modifyButton);
        JButton SearchButton = new JButton("Search");
        modifyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showModifyRecordDialog();
            }
        });
        buttonPanel.add(SearchButton);

        panel.add(buttonPanel, BorderLayout.CENTER);

        setContentPane(panel);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                saveAndExit();
            }
        });
    }

    private boolean showLoginDialog() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        Object[] message = {
                "Username:", usernameField,
                "Password:", passwordField
        };
        int option = JOptionPane.showOptionDialog(this, message, "Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            return system.login(username, password);
        }
        return false;
    }

        private void showAddRecordDialog() {
        JDialog dialog = new JDialog(this, "Add Record", true);
        dialog.setSize(300, 200);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));

        JLabel crimeNameLabel = new JLabel("Crime Name:");
        JTextField crimeNameField = new JTextField();
        panel.add(crimeNameLabel);
        panel.add(crimeNameField);

        JLabel suspectNameLabel = new JLabel("Suspect Name:");
        JTextField suspectNameField = new JTextField();
        panel.add(suspectNameLabel);
        panel.add(suspectNameField);

        JLabel dateLabel = new JLabel("Date:");
        JTextField dateField = new JTextField();
        panel.add(dateLabel);
        panel.add(dateField);

        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String crimeName = crimeNameField.getText();
                String suspectName = suspectNameField.getText();
                String date = dateField.getText();

                system.addRecord(crimeName, suspectName, date);
                updateDisplayArea();
                dialog.dispose();
            }
        });
        panel.add(addButton);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void showDeleteRecordDialog() {
        String input = JOptionPane.showInputDialog(this, "Enter the serial number of the record to delete:");
        if (input != null && !input.isEmpty()) {
            try {
                int serialNumber = Integer.parseInt(input);
                system.deleteRecord(serialNumber);
                updateDisplayArea();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid serial number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showModifyRecordDialog() {
        String input = JOptionPane.showInputDialog(this, "Enter the serial number of the record to modify:");
        if (input != null && !input.isEmpty()) {
            try {
                int serialNumber = Integer.parseInt(input);
                CrimeRecord record = findRecordBySerialNumber(serialNumber);
                if (record != null) {
                    showEditRecordDialog(record);
                } else {
                    JOptionPane.showMessageDialog(this, "Record not found. Please enter a valid serial number.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid serial number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditRecordDialog(CrimeRecord record) {
        JDialog dialog = new
        		JDialog(this, "Modify Record", true);
        dialog.setSize(300, 200);    JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));

        JLabel serialNumberLabel = new JLabel("Serial Number:");
        JTextField serialNumberField = new JTextField(Integer.toString(record.getSerialNumber()));
        serialNumberField.setEditable(false);
        panel.add(serialNumberLabel);
        panel.add(serialNumberField);

        JLabel crimeNameLabel = new JLabel("Crime Name:");
        JTextField crimeNameField = new JTextField(record.getCrimeName());
        panel.add(crimeNameLabel);
        panel.add(crimeNameField);

        JLabel suspectNameLabel = new JLabel("Suspect Name:");
        JTextField suspectNameField = new JTextField(record.getSuspectName());
        panel.add(suspectNameLabel);
        panel.add(suspectNameField);

        JLabel dateLabel = new JLabel("Date:");
        JTextField dateField = new JTextField(record.getDate());
        panel.add(dateLabel);
        panel.add(dateField);

        JButton modifyButton = new JButton("Modify");
        modifyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String crimeName = crimeNameField.getText();
                String suspectName = suspectNameField.getText();
                String date = dateField.getText();

                system.modifyRecord(record.getSerialNumber(), crimeName, suspectName, date);
                updateDisplayArea();
                dialog.dispose();
            }
        });
        panel.add(modifyButton);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private CrimeRecord findRecordBySerialNumber(int serialNumber) {
        ArrayList<CrimeRecord> records = system.getRecords();
        for (CrimeRecord record : records) {
            if (record.getSerialNumber() == serialNumber) {
                return record;
            }
        }
        return null;
    }

    public void updateDisplayArea() {
        ArrayList<CrimeRecord> records = system.getRecords();
        displayArea.setText("");

        for (CrimeRecord record : records) {
            displayArea.append("Serial Number: " + record.getSerialNumber() + ", Crime: " + record.getCrimeName() +
                    ", Suspect: " + record.getSuspectName() + ", Date: " + record.getDate() + "\n");
        }
    }

    private void saveAndExit() {
        system.saveRecords();
        System.exit(0);
    }
}

public class CrimeRecordManagement{
    public static void main(String[] args) {
        String filePath = "crime_records.txt";
        CrimeRecordManagementSystem system = new CrimeRecordManagementSystem(filePath);
        CrimeRecordManagementGUI gui = new CrimeRecordManagementGUI(system);
        gui.setVisible(true);
    }
}
