import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.dillon.fx.view.control.FilterableTreeItem;
import org.dillon.fx.view.control.TreeItemPredicate;


public class FilterableTreeItemSample extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	
	private TextField filterField;
	private FilterableTreeItem<Actor> folder1;

	@Override
	public void start(Stage stage) throws Exception {
		try {
            Parent root = createContents();
            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	private Parent createContents() {
        VBox vbox = new VBox(6);
        vbox.getChildren().add(createFilterPane());
        vbox.getChildren().add(createAddItemPane());
        Node demoPane = createDemoPane();
        VBox.setVgrow(demoPane, Priority.ALWAYS);
        vbox.getChildren().add(demoPane);
        return new BorderPane(vbox);
    }

	private Node createFilterPane() {
        filterField = new TextField();
        filterField.setPromptText("Enter filter text ...");

        TitledPane pane = new TitledPane("Filter", filterField);
        pane.setCollapsible(false);
        return pane;
    }
	
	private Node createAddItemPane() {
		HBox box = new HBox(6);
		TextField firstname = new TextField();
		firstname.setPromptText("Enter first name ...");
		TextField lastname = new TextField();
		lastname.setPromptText("Enter last name ...");
		
		Button addBtn = new Button("Add new actor to \"Folder 1\"");
		addBtn.setOnAction(event -> {
			FilterableTreeItem<Actor> treeItem = new FilterableTreeItem<>(new Actor(firstname.getText(), lastname.getText()));
			folder1.getInternalChildren().add(treeItem);
		});
		addBtn.disableProperty().bind(Bindings.isEmpty(lastname.textProperty()));
		
		box.getChildren().addAll(firstname, lastname, addBtn);
		TitledPane pane = new TitledPane("Add new element", box);
		pane.setCollapsible(false);
        return pane;
	}

    private Node createDemoPane() {
        HBox hbox = new HBox(6);
        Node filteredTree = createFilteredTree();
        HBox.setHgrow(filteredTree, Priority.ALWAYS);
        hbox.getChildren().add(filteredTree);
        return hbox;
    }

    private Node createFilteredTree() {
        FilterableTreeItem<Actor> root = getTreeModel();
        root.predicateProperty().bind(Bindings.createObjectBinding(() -> {
            if (filterField.getText() == null || filterField.getText().isEmpty())
                return null;
            return TreeItemPredicate.create(actor -> actor.toString().contains(filterField.getText()));
        }, filterField.textProperty()));
        
        TreeView<Actor> treeView = new TreeView<>(root);
        treeView.setShowRoot(false);
        
        TitledPane pane = new TitledPane("Filtered TreeView", treeView);
        pane.setCollapsible(false);
        pane.setMaxHeight(Double.MAX_VALUE);
        return pane;
    }

    private FilterableTreeItem<Actor> getTreeModel() {
        FilterableTreeItem<Actor> root = new FilterableTreeItem<>(new Actor("Root"));
        folder1 = createFolder("Folder 1");
        folder1.setExpanded(true);
        root.getInternalChildren().add(folder1);
        root.getInternalChildren().add(createFolder("Folder 2"));
        root.getInternalChildren().add(createFolder("Folder 3"));
        return root;
    }

    private FilterableTreeItem<Actor> createFolder(String name) {
        FilterableTreeItem<Actor> folder = new FilterableTreeItem<>(new Actor(name));
        getActorList().forEach(actor -> folder.getInternalChildren().add(new FilterableTreeItem<>(actor)));
        return folder;
    }

	private Iterable<Actor> getActorList() {
		ObservableList<Actor> actorList = FXCollections.observableArrayList(
				new Actor("Jack", "Nicholson"), 
				new Actor("Marlon", "Brando"), 
				new Actor("Robert", "De Niro"), 
				new Actor("Al", "Pacino"), 
				new Actor("Daniel","Day-Lewis"), 
				new Actor("Dustin", "Hoffman"), 
				new Actor("Tom", "Hanks"),
				new Actor("Anthony", "Hopkins"), 
				new Actor("Paul", "Newman"), 
				new Actor("Denzel", "Washington"),
				new Actor("Spencer", "Tracy"), 
				new Actor("Laurence", "Olivier"), 
				new Actor("Jack", "Lemmon"));
		return actorList;
	}
	
	private static class Actor {
		public String firstname;
        public String lastname;
        
        public Actor(String string) {
        	this.lastname = string;
        }
        
        public Actor(String firstname, String lastname) {
        	this.firstname = firstname;
        	this.lastname = lastname;
        }

		@Override
        public String toString() {
            return firstname == null ? lastname : firstname + " " + lastname;
        }
    }

}