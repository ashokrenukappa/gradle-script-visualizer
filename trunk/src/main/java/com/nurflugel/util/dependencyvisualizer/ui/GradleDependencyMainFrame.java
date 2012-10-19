package com.nurflugel.util.dependencyvisualizer.ui;

import com.nurflugel.util.dependencyvisualizer.output.NoConfigurationsFoundException;
import com.nurflugel.util.dependencyvisualizer.parser.GradleDependencyParser;
import com.nurflugel.util.gradlescriptvisualizer.domain.Os;
import com.nurflugel.util.gradlescriptvisualizer.ui.GradleScriptPreferences;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import static com.nurflugel.util.Util.*;
import static com.nurflugel.util.dependencyvisualizer.output.DependencyDotFileGenerator.createOutputForFile;
import static com.nurflugel.util.gradlescriptvisualizer.domain.Os.findOs;
import static javax.swing.JFileChooser.APPROVE_OPTION;

/** Created with IntelliJ IDEA. User: douglas_bullard Date: 6/3/12 Time: 14:32 To change this template use File | Settings | File Templates. */
@SuppressWarnings("MethodOnlyUsedFromInnerClass")
public class GradleDependencyMainFrame
{
  public static final String           TITLE_TEXT                      = "Gradle Dependency Visualizer v";
  private JButton                      selectGradleScriptButton;
  private JRadioButton                 generateJustDOTFilesRadioButton;
  private JRadioButton                 generatePNGPDFFilesRadioButton;
  private JPanel                       mainPanel;
  private JButton                      quitButton;
  private JCheckBox                    deleteDOTFilesOnCheckBox;
  private JTabbedPane                  tabbedPane1;
  private JPanel                       mainUiPanel;
  private JPanel                       resultsUiPanel;
  private JButton                      graphButton;
  private JButton                      quitButton1;
  private JRadioButton                 radioButton1;
  private JRadioButton                 radioButton2;
  private JFrame                       frame;
  private GradleScriptPreferences      preferences;
  private Os                           os;
  private final Set<File>              filesToRender                   = new HashSet<>();
  private final GradleDependencyParser parser;

  public GradleDependencyMainFrame()
  {
    preferences = new GradleScriptPreferences();
    os          = findOs();
    frame       = new JFrame();
    frame.setContentPane(mainPanel);
    initializeUi();
    addActionListeners();
    parser = new GradleDependencyParser();
  }

  /** I like to put all the listeners in one method. */
  private void addActionListeners()
  {
    deleteDOTFilesOnCheckBox.addActionListener(new ActionListener()
      {
        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
          preferences.setShouldDeleteDotFilesOnExit(deleteDOTFilesOnCheckBox.isSelected());
        }
      });
    generateJustDOTFilesRadioButton.addActionListener(new ActionListener()
      {
        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
          preferences.setGenerateJustDotFiles(generateJustDOTFilesRadioButton.isSelected());
        }
      });
    generatePNGPDFFilesRadioButton.addActionListener(new ActionListener()
      {
        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
          preferences.setGenerateJustDotFiles(generateJustDOTFilesRadioButton.isSelected());
        }
      });
    selectGradleScriptButton.addActionListener(new ActionListener()
      {
        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
          try
          {
            selectGradleScript();
          }
          catch (IOException | NoConfigurationsFoundException e)
          {
            e.printStackTrace();
          }
        }
      });
    quitButton.addActionListener(new ActionListener()
      {
        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
          doQuitAction();
        }
      });
  }

  /** Do any initialization that the IDE doesn't do in it's layout. Look and feel, setting UI attributes from stored preferencer, etc. */
  private void initializeUi()
  {
    setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel", frame);
    frame.pack();
    center(frame);
    deleteDOTFilesOnCheckBox.setSelected(preferences.shouldDeleteDotFilesOnExit());
    generateJustDOTFilesRadioButton.setSelected(preferences.generateJustDotFiles());
    setComponentVisibilityFromSettings();
    frame.setTitle(TITLE_TEXT + VERSION);
    frame.setVisible(true);
  }

  /** Called at startup and when the user clicks on one of the checkboxes. */
  private void setComponentVisibilityFromSettings()
  {
    frame.pack();
  }

  /** Open up a file chooser and select the file(s) to process. */
  private void selectGradleScript() throws IOException, NoConfigurationsFoundException
  {
    JFileChooser chooser = new JFileChooser();
    String       lastDir = preferences.getLastDir();

    if (lastDir != null)
    {
      chooser.setCurrentDirectory(new File(lastDir));
    }

    chooser.setFileFilter(new FileNameExtensionFilter("Gradle scripts", "gradle"));
    chooser.setMultiSelectionEnabled(false);

    int returnVal = chooser.showOpenDialog(frame);

    if (returnVal == APPROVE_OPTION)
    {
      File selectedFile = chooser.getSelectedFile();

      chooser.hide();
      createOutputForFile(selectedFile, parser, preferences, "dibble.dot", os);
    }
  }

  private void doQuitAction()
  {
    getOutputPreferencesFromUi();
    preferences.save();
    System.exit(0);
  }

  private void getOutputPreferencesFromUi() {}

  // --------------------------- main() method ---------------------------
  public static void main(String[] args)
  {
    GradleDependencyMainFrame ui = new GradleDependencyMainFrame();
  }
}
