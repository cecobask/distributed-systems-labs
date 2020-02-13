import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.ParseException;
import java.util.ArrayList;

public class FrameController implements ActionListener {

    private JTextField ssnTextField;
    private JTextField dobTextField;
    private JTextField nameTextField;
    private JTextField addressTextField;
    private JTextField salaryTextField;
    private JRadioButton maleRadioButton;
    private JRadioButton femaleRadioButton;
    private ButtonGroup genderGroup;
    private JButton previousButton;
    private JButton clearButton;
    private JButton nextButton;
    private JButton deleteButton;
    private JButton addButton;
    private JButton updateButton;
    private Employee employee = new Employee("", new Date(0), "", "", 0, "");
    private ArrayList<Employee> employees = new ArrayList<>();
    private EmployeeService employeeService = new EmployeeService();
    private boolean tableIsEmpty;
    private int currentIndex = 0;

    public FrameController(Frame frame) {
        initComponents(frame);
        addActionListeners( // Add click listeners to the buttons.
                new JButton[]{previousButton, clearButton, nextButton, deleteButton, addButton, updateButton}
        );
        loadData();
        displayData();
    }

    private void initComponents(Frame frame) {
        ssnTextField = frame.getSsnTextField();
        dobTextField = frame.getDobTextField();
        nameTextField = frame.getNameTextField();
        addressTextField = frame.getAddressTextField();
        salaryTextField = frame.getSalaryTextField();
        maleRadioButton = frame.getMaleRadioButton();
        femaleRadioButton = frame.getFemaleRadioButton();
        genderGroup = frame.getGenderGroup();
        previousButton = frame.getPreviousButton();
        clearButton = frame.getClearButton();
        nextButton = frame.getNextButton();
        deleteButton = frame.getDeleteButton();
        addButton = frame.getAddButton();
        updateButton = frame.getUpdateButton();
    }

    private void addActionListeners(JButton[] buttons) {
        for (JButton button : buttons) {
            button.addActionListener(this);
        }
    }

    private void loadData() {
        employees = employeeService.getAllEmployees();

        if (employees.isEmpty()) {
            disableButtons(new JButton[]{deleteButton, previousButton, nextButton, updateButton});
            clearButton.doClick(); // Clear input boxes.
            showMessageDialog("Table 'data' is empty!");
            tableIsEmpty = true;
            return;
        }

        employee = employees.get(0);

        if (employees.size() <= 1) {
            nextButton.setEnabled(false);
        } else {
            nextButton.setEnabled(true);
        }

        previousButton.setEnabled(false);
        deleteButton.setEnabled(true);
        tableIsEmpty = false;
    }

    private void displayData() {
        ssnTextField.setText(employee.getSsn());
        dobTextField.setText(employee.getDob().equals(new Date(0)) ? "" : employee.getFormattedDate());
        nameTextField.setText(employee.getName());
        addressTextField.setText(employee.getAddress().equals("NOT_PROVIDED") ? "" : employee.getAddress());
        salaryTextField.setText(employee.getSalary() == 0 ? "" : String.valueOf(employee.getSalary()));
        femaleRadioButton.setSelected(employee.getGender().equals("F"));
        maleRadioButton.setSelected(employee.getGender().equals("M"));
    }

    private void addEmployee(Employee employee) {
        try {
            employeeService.addEmployee(employee);
            loadData();
            displayData();
        } catch (SQLException e) {
            showMessageDialog("Employee with SSN '" + employee.getSsn() + "' already exists.");
        }
    }

    private void clearInputs() {
        ssnTextField.setText("");
        dobTextField.setText("");
        nameTextField.setText("");
        addressTextField.setText("");
        salaryTextField.setText("");
        genderGroup.clearSelection();
    }

    private void disableButtons(JButton[] buttons) {
        for (JButton button : buttons) {
            button.setEnabled(false);
        }
    }

    private void showMessageDialog(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == previousButton) {
            try {
                currentIndex--;
                if (currentIndex == 0) {
                    previousButton.setEnabled(false);
                    nextButton.setEnabled(true);
                    employee = employees.get(currentIndex);
                    displayData();
                    return;
                }
                employee = employees.get(currentIndex);
                displayData();
            } catch (IndexOutOfBoundsException ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == clearButton) {
            clearInputs();
        } else if (e.getSource() == nextButton) {
            try {
                currentIndex++;
                if (currentIndex >= employees.size()-1) {
                    nextButton.setEnabled(false);
                    previousButton.setEnabled(true);
                    employee = employees.get(currentIndex);
                    displayData();
                    return;
                }
                employee = employees.get(currentIndex);
                displayData();
            } catch (IndexOutOfBoundsException ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == deleteButton) {
            System.out.println("Delete button pressed.");
        } else if (e.getSource() == addButton) {
            Employee emp = new Employee();
            try {
                emp.setSsn(ssnTextField.getText().trim());
                emp.setDob(employee.parseDobString(dobTextField.getText().trim()));
                emp.setName(nameTextField.getText().trim());
                emp.setAddress(addressTextField.getText().trim());
                emp.setSalary(Integer.parseInt(salaryTextField.getText()));
                emp.setGender(genderGroup.getSelection().getActionCommand());
            } catch (NumberFormatException ex) {
                showMessageDialog("Problem with field 'SALARY'. Please provide integer value.");
                return;
            } catch (ParseException ex) {
                showMessageDialog("'DOB' has to be a valid date in format: dd/mm/yyyy");
                return;
            }

            addEmployee(emp);

//            showMessageDialog("Inserted record with SSN: " + emp.getSsn());
//            loadData();
//            displayData();
        } else if (e.getSource() == updateButton) {
            System.out.println("Update button pressed.");
        }
    }
}