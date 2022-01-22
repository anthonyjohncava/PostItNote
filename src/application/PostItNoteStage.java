package application;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
/**
 * This class implements a Post It Note.
 * @author Anthony John Cava - cavay010
 * @version 1.0
 */
public class PostItNoteStage {
	private double sizeX;
	private double sizeY;
	private double positionX;
	private double positionY;
	private double origSizeX;
	private double origSizeY;
	private BorderPane content;
	private BorderPane buttonArea;
	private BorderPane bottom;
	private Scene scene;
	private Stage stage;
	private Button newPostItNote;
	private Button deletePostItNote;
	private TextArea textArea;
	private Font buttonFont;
	private Button resize;
	private Region region;
	private ContextMenu rightClickMenu;
	private MenuItem cut;
	private MenuItem copy;
	private MenuItem paste;
	private MenuItem about;
	private MenuItem exit;
	private double xOffset;
	private double yOffset;
	private double origWidthCursorPosition;
	private double origHeightCursorPosition;
	
	/**
	 * This construct a Post It Note with a specified size and
	 * position on the screen.
	 * @param sizeX The original width of the post-it note.
	 * @param sizeY The original height of the post it note.
	 * @param positionX The original horizontal position of the post it note.
	 * @param positionY The original vertical position of the post it note.
	 */
	public PostItNoteStage(double sizeX, double sizeY, double positionX, double positionY) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.positionX = positionX;
		this.positionY = positionY;
		
		// Stores the original size of the note, to be set as a limit when resizing.
		this.origSizeX = sizeX;
		this.origSizeY = sizeY;

		createPostItWindow();
		createRightClickMenu();

		// Sets actions for clickable areas on the note.
		newPostItNote.setOnAction(newButton);
		deletePostItNote.setOnAction(closeButton);
		content.setOnMouseClicked(rightClick);
		textArea.setOnMouseClicked(rightClick);

		// Sets actions on right click window buttons.
		cut.setOnAction(cutButton);
		copy.setOnAction(copyButton);
		paste.setOnAction(pasteButton);
		about.setOnAction(aboutButton);
		exit.setOnAction(exitButton);
		
		// Allows the title bar to be clicked and dragged around the screen.
		buttonArea.setOnMousePressed(leftClickTitle);
		buttonArea.setOnMouseDragged(dragTitle);

		// Allows the note to be resized.
		resize.setOnMousePressed(resizeButtonPressed);
		resize.setOnMouseDragged(resizeButtonDragged);
	}

	/**
	 * This method creates a standard post-it note with the given size and position.
	 */
	public void createPostItWindow() {
		// Creates the layout of the note.
		content = new BorderPane();	
		buttonArea = new BorderPane();
		bottom = new BorderPane();		
		content.setTop(buttonArea);
		content.setBottom(bottom);
		
		// Adds the content to the scene and the scene to the stage.	
		scene = new Scene(content, sizeX, sizeY);
		stage = new Stage();
		stage.setX(positionX);
		stage.setY(positionY);
		stage.setScene(scene);		

		// Removes the default title bar.
		stage.initStyle(StageStyle.UNDECORATED);
		
		
		// Formats the Title bar and its buttons.
		newPostItNote = new Button("+");
		deletePostItNote = new Button("x");
		buttonArea.setLeft(newPostItNote);
		buttonArea.setRight(deletePostItNote);
		buttonArea.setStyle("-fx-background-color:rgb(248, 247, 182)");
		newPostItNote.setStyle("-fx-background-color:rgb(248, 247, 182)");
		deletePostItNote.setStyle("-fx-background-color:rgb(248, 247, 182)");
		buttonFont = Font.font("Arial", FontWeight.BOLD,20);
		newPostItNote.setFont(buttonFont);
		newPostItNote.setTextFill(Color.GREY);
		deletePostItNote.setFont(buttonFont);
		deletePostItNote.setTextFill(Color.GREY);

		// Adds the text area to the center.
		textArea = new TextArea();		
		content.setCenter(textArea);

		// Formats the bottom area and the resize button.
		resize = new Button("///");
		bottom.setRight(resize);
		resize.setTextFill(Color.GREY);
		resize.setStyle("-fx-background-color: rgb(253, 253, 201)");
		bottom.setStyle("-fx-background-color: rgb(253, 253, 201)");

		// Shows the stage and formats the text area colour.
		stage.show();		
		region = (Region) textArea.lookup(".content");
		region.setStyle("-fx-background-color: rgb(253, 253, 201)");	
	}
	
	/**
	 * This method creates the window when the right button is clicked.
	 */
	public void createRightClickMenu() {
		rightClickMenu = new ContextMenu();
		cut = new MenuItem("Cut");
		rightClickMenu.getItems().add(cut);
		copy = new MenuItem("Copy");
		rightClickMenu.getItems().add(copy);
		paste = new MenuItem("Paste");
		rightClickMenu.getItems().add(paste);
		about = new MenuItem("About");
		rightClickMenu.getItems().add(about);
		exit = new MenuItem("Exit");
		rightClickMenu.getItems().add(exit);
		
		// This gets rid of the default right click event.
		textArea.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);		
	}	
	
	/**
	 * This event creates an additional note when the New button is pressed.
	 */
	EventHandler<ActionEvent> newButton = new EventHandler<ActionEvent>() {			
		@Override
		public void handle(ActionEvent e) {
			double x = stage.getX();
			double y = stage.getY();
			double newX = x + stage.getWidth();
			double newY = y;

			Rectangle2D screen = Screen.getPrimary().getVisualBounds();
			
			/*
			 * Sets the new starting position of the new note on the next row if the 
			 * user reaches the width of the monitor.
			 */			
			if(stage.getX() + stage.getWidth()*2 > screen.getWidth()){
				newX = 0;
				newY = stage.getY() + stage.getHeight();
			}
			// Creates a new Postit note on a different position.
			new PostItNoteStage(sizeX,sizeY,newX,newY);				
		}			
	};
	
	/**
	 * This event closes the specified post it note.
	 */	
	EventHandler<ActionEvent> closeButton = new EventHandler<ActionEvent>(){ 
		@Override
		public void handle(ActionEvent e){
			stage.close();
		}
	};	

	/**
	 * This event detects a right click and opens the right clikc menu.
	 */
	EventHandler<MouseEvent> rightClick = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent e) {
			if(e.getButton() == MouseButton.SECONDARY) {
				rightClickMenu.show(content, e.getScreenX(), e.getScreenY());
			}
		}
	};

	/**
	 * This event is used to cut a text from the text area.
	 * It stores the selected string on a String variable. If the string is not null
	 * or is greater than 0 in length, then it is stored to the System Clipboard.
	 * The selected text is replaced with an empty string.
	 */
	EventHandler<ActionEvent> cutButton = new EventHandler<ActionEvent>(){ 	
		@Override
		public void handle(ActionEvent e){			
			String text = textArea.getSelectedText();
			textArea.replaceSelection("");
			ClipboardContent clip = new ClipboardContent();			
			if (text.length() > 0) {
				clip.putString(text);			
				Clipboard.getSystemClipboard().setContent(clip);				
			}
		}
	};
	
	/**
	 * This event is used to copy text from the text area.
	 * It stores the selected string on a String variable. If the string is not null
	 * or is greater than 0 in length, then it is stored to the System Clipboard.
	 * The selected string is left on the text area.
	 */
	EventHandler<ActionEvent> copyButton = new EventHandler<ActionEvent>(){ 	
		@Override
		public void handle(ActionEvent e){
			ClipboardContent clip = new ClipboardContent();
			if (textArea.getSelectedText().length() > 0) {
				clip.putString(textArea.getSelectedText());			
				Clipboard.getSystemClipboard().setContent(clip);				
			}
		}
	};	
	
	/**
	 * This event is used to paste text to the text area.
	 * If the System Clipboard has a string, then it inserts the string to the position
	 * of the caret/cursor in the text area.
	 */
	EventHandler<ActionEvent> pasteButton = new EventHandler<ActionEvent>(){ 
		@Override
		public void handle(ActionEvent e){
			if(Clipboard.getSystemClipboard().hasString()) {
				textArea.insertText(textArea.getCaretPosition(), (String)Clipboard.getSystemClipboard().getContent(DataFormat.PLAIN_TEXT));
			}
		}
	};	

	/**
	 * This event opens a window about the program and the author.
	 */
	EventHandler<ActionEvent> aboutButton = new EventHandler<ActionEvent>(){ 	
		@Override
		public void handle(ActionEvent e){
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Post-It Note");
			alert.setHeaderText("Post-It Note");

			Image image = new Image("pic.jpg");
			ImageView imageView = new ImageView(image);

			// Sets a standard size of the image.
			imageView.setFitHeight(200);
			imageView.setFitWidth(150);
			GridPane.setConstraints(imageView, 0, 0);
			
			// Uses a GridPane to format contents into the alert window.
			GridPane grid = new GridPane();
			grid.setPadding(new Insets(10,10,10,10));
			grid.setVgap(8);
			grid.setHgap(10);

			// Details of the program and the author.
			Label detail = new Label("Digital Post-It Note using JavaFX\n"
					+ "Version 1.0\n"
					+ "Author: Anthony John Cava\n"
					+ "Copyright (c) 2020");
			GridPane.setConstraints(detail, 1, 0);

			// Adds the elements to a grid, then adds the grid to the alert window.
			grid.getChildren().addAll(imageView, detail);
			alert.getDialogPane().setContent(grid);	
			alert.show();
		}		
	};	

	/**
	 * This event closes the right click menu.
	 */
	EventHandler<ActionEvent> exitButton = new EventHandler<ActionEvent>(){ 	
		@Override
		public void handle(ActionEvent e){	
			rightClickMenu.hide();
		}
	};	
	
	/**
	 * This event stores the original position of the cursor in the Title bar
	 * which is used when the title bar is dragged to move the window around
	 * the screen.
	 */
	EventHandler<MouseEvent> leftClickTitle = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent e) {
			xOffset = e.getSceneX();
			yOffset = e.getSceneY();
		}
	};
	
	/**
	 * This event moves the window of the post it note when the cursor is clicked
	 * and dragged around the screen.
	 * Using the cursor position captured by the "leftClickTitle" event, it computes
	 * for the new position by subtracting the original click position from 
	 * the stage position.
	 */
	EventHandler<MouseEvent> dragTitle = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent e) {
			stage.setX(e.getScreenX() - xOffset);
			stage.setY(e.getScreenY() - yOffset);
		}
	};		
	
	/**
	 * This event stores the original details of the cursor when the resize button
	 * is pressed.
	 */
	EventHandler<MouseEvent> resizeButtonPressed = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent e) {		
			origWidthCursorPosition = e.getScreenX();
			origHeightCursorPosition = e.getScreenY();
		}
	};		
	
	/**
	 * This event resized the window of the post it note when dragged.
	 * Restricts the sizing to remain, at minimum, the original size.
	 */
	EventHandler<MouseEvent> resizeButtonDragged = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent e) {
			
			/*
			 * Resizes the width of the window. Stores the new cursor position
			 * and computes for the total difference from the original cursor position.
			 * Stores the new size by adding the increment/decrement to the current
			 * width of the window.
			 */
			double newCursorPositionX = e.getScreenX();
			double totalChangeX = newCursorPositionX - origWidthCursorPosition;
			double currentSizeX = stage.getWidth() + totalChangeX;
			
			/*
			 * Sets the new width of the window.
			 * Restricts the resizing to the minimum width, which is the original
			 * width.
			 */
			if (currentSizeX < origSizeX) {
				stage.setWidth(origSizeX);
			} else {
				stage.setWidth(currentSizeX);
			}
			// Stores the data if the cursor is dragged again.
			origWidthCursorPosition = e.getScreenX();
			
			// Adjusts the size of the height. Same principle as adjusting the width.
			double newCursorPositionY = e.getScreenY();
			double totalChangeY = newCursorPositionY - origHeightCursorPosition;
			double currentSizeY = stage.getHeight() + totalChangeY;

			if (currentSizeY < origSizeY) {
				stage.setHeight(origSizeY);
			} else {
				stage.setHeight(currentSizeY);
			}
			origHeightCursorPosition = e.getScreenY();
		}
	};		
}
