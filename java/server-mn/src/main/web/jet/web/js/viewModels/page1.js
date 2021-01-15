/*
 * Copyright (c) 2020 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

/*
 * ViewModel for Coherence To Do List Example.
 */
define(["knockout", "ojs/ojbootstrap", "ojs/ojarraydataprovider", "ojs/ojvalidation-base", "ojs/ojdatacollection-utils", "jquery", "ojs/ojknockout",
        "ojs/ojlabel", "ojs/ojtable", "ojs/ojinputtext", "ojs/ojformlayout", "ojs/ojdatetimepicker", "ojs/ojselectcombobox", "ojs/ojradioset",
        "ojs/ojdialog", "ojs/ojbutton", "ojs/ojmenu", "ojs/ojoption"],
    function (ko, Bootstrap, ArrayDataProvider, ValidationBase, DataCollectionEditUtils, $) {

        function Page1ViewModel() {
            var root = this;

            // the full list of tasks
            this.taskLoadedArray = ko.observableArray([]);

            // the filtered list off which the table is driven
            this.taskObservableArray = ko.observableArray([]);
            
            this.dataprovider = new ArrayDataProvider(this.taskObservableArray, {keyAttributes: 'id'});
            this.editRow = ko.observable();
            this.newTask = ko.observable();
            this.itemsLeft = ko.observable("0 Items left");
            this.currentSelection = ko.observable("all");
            this.clearDisabled = ko.observable(true);

            // return the style for completed and description columns
            root.getStyle = function (column, completed) {
                if (column === "description") {
                    return completed ? {
                            "text-decoration": "line-through",
                            "min-width": "20em",
                            "max-width": "60em",
                            "width": "20em"
                        }
                        : {
                            "min-width": "20em",
                            "max-width": "60em",
                            "width": "20em"
                        };
                } else if (column === "completed") {
                    return completed ? {
                            "text-decoration": "line-through",
                            "min-width": "8em",
                            "max-width": "8em",
                            "width": "8em"
                        }
                        : {
                            "min-width": "8em",
                            "max-width": "8em",
                            "width": "8em"
                        };
                }
            };

            // function to log an error
            root.logError = function (message, jqXHR) {
                console.log(message + ", Response is " + jqXHR.status + " - " + jqXHR.statusText)
            };

            // apply the filter to the loaded data
            root.applyFilter = function () {
                var queryCompleted = root.currentSelection();
                queryCompleted = queryCompleted === "all" ? undefined : queryCompleted === "true";

                // filter the data from the taskLoadedArray into the taskObservableArray
                var filteredData = [];
                root.taskLoadedArray().forEach(function (entry) {
                    if (queryCompleted === undefined || queryCompleted === entry.completed) {
                        filteredData.push(entry);
                    }
                });

                // sort and add the data
                root.taskObservableArray(filteredData.sort(function (todoLhs, todoRhs) {
                    return todoLhs.createdAt - todoRhs.createdAt
                }));

                document.getElementById('taskTable').refresh();
                root.updateItemsLeft();
            };

            // update the number of items left
            root.updateItemsLeft = function () {
                var activeCount = 0;
                var completedCount = 0;
                root.taskLoadedArray().forEach(function (entry) {
                    if (!entry.completed) {
                        activeCount++;
                    } else {
                        completedCount++;
                    }
                });

                root.itemsLeft(activeCount + (activeCount === 1 ? " Item " : " Items ") + " left");
                root.clearDisabled(completedCount === 0);
            };

            // refresh the task list
            root.reloadData = function () {
                $.ajax({
                    url: "/api/tasks",
                    type: "GET",
                    dataType: "json",
                    error: function (jqXHR, exception) {
                        root.logError("Unable to retrieve data", jqXHR);
                    },
                    success: function (data) {
                        root.taskLoadedArray(data);
                        root.applyFilter();
                    }
                });

                root.updateItemsLeft();
            };

            // update a task
            root.updateTask = function (id, description, completed) {
                var updatedTask = {
                    id: id,
                    description: description,
                    completed: completed
                };

                $.ajax({
                    url: "/api/tasks/" + id,
                    type: "PUT",
                    data: JSON.stringify(updatedTask),
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    error: function (jqXHR, exception) {
                        root.logError("Unable to update task with id=" + id, jqXHR);
                    }
                });
            };

            // delete a task
            root.deleteTask = function (id) {
                $.ajax({
                    url: "/api/tasks/" + id,
                    type: "DELETE",
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    error: function (jqXHR, exception) {
                        root.logError("Unable to delete task with id=" + id, jqXHR);
                    }
                });
            };

            // add a new taks
            root.addTask = function () {
                var description = root.newTask();
                if (description === "" || description === undefined) {
                    alert("Please enter a task description!");
                } else {
                    var newTask = {
                        description: description
                    };

                    $.ajax({
                        url: "/api/tasks",
                        type: "POST",
                        data: JSON.stringify(newTask),
                        dataType: "json",
                        contentType: "application/json; charset=utf-8",
                        error: function (jqXHR, exception) {
                            root.logError("Unable to create task with description =" + description, jqXHR);
                        },
                        success: function () {
                            root.newTask("");
                        }
                    });
                }
            };

            // clear completed tasks
            root.clearCompleted = function () {
                $.ajax({
                    url: "/api/tasks",
                    type: "DELETE",
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    error: function (jqXHR, exception) {
                        root.logError("Unable to clear completed tasks", jqXHR);
                    }
                });
            };

            // respond to a change in the radio button
            root.currentSelection.subscribe(root.applyFilter);

            // row editing handling functions
            this.beforeRowEditEndListener = function (event) {
                var detail = event.detail;
                if (DataCollectionEditUtils.basicHandleRowEditEnd(event, detail) === false) {
                    event.preventDefault();
                } else {
                    let updatedData = event.target.getDataForVisibleRow(detail.rowContext.status.rowIndex).data;
                    root.updateTask(updatedData.id, updatedData.description, updatedData.completed);
                }
            };

            // process an incoming event and update the clients view of the data
            root.processEvent = function (type, data) {
                if (type === "insert") {
                    root.taskLoadedArray.push(data);
                } else if (type === "delete") {
                    root.taskLoadedArray.remove(function (item) {
                        return item.id === data.id;
                    });
                } else if (type === "update") {
                    var reloadRequired = false;
                    var newArray = [];
                    root.taskLoadedArray().forEach(function (item) {
                        var newItem = item;
                        if (newItem.id === data.id) {
                            newItem.completed = data.completed;
                            newItem.description = data.description;
                            if (newItem.completed !== data.completed || newItem.description !== data.description) {
                                reloadRequired = true;
                            }
                        }
                        newArray.push(newItem);
                    });

                    if (reloadRequired) {
                        root.taskLoadedArray(newArray);
                    }
                }
                // finally apply the filter on the updated data set
                root.applyFilter();
            };

            // handle update to the row
            this.handleUpdate = function (event, context) {
                root.editRow({rowKey: context.key});
            }.bind(this);

            // handle save of row
            this.handleDone = function (event, context) {
                root.editRow({rowKey: null});
            }.bind(this);

            // handle delete
            this.handleDelete = function (event, context) {
                root.deleteTask(context.key);
            }.bind(this);

            // respond to the action menu
            this.menuListener = function (event, context) {
                var id = context.row.id;
                var action = event.target.value;
                if (action === "delete") {
                    root.deleteTask(id);
                } else if (action === "complete") {
                    root.updateTask(context.key, context.row.description, true);
                } else if (action === "reopen") {
                    root.updateTask(context.key, context.row.description, false);
                }
            }.bind(this);

            // stop default action for the menu item
            this.actionListener = function (event) {
                event.detail.originalEvent.stopPropagation();
            };

            // Display message if we are running under IE
            if (typeof (bIsIE) != "undefined") {
                alert("Internet Explorer does not support Server Sent Events.\n" +
                      "Refer to http://www.w3schools.com/html/html5_serversentevents.asp");
            }

            root.reloadData();

            // setup the SSE Events
            var eventSourceTask = new EventSource("/api/tasks/events");

            eventSourceTask.addEventListener("insert", function (event) {
                root.processEvent("insert", JSON.parse(event.data));
            });

            eventSourceTask.addEventListener("update", function (event) {
                root.processEvent("update", JSON.parse(event.data));
            });

            eventSourceTask.addEventListener("delete", function (event) {
                root.processEvent("delete", JSON.parse(event.data));
            });
        }

        return new Page1ViewModel();
    }
);
