import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class form {
    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextArea contactsArea;
    private JTextField nameField, phoneField, emailField, addressField;
    private JButton addButton, updateButton, deleteButton, showButton;

    public form() {
        frame = new JFrame("Phonebook Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 750);

      tableModel = new DefaultTableModel(new String[]{"Name", "Phone", "Email", "Address"}, 0);
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();

            if (row >= 0 && column >= 0) {
                String newValue = tableModel.getValueAt(row, column).toString();
                String columnName = tableModel.getColumnName(column);
                System.out.println("Updating: " + columnName + " for Name: " + tableModel.getValueAt(row, 0) + " with new value: " + newValue);

                String name = tableModel.getValueAt(row, 0).toString();
                String phone = tableModel.getValueAt(row, 1).toString();
                String email = tableModel.getValueAt(row, 2).toString();
                String address = tableModel.getValueAt(row, 3).toString();

                if (column == 0) name = newValue;
                else if (column == 1) phone = newValue;
                else if (column == 2) email = newValue;
                else if (column == 3) address = newValue;

                Connect.updateContact(name, phone, email, address);
            }
        });
        nameField = new JTextField(20);
        phoneField = new JTextField(20);
        emailField = new JTextField(20);
        addressField = new JTextField(20);

      addButton = new JButton("Add Contact");
        updateButton = new JButton("Update Contact");
        deleteButton = new JButton("Delete Contact");
        showButton = new JButton("Show Contacts");
        
      addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            String address = addressField.getText().trim();

            if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "⚠️ All fields must be filled!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                JOptionPane.showMessageDialog(frame, "⚠️ Invalid email format! Must contain '@' and a valid domain.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!phone.matches("^[0-9]{6,}$")) {
                JOptionPane.showMessageDialog(frame, "⚠️ Invalid phone number! It must be at least 6 digits and contain only numbers.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Connect.addContact(name, phone, email, address);
            refreshTable();
            clearFields();
        });
        updateButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String name = tableModel.getValueAt(row, 0).toString();
                String phone = phoneField.getText().trim();
                String email = emailField.getText().trim();
                String address = addressField.getText().trim();

                if (!name.isEmpty() && !phone.isEmpty()) {
                    Connect.updateContact(name, phone, email, address);
                    refreshTable();
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(frame, "Select a contact and enter new details!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a contact to update!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String name = tableModel.getValueAt(row, 0).toString();
                Connect.deleteContact(name);
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a contact to delete!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        showButton.addActionListener(e -> refreshTable());

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Phone:"));
        inputPanel.add(phoneField);
        inputPanel.add(new JLabel("Email:"));
        inputPanel.add(emailField);
        inputPanel.add(new JLabel("Address:"));
        inputPanel.add(addressField);
        inputPanel.add(addButton);
        inputPanel.add(updateButton);
        inputPanel.add(deleteButton);
        inputPanel.add(showButton);

        frame.add(new JScrollPane(table), BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);

        refreshTable();
        frame.setVisible(true);
    }
    private void refreshTable() {
        tableModel.setRowCount(0); // Clear table before adding data
        try {
            ResultSet rs = Connect.getContacts();

            if (!rs.isBeforeFirst()) { // ✅ Check if database has no contacts
                contactsArea.setText("⚠️ No contacts found in the database.");
                return;
            }

            StringBuilder contactText = new StringBuilder();
            while (rs.next()) {
                String name = rs.getString("Name");
                String phone = rs.getString("Phone");
                String email = rs.getString("Email");
                String address = rs.getString("Address");

                tableModel.addRow(new Object[]{name, phone, email, address});
                contactText.append("Name: ").append(name)
                        .append(", Phone: ").append(phone)
                        .append(", Email: ").append(email)
                        .append(", Address: ").append(address)
                        .append("\n");
            }
            contactsArea.setText(contactText.toString()); // ✅ Display contacts
            System.out.println("✅ Contacts displayed successfully.");
        } catch (SQLException e) {
            contactsArea.setText("❌ Error: Could not retrieve contacts.");
            e.printStackTrace();
        }
    }

    privatevoid clearFields() {
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressField.setText("");
    }
}
