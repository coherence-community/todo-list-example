/*
 * Copyright (c) 2020 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * http://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.todo.client;

import de.perdoctus.fx.FxCdiApplication;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.application.Platform;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.geometry.Pos;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import javafx.stage.Stage;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 * Main application.
 *
 * @author Tim Middleton
 * @author Manfred Riem
 * @author Aleks Seovic
 */
@SuppressWarnings({"unused"})
@ApplicationScoped
public class ToDoApplication
        extends FxCdiApplication
    {
    @Inject
    private FXMLLoader fxmlLoader;

    /**
     * Stores the task manager.
     */
    @Inject
    private TaskManager taskManager;

    /**
     * The active count.
     */
    private IntegerProperty activeCount;

    /**
     * The completed count.
     */
    private IntegerProperty completedCount;

    /**
     * Stores the "Title" label.
     */
    @FXML
    private Label lblTitle;

    /**
     * Displays the "Active" count.
     */
    @FXML
    private Label lblActive;

    /**
     * Displays the "Completed" count.
     */
    @FXML
    private Label lblCompleted;

    /**
     * The 'Clear Completed' button.
     */
    @FXML
    private Button btnClearCompleted;

    /**
     * The filtered tasks.
     */
    private FilteredList<Task> filteredTasks;

    /**
     * The task table.
     */
    @FXML
    private TableView<Task> tblTasks;

    /**
     * The list of tasks.
     */
    private ObservableList<Task> tasks;

    /**
     * Stores the task description.
     */
    @FXML
    private TextField txtDescription;

    /**
     * Add a task.
     *
     * @param event the event.
     */
    @FXML
    public void btnAddTodoAction(ActionEvent event)
        {
        Platform.runLater(() ->
                          {
                          String description = txtDescription.getText();
                          if (description == null || description.trim().isEmpty())
                              {
                              Alert alert = new Alert(Alert.AlertType.ERROR, "No description was entered", ButtonType.OK);
                              alert.showAndWait();
                              }
                          else
                              {
                              taskManager.addTodo(description);
                              tblTasks.refresh();
                              }
                          txtDescription.clear();
                          });
        }

    /**
     * Toggle to all tasks.
     *
     * @param event the event.
     */
    @FXML
    public void btnAllToggleAction(ActionEvent event)
        {
        Platform.runLater(() ->
                          {
                          tasks.clear();
                          tasks.addAll(taskManager.getAllTasks());
                          filteredTasks.setPredicate(t -> true);
                          activeCount.set(taskManager.getActiveCount());
                          completedCount.set(taskManager.getCompletedCount());
                          tblTasks.refresh();
                          });
        }

    /**
     * Toggle to active tasks.
     *
     * @param event the event.
     */
    @FXML
    public void btnActiveToggleAction(ActionEvent event)
        {
        Platform.runLater(() ->
                          {
                          tasks.clear();
                          tasks.addAll(taskManager.getActiveTasks());
                          filteredTasks.setPredicate(t -> !t.isCompleted());
                          activeCount.set(taskManager.getActiveCount());
                          completedCount.set(taskManager.getCompletedCount());
                          tblTasks.refresh();
                          });
        }

    /**
     * Toggle to completed tasks.
     *
     * @param event the event.
     */
    @FXML
    public void btnCompletedToggleAction(ActionEvent event)
        {
        Platform.runLater(() ->
                          {
                          tasks.clear();
                          tasks.addAll(taskManager.getCompletedTasks());
                          filteredTasks.setPredicate(Task::isCompleted);
                          activeCount.set(taskManager.getActiveCount());
                          completedCount.set(taskManager.getCompletedCount());
                          tblTasks.refresh();
                          });
        }

    /**
     * Clear out all completed Todos.
     *
     * @param actionEvent the action event.
     */
    @FXML
    public void btnClearCompletedAction(ActionEvent actionEvent)
        {
        taskManager.removeCompletedTasks();
        }

    /**
     * Initialize the application.
     */
    @SuppressWarnings("unchecked")
    public void initialize()
        {
        lblTitle.setText("Tasks");

        TableColumn<Task, Long> createdColumn = new TableColumn<>("Created");
        createdColumn.setCellValueFactory(cvf -> new SimpleObjectProperty<>(cvf.getValue(), "created", cvf.getValue().getCreatedAt()));
        createdColumn.setVisible(false);
        
        TableColumn<Task, Boolean> completedColumn = new TableColumn<>("Completed");
        completedColumn.setEditable(true);
        completedColumn.setCellValueFactory(cvf -> new SimpleBooleanProperty(cvf.getValue(), "completed", cvf.getValue().isCompleted())
            {
            @Override
            protected void fireValueChangedEvent()
                {
                taskManager.updateCompleted(cvf.getValue().getId(), getValue());
                }
            });
        completedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(completedColumn));

        TableColumn<Task, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setEditable(true);
        descriptionColumn.setPrefWidth(430);
        descriptionColumn.setCellValueFactory(cvf -> new SimpleObjectProperty<>(cvf.getValue(), "description", cvf.getValue().getDescription()));
        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionColumn.setOnEditCommit(event ->
                                   {
                                   if (event.getNewValue() != null)
                                       {
                                       Task todo = event.getRowValue();
                                       taskManager.updateText(todo.getId(), event.getNewValue());
                                       }
                                   });

        TableColumn<Task, String> removeColumn = new TableColumn<>("Remove");
        removeColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        removeColumn.setCellFactory(cf ->
                                    {
                                    TableCell<Task, String> cell = new TableCell<>()
                                        {
                                        private final Button removeButton = new Button("X");

                                        @Override
                                        protected void updateItem(String item, boolean empty)
                                            {
                                            super.updateItem(item, empty);
                                            if (empty)
                                                {
                                                setGraphic(null);
                                                }
                                            else if (getTableRow() != null)
                                                {
                                                final Task task = getTableRow().getItem();
                                                removeButton.setOnAction(e -> taskManager.removeTodo(task.getId()));
                                                setGraphic(removeButton);
                                                }
                                            }
                                        };
                                    cell.setAlignment(Pos.CENTER);
                                    return cell;
                                    });
        tasks = FXCollections.observableList(new ArrayList<>(taskManager.getAllTasks()));
        filteredTasks = new FilteredList<>(tasks, t -> true);

        SortedList<Task> sortedList = new SortedList<>(filteredTasks);
        tblTasks.getColumns().setAll(createdColumn, completedColumn, descriptionColumn, removeColumn);
        tblTasks.getSortOrder().add(createdColumn);
        tblTasks.setItems(sortedList);
        tblTasks.setEditable(true);
        sortedList.comparatorProperty().bind(tblTasks.comparatorProperty());

        activeCount = new SimpleIntegerProperty(taskManager.getActiveCount());
        activeCount.addListener(e -> lblActive.setText(Integer.toString(activeCount.get())));
        lblActive.setText(Integer.toString(activeCount.get()));

        completedCount = new SimpleIntegerProperty(taskManager.getCompletedCount());
        completedCount.addListener(e -> lblCompleted.setText(Integer.toString(completedCount.get())));
        lblCompleted.setText(Integer.toString(completedCount.get()));
        
        btnClearCompleted.visibleProperty().bind(completedCount.greaterThan(0));
        }


    /**
     * Process the TaskEvent.
     *
     */
    private void processTaskEvent(@Observes TaskEvent event)
        {
        final Task oldValue = event.getOldValue();
        final Task newValue = event.getNewValue();

        Platform.runLater(() ->
                          {
                          if (oldValue != null)
                              {
                              if (oldValue.isCompleted())
                                  {
                                  completedCount.set(completedCount.get() - 1);
                                  }
                              if (!oldValue.isCompleted())
                                  {
                                  activeCount.set(activeCount.get() - 1);
                                  }
                              tasks.remove(oldValue);
                              }
                          if (newValue != null)
                              {
                              if (newValue.isCompleted())
                                  {
                                  completedCount.set(completedCount.get() + 1);
                                  }
                              if (!newValue.isCompleted())
                                  {
                                  activeCount.set(activeCount.get() + 1);
                                  }
                              tasks.add(newValue);
                              }
                          tblTasks.refresh();
                          });
        }

    /**
     * Start the application.
     *
     * @param stage  the initial stage
     *
     * @throws Exception when a serious error occurs.
     */
    @Override
    public void start(Stage stage, Application.Parameters parameters) throws Exception
        {
        fxmlLoader.setController(this);
        fxmlLoader.setLocation(getClass().getResource("ToDoApplication.fxml"));
        
        final Parent mainWindow = fxmlLoader.load();
        final Scene scene = new Scene(mainWindow);

        stage.setTitle("JavaFX To Do List");
        stage.setScene(scene);
        stage.show();
        }
    }
