/*
 * Copyright (c) 2020 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.todo.client;

import de.perdoctus.fx.FxCdiApplication;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.application.Platform;

import javafx.beans.property.SimpleBooleanProperty;
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
     * Stores the "Title" label.
     */
    @FXML
    private Label lblTitle;

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
        }


    /**
     * Handle the TaskInsertedEvent.
     */
    private void processTaskInsertedEvent(@Observes @TaskEvent.Inserted Task task)
        {
        Platform.runLater(() ->
                          {
                          tasks.add(task);
                          tblTasks.refresh();
                          });
        }

    /**
     * Handle the TaskUpdatedEvent.
     */
    private void processTaskUpdatedEvent(@Observes @TaskEvent.Updated Task task)
        {
        Platform.runLater(() ->
                          {
                          tasks.removeIf(t -> t.getId().equals(task.getId()));
                          tasks.add(task);
                          tblTasks.refresh();
                          });
        }

    /**
     * Handle the TaskRemovedEvent.
     */
    private void processTaskRemovedEvent(@Observes @TaskEvent.Removed Task task)
        {
        Platform.runLater(() ->
                          {
                          tasks.remove(task);
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
