

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class MediaControl extends BorderPane {

	private MediaPlayer mp;
	private MediaView mediaView;
	private final boolean repeat = false;
	private boolean stopRequested = false;
	private boolean atEndOfMedia = false;
	private Duration duration;
	private Slider timeSlider;
	private Label playTime;
	private HBox mediaBar;

	public MediaControl(final MediaPlayer mp, double width, double height) {
		this.mp = mp;
		setPadding(new Insets(15, 10,0, 10));
		Image play = new Image("image\\play.png");
		Image paused = new Image("image\\paused.png");

		//        setStyle("-fx-background-color: #bfc2c7;");
		mediaView = new MediaView(mp);
		mediaView.setFitHeight(height*0.70);
		mediaView.setFitWidth(width*0.70);
		//		mediaView.setPreserveRatio(true);
		Pane mvPane = new Pane() {
		};
		mvPane.getChildren().add(mediaView);
		mvPane.setStyle("-fx-background-color: black;");
		BorderPane.setAlignment(mediaView, Pos.TOP_CENTER);
		setTop(mediaView);

		mediaBar = new HBox();
		mediaBar.setAlignment(Pos.CENTER);
		mediaBar.setPadding(new Insets(0, 0, 5, 0));
		BorderPane.setAlignment(mediaBar, Pos.CENTER);

		//        final Button playButton = new Button(null, new ImageView(play));
		ImageView playButton= new ImageView(play);

		playButton.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {
				Status status = mp.getStatus();

				if (status == Status.UNKNOWN || status == Status.HALTED) {
					// don't do anything in these states
					return;
				}
				if (status == Status.PAUSED
						|| status == Status.PLAYING
						|| status == Status.READY
						|| status == Status.STOPPED) {
					// rewind the movie if we're sitting at the end
					if (atEndOfMedia) {
						mp.seek(mp.getStartTime());
						atEndOfMedia = false;
					}
					mp.play();
				} else {
					mp.pause();
				}
				e.consume();
			}

		});
		mp.currentTimeProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable ov) {
				updateValues();
			}
		});

		mp.setOnPlaying(new Runnable() {
			public void run() {
				System.out.println("playing");
				if (stopRequested) {
					mp.pause();
					playButton.setImage(paused);
					stopRequested = false;
				} else {
					playButton.setImage(paused);
				}
			}
		});

		mp.setOnPaused(new Runnable() {
			public void run() {
				playButton.setImage(play);
			}
		});

		mp.setOnReady(new Runnable() {
			public void run() {
				duration = mp.getMedia().getDuration();
				updateValues();
			}
		});

		mp.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);
		mp.setOnEndOfMedia(new Runnable() {
			public void run() {
				if (!repeat) {
					playButton.setImage(play);
					stopRequested = true;
					atEndOfMedia = true;
				}
			}
		});

		mediaBar.getChildren().add(playButton);

		// Add Time label
		Label timeLabel = new Label("  Time: ");
		timeLabel.setFont(new Font("David",14));
		mediaBar.getChildren().add(timeLabel);

		// Add time slider
		timeSlider = new Slider();
		HBox.setHgrow(timeSlider, Priority.ALWAYS);
		//        timeSlider.setMinWidth(10);
		timeSlider.setMaxWidth(Double.MAX_VALUE);
		timeSlider.valueProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable ov) {
				if (timeSlider.isValueChanging()) {
					// multiply duration by percentage calculated by slider position
					mp.seek(duration.multiply(timeSlider.getValue() / 100.0));
				}
			}
		});
		mediaBar.getChildren().add(timeSlider);

		// Add Play label
		playTime = new Label();
		//        playTime.setPrefWidth(10);
		//        playTime.setMinWidth(10);
		mediaBar.getChildren().add(playTime);

		setCenter(mediaBar);
	}

	protected void updateValues() {
		if (playTime != null && timeSlider != null) {
			Platform.runLater(new Runnable() {
				public void run() {
					Duration currentTime = mp.getCurrentTime();
					playTime.setText(formatTime(currentTime, duration));
					timeSlider.setDisable(duration.isUnknown());
					if (!timeSlider.isDisabled()
							&& duration.greaterThan(Duration.ZERO)
							&& !timeSlider.isValueChanging()) {
						timeSlider.setValue(currentTime.divide(duration).toMillis()
								* 100.0);
					}
				}
			});
		}
	}

	private static String formatTime(Duration elapsed, Duration duration) {
		int intElapsed = (int) Math.floor(elapsed.toSeconds());
		int elapsedHours = intElapsed / (60 * 60);
		if (elapsedHours > 0) {
			intElapsed -= elapsedHours * 60 * 60;
		}
		int elapsedMinutes = intElapsed / 60;
		int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
				- elapsedMinutes * 60;

		if (duration.greaterThan(Duration.ZERO)) {
			int intDuration = (int) Math.floor(duration.toSeconds());
			int durationHours = intDuration / (60 * 60);
			if (durationHours > 0) {
				intDuration -= durationHours * 60 * 60;
			}
			int durationMinutes = intDuration / 60;
			int durationSeconds = intDuration - durationHours * 60 * 60
					- durationMinutes * 60;
			if (durationHours > 0) {
				return String.format("%d:%02d:%02d/%d:%02d:%02d",
						elapsedHours, elapsedMinutes, elapsedSeconds,
						durationHours, durationMinutes, durationSeconds);
			} else {
				return String.format("%02d:%02d/%02d:%02d",
						elapsedMinutes, elapsedSeconds, durationMinutes,
						durationSeconds);
			}
		} else {
			if (elapsedHours > 0) {
				return String.format("%d:%02d:%02d", elapsedHours,
						elapsedMinutes, elapsedSeconds);
			} else {
				return String.format("%02d:%02d", elapsedMinutes,
						elapsedSeconds);
			}
		}
	}
}