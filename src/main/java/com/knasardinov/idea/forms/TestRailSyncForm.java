package com.knasardinov.idea.forms;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import com.knasardinov.idea.api.TestRailClient;
import com.knasardinov.idea.api.TestRailClientBuilder;
import com.knasardinov.idea.api.TestRailException;
import com.knasardinov.idea.pojo.TestCase;
import com.knasardinov.idea.utils.TestRailHelper;
import lombok.extern.log4j.Log4j;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j
public class TestRailSyncForm extends JDialog {
    private JPanel rootPanel;
    private JList testCaseList;
    private JButton analyzeButton;
    private JButton updateButton;
    private JTable testCasesPerModuleTable;
    private JLabel testSectionLabel;
    private JLabel updateAutomationStatusLabel;
    private JLabel analyzeProjectForTestsLabel;
    private JScrollPane testCasesPerModuleScroll;
    private JScrollPane testCaseListScroll;
    private TestRailClient testRail;
    private static final int TEST_RAIL_AUTOMATION_STATUS_DONE = 3;
    private List<String> updatedTests;
    private List<String> notUpdatedTests;
    private static final String TEST_CASE_REGEXP = "^[^0-9](\\d{5})(.*)";
    private static final String JAVA_FILE_EXTENSION = ".java";

    public TestRailSyncForm(AnActionEvent event) {
        setTitle("TestRail integration");
        setSize(700, 500);
        add(rootPanel);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dimension.width / 2 - getSize().width / 2, dimension.height / 2 - getSize().height / 2);
        initializeTestRailClient();
        analyzeButton.addActionListener(e -> analyzeButtonListener(event));
        updateButton.addActionListener(e -> updateButtonListener(event));
    }

    public void analyzeButtonListener(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PSI_FILE).getProject();
        List<VirtualFile> testFiles = FileBasedIndex.getInstance()
                        .getContainingFiles(
                                FileTypeIndex.NAME,
                                JavaFileType.INSTANCE,
                                GlobalSearchScope.allScope(project)
                        )
                .stream().filter(file -> file.getName().matches(TEST_CASE_REGEXP))
                .collect(Collectors.toCollection(() -> new ArrayList<>()));
        DefaultListModel<String> model = new DefaultListModel<>();
        testFiles.stream().forEach(test -> model.addElement(test.getName().replace(JAVA_FILE_EXTENSION, "")));
        testCaseList.setModel(model);
    }

    public void updateButtonListener(AnActionEvent event){
        updatedTests = new ArrayList<>();
        notUpdatedTests = new ArrayList<>();
        DefaultListModel<String> model = (DefaultListModel) testCaseList.getModel();
        Arrays.stream(model.toArray())
                .forEach(testCase -> updateAutomationStatusLabel(testCase.toString()));
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Updated Tests:", updatedTests.toArray());
        tableModel.addColumn("Not Updated Tests:", notUpdatedTests.toArray());
        testCasesPerModuleTable.setModel(tableModel);
    }

    public void updateAutomationStatusLabel(String id){
        int testId = Integer.valueOf(id.replace("C", ""));
        TestCase test = new TestCase();
        test.setId(testId);
        test.setCustomAutomationStatus(TEST_RAIL_AUTOMATION_STATUS_DONE);
        try {
            getTestRailClient().updateTestCase(testId, test);
            updatedTests.add(id);
        } catch (TestRailException e){
            notUpdatedTests.add(id);
            log.info("The following test case wasn`t updated: " + id);
            log.info(e.getMessage());
        }
    }

    private void initializeTestRailClient(){
        Optional<String> endpoint = TestRailHelper.getEndpoint();
        Optional<String> username = TestRailHelper.getUsername();
        Optional<String> password = TestRailHelper.getPassword();
        testRail = new TestRailClientBuilder(endpoint.get(), username.get(), password.get()).build();
    }

    public TestRailClient getTestRailClient() {
        return testRail;
    }
}