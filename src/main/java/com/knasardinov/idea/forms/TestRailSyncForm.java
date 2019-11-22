package com.knasardinov.idea.forms;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.AnnotatedElementsSearch;
import com.knasardinov.idea.api.TestRailClient;
import com.knasardinov.idea.api.TestRailClientBuilder;
import com.knasardinov.idea.api.TestRailException;
import com.knasardinov.idea.pojo.TestCase;
import com.knasardinov.idea.utils.Settings;
import com.knasardinov.idea.utils.TestRailHelper;
import lombok.extern.log4j.Log4j;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

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
    private JTextField testRailAnnotationInputField;
    private JButton applyTestRailAnnotationButton;
    private JLabel annotationStatus;
    private TestRailClient testRail;
    private static final int TEST_RAIL_AUTOMATION_STATUS_DONE = 3;
    private List<String> updatedTests;
    private List<String> notUpdatedTests;

    public TestRailSyncForm(AnActionEvent event) {
        setTitle("TestRail Integration");
        setSize(700, 500);
        add(rootPanel);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dimension.width / 2 - getSize().width / 2, dimension.height / 2 - getSize().height / 2);
        initializeTestRailClient();
        final Settings settings = ServiceManager.getService(Settings.class);
        testRailAnnotationInputField.setText(settings.getTestRailAnnotation());
        analyzeButton.addActionListener(e -> analyzeButtonListener(event));
        updateButton.addActionListener(e -> updateButtonListener());
        applyTestRailAnnotationButton.addActionListener(e -> testRailAnnotationCheckActionListener(event));
    }

    private void analyzeButtonListener(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PSI_FILE).getProject();
        final Settings settings = ServiceManager.getService(Settings.class);

        PsiClass serviceAnnotation = getTestRailAnnotation(event, settings.getTestRailAnnotation());

        List<String> testRailIdsFromMethods = getAllTestRailIdsFromAnnotatedMethods(project, serviceAnnotation);
        List<String> testRailIdsFromClasses = getAllTestRailIdsFromAnnotatedClasses(project, serviceAnnotation);

        DefaultListModel<String> model = new DefaultListModel<>();
        Stream.concat(testRailIdsFromClasses.stream(), testRailIdsFromMethods.stream())
                .forEach(test -> model.addElement(test));
        testCaseList.setModel(model);
    }

    private List<String> getAllTestRailIdsFromAnnotatedClasses(Project project, PsiClass serviceAnnotation){
        List<PsiClass> classes = new ArrayList< >(
                AnnotatedElementsSearch
                        .searchPsiClasses(serviceAnnotation, GlobalSearchScope.projectScope(project))
                        .findAll()
        );
        return getTestRailAnnotations(classes);
    }

    private List<String> getAllTestRailIdsFromAnnotatedMethods(Project project, PsiClass serviceAnnotation){
        List<PsiMethod> methods = new ArrayList< >(
                AnnotatedElementsSearch
                        .searchPsiMethods(serviceAnnotation, GlobalSearchScope.projectScope(project))
                        .findAll()
        );
        return getTestRailAnnotations(methods);
    }

    private List<String> getTestRailAnnotations(List javaEntities){
        List<String> testRailIds = new ArrayList<>();
        PsiAnnotation[] annotations;
        for(int i = 0; i < javaEntities.size(); i++){
            annotations = ((PsiMember) javaEntities.get(i)).getAnnotations();
            for(int j = 0; j < annotations.length; j++){
                if(annotations[j].getQualifiedName().contains("TestRailId")){
                    testRailIds.add(annotations[j]
                            .findAttributeValue("value")
                            .getText()
                            .replaceAll("\"", "")
                    );
                }
            }
        }
        return testRailIds;
    }

    private boolean testRailAnnotationCheckActionListener(AnActionEvent event) {
        String testRailAnnotation = testRailAnnotationInputField.getText();
        PsiClass annotation = getTestRailAnnotation(event, testRailAnnotation);
        if(annotation != null && annotation.isAnnotationType()){
            annotationStatus.setText("Valid");
            final Settings settings = ServiceManager.getService(Settings.class);
            settings.setTestRailAnnotation(testRailAnnotation);
            return true;
        } else {
            annotationStatus.setText("Invalid");
            return false;
        }
    }

    private PsiClass getTestRailAnnotation(AnActionEvent event, String testRailAnnotationString){
        Project project = event.getData(PlatformDataKeys.PSI_FILE).getProject();
        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        return javaPsiFacade.findClass(testRailAnnotationString, GlobalSearchScope.allScope(project));
    }

    private void updateButtonListener(){
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

    private void updateAutomationStatusLabel(String id){
        int testId = Integer.parseInt(id.replace("C", ""));
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

    private TestRailClient getTestRailClient() {
        return testRail;
    }
}