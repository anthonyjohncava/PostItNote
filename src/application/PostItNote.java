package application;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * This class runs the Post It Note application.
 * @author Anthony John Cava - cavay010
 * @version 1.0
 */
public class PostItNote extends Application {
	PostItNoteStage mainWindow;
	
	public PostItNote() {
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Starting Post-It Note Application...");
		System.out.println("Author: Anthony John Cava");
		
		launch(args);
	}

	@Override
	public void start(Stage arg0) throws Exception {
		mainWindow = new PostItNoteStage(200, 200, 0, 0);
	}

}
