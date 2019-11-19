package com.knasardinov.idea.forms;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.knasardinov.idea.utils.TestRailHelper;
import lombok.extern.log4j.Log4j;
import javax.swing.*;
import java.awt.*;

@Log4j
public class Login extends JDialog{

    private static final int MINIMAL_LENGTH = 3;
    private JPanel rootPanel;
    private JButton loginButton;
    private JFormattedTextField loginInputField;
    private JPasswordField passwordInputField;
    private JPanel loginInputsPanel;
    private JLabel loginLabel;
    private JLabel passwordLabel;
    private JLabel messageField;
    private JPanel messagePanel;
    private JFormattedTextField endpointInputField;

    public Login(AnActionEvent event){
        setTitle("Enter your TestRail credentials");
        setSize(700, 100);
        add(rootPanel);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dimension.width/2 - getSize().width/2, dimension.height/2 - getSize().height/2);
        loginButton.addActionListener(e -> loginButtonListener(event));
    }

    public void loginButtonListener(AnActionEvent event){
        messageField.setText(" ");
        String login = loginInputField.getText();
        String password = String.valueOf(passwordInputField.getPassword());
        String endpoint = endpointInputField.getText();
        if(login.length() < MINIMAL_LENGTH){
            messageField.setText("Invalid Login");
        } else if(password.length() < MINIMAL_LENGTH){
            messageField.setText("Invalid Password");
        } else if(endpoint.length() < 11 && !endpoint.startsWith("https://")){
            messageField.setText("Invalid endpoint");
        } else if(new TestRailHelper().validateTestRailCredentials(login, password, endpoint)){
            this.dispose();
            new TestRailSyncForm(event).setVisible(true);
        } else {
            messageField.setText("Credentials you entered is invalid");
        }
    }
}