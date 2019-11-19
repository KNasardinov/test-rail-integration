package com.knasardinov.idea.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.knasardinov.idea.forms.Login;
import com.knasardinov.idea.forms.TestRailSyncForm;
import com.knasardinov.idea.utils.TestRailHelper;
import java.util.Optional;

public class MainAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        if(!userAuthorized()){
            openLoginForm(e);
        } else {
            openTestRailSyncForm(e);
        }
    }

    private boolean userAuthorized(){
        Optional<String> username = TestRailHelper.getUsername();
        Optional<String> password = TestRailHelper.getPassword();
        Optional<String> endpoint = TestRailHelper.getEndpoint();
        if(username == null || endpoint == null){
            return false;
        }
        return (new TestRailHelper()
                .validateTestRailCredentials(username.get(), password.get(), endpoint.get())) ? true : false;
    }

    private void openLoginForm(AnActionEvent e){
        final Login loginForm = new Login(e);
        loginForm.setVisible(true);
    }

    private void openTestRailSyncForm(AnActionEvent e){
        final TestRailSyncForm testRailSyncForm = new TestRailSyncForm(e);
        testRailSyncForm.setVisible(true);
    }
}