package linus.commentar_creator;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

	private static String buildString(final String str, final int counter) {
		final StringBuilder out = new StringBuilder(str.length() * counter);
		for (int i = 0; i < counter; i++)
			out.append(str);
		return out.toString();
	}

	private final TextField input = new TextField();
	private final TextArea output = new TextArea();

	private void triangle(final boolean doSpaces) {
		final String text = input.getText().trim();
		final StringBuilder out = new StringBuilder();
		for (int i = 1; i < text.length(); i++)
			if (doSpaces || text.charAt(i) != ' ')
				out.append(text.substring(0, i)).append(System.lineSeparator());
		out.append(text).append(System.lineSeparator());
		for (int i = text.length() - 1; i > 0; i--)
			if (doSpaces || text.charAt(i) != ' ')
				out.append(text.substring(0, i)).append(System.lineSeparator());
		output.setText(out.toString());
	}

	private void wiggle(final int counter, final int width) {
		final String text = input.getText().trim();
		final StringBuilder out = new StringBuilder();
		for (int i = 0; i < counter; i++) {
			for (int k = 0; k < width; k++)
				out.append(buildString(" ", k)).append(text).append(System.lineSeparator());
			for (int k = width; k > 0; k--)
				out.append(buildString(" ", k)).append(text).append(System.lineSeparator());
		}
		output.setText(out.toString());
	}
	
	private void pyramid(final boolean doSpaces) {
		final String text = input.getText().trim();
		final StringBuilder out = new StringBuilder();
		final int length = text.length();
		final boolean odd = length % 2 != 0;
		final int half = odd ? (length - 1) / 2 : length / 2;
		for(int i = odd ? 0 : 1; i <= half; i++) {
			final int from = half - i;
			final int to = half + i;
			if(doSpaces || (text.charAt(from) != ' ' && text.charAt(to - (odd ? 0 : 1)) != ' '))
				out
					.append(buildString(" ", from))
					.append(text.substring(from, to + (odd ? 1 : 0)))
					.append(buildString(" ", from))
					.append(System.lineSeparator());
		}
		output.setText(out.toString());
	}

	@Override
	public void start(final Stage stage) throws Exception {

		stage.setWidth(700);
		stage.setHeight(450);
		stage.setTitle("Commentar Creator");

		final Label inputInfo = new Label("Insert word here:");

		final VBox inputBox = new VBox(inputInfo, input);

		final HBox buttons = new HBox(5);
		buttons.setDisable(true);
		input.setOnKeyTyped(e -> {
			if (input.getLength() > 0)
				buttons.setDisable(false);
			else
				buttons.setDisable(true);
		});
		buttons.setPadding(new Insets(5, 0, 0, 0));

		final AnchorPane options = new AnchorPane();

		final Button triangle = new Button("Triangle");
		triangle.setOnAction(e -> {

			final CheckBox doSpacesButton = new CheckBox();
			doSpacesButton.setOnAction(f -> triangle(doSpacesButton.isSelected()));
			doSpacesButton.setSelected(true);

			final Label doSpacesInfo = new Label("do spaces");

			final HBox doSpacesBox = new HBox(4, doSpacesButton, doSpacesInfo);
			options.getChildren().setAll(doSpacesBox);

			triangle(true);

		});
		triangle.setPrefWidth(100);

		final Button wiggle = new Button("Wiggle");
		wiggle.setOnAction(e -> {

			final String defaultCounter = "3";
			final String defaultLength = "5";

			final TextField counterButton = new TextField(defaultCounter);
			final TextField lengthButton = new TextField(defaultLength);

			counterButton.setOnKeyTyped(f -> {
				final String text = counterButton.getText().trim();
				if (text.isEmpty())
					return;
				if (!text.matches("\\d*"))
					counterButton.setText(text.replaceAll("[^\\d]", ""));
				else
					wiggle(Integer.parseInt(text), Integer.parseInt(lengthButton.getText()));

			});
			counterButton.setPrefWidth(30);

			lengthButton.setOnKeyTyped(f -> {
				final String text = lengthButton.getText().trim();
				if (text.isEmpty())
					return;
				else if (!text.matches("\\d*"))
					lengthButton.setText(text.replaceAll("[^\\d]", ""));
				else
					wiggle(Integer.parseInt(counterButton.getText()), Integer.parseInt(text));
			});
			lengthButton.setPrefWidth(30);

			final Label counterInfo = new Label("Counter");
			final HBox counterBox = new HBox(5, counterButton, counterInfo);

			final Label lengthInfo = new Label("Length");
			final HBox lengthBox = new HBox(5, lengthButton, lengthInfo);

			final VBox box = new VBox(5, counterBox, lengthBox);
			options.getChildren().setAll(box);

			wiggle(Integer.parseInt(defaultCounter), Integer.parseInt(defaultLength));

		});
		wiggle.setPrefWidth(100);

		final Button pyramid = new Button("Pyramid");
		pyramid.setOnAction(e -> {
			final CheckBox doSpacesButton = new CheckBox();
			doSpacesButton.setOnAction(f -> pyramid(doSpacesButton.isSelected()));
			doSpacesButton.setSelected(true);

			final Label doSpacesInfo = new Label("do spaces");

			final HBox doSpacesBox = new HBox(4, doSpacesButton, doSpacesInfo);
			options.getChildren().setAll(doSpacesBox);

			pyramid(true);
		});
		pyramid.setPrefWidth(100);
		
		buttons.getChildren().addAll(triangle, wiggle, pyramid);

		final HBox outputOptions = new HBox(5);
		outputOptions.setDisable(true);

		output.prefHeightProperty().bind(stage.heightProperty().subtract(output.layoutYProperty()));
		output.textProperty()
				.addListener((obj, oldValue, newValue) -> outputOptions.setDisable(output.getText().trim().isEmpty()));

		final Button clear = new Button("Clear");
		clear.setOnAction(e -> output.clear());
		clear.setPrefWidth(100);

		final Button selectAll = new Button("Select all");
		selectAll.setOnAction(e -> {
			output.selectAll();
			output.requestFocus();
		});
		selectAll.setPrefWidth(100);

		final Button copySelected = new Button("Copy selected");
		copySelected.setOnAction(e -> output.copy());
		copySelected.setPrefWidth(100);

		final Button copyAll = new Button("Copy all");
		copyAll.setOnAction(e -> {
			final String text = output.getText();
			final ClipboardContent content = new ClipboardContent();
			content.putString(text);
			Clipboard.getSystemClipboard().setContent(content);
		});
		copyAll.setPrefWidth(100);

		outputOptions.getChildren().addAll(clear, selectAll, copySelected, copyAll);
		outputOptions.setAlignment(Pos.BOTTOM_RIGHT);
		final VBox outputBox = new VBox(5, output, outputOptions);

		final VBox root = new VBox(inputBox, buttons, options, outputBox);
		root.setSpacing(5);
		root.setPadding(new Insets(10));
		stage.setScene(new Scene(root));
		stage.show();
	}

	public static void main(final String... args) {
		launch(Main.class, args);
	}

}
