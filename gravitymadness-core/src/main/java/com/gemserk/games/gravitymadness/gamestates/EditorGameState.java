package com.gemserk.games.gravitymadness.gamestates;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.commons.gdx.graphics.ImmediateModeRendererUtils;
import com.gemserk.componentsengine.input.ButtonMonitor;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.componentsengine.utils.ParametersWrapper;
import com.gemserk.resources.ResourceManager;

public class EditorGameState extends GameStateImpl {

	List<Rectangle> rectangles = new ArrayList<Rectangle>();
	ResourceManager<String> resourceManager;
	Rectangle rectangle;
	String label = "hola";
	private BitmapFont font;

	SpriteBatch spriteBatch = new SpriteBatch();

	@Override
	public void init() {
		font = resourceManager.getResourceValue("FpsFont");
		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();
		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				
				monitorKey("dump", Keys.D);
			}
		};
		
		dumpButton = inputDevicesMonitor.getButton("dump");
	}

	int frame = 0;
	int frameClick = 0;
	private InputDevicesMonitorImpl<String> inputDevicesMonitor;
	private ButtonMonitor dumpButton;

	@Override
	public void update() {
		inputDevicesMonitor.update();
		frame++;
		Input input = Gdx.input;
		int inputX = input.getX();
		int inputY = Gdx.graphics.getHeight() - input.getY();
		label = "(" + inputX + "," + inputY + ")";
		if (input.isTouched() && input.justTouched()) {
			System.out.println("TOUCHED " + frame);
			if (rectangle == null) {
				if (frame - frameClick > 10) {
					rectangle = new Rectangle(inputX, inputY, 0, 0);
					System.out.println("FirstClick " + frame + ": " + inputX + " " + inputY);
					frameClick = frame;
				}
			} else {
				if (frame - frameClick > 10) {

					System.out.println("SecondClick " + frame + ": " + inputX + " " + inputY);
					rectangle.set(rectangle.x, rectangle.y, inputX - rectangle.x, inputY - rectangle.y);
					if(rectangle.width<0){
						rectangle.width = -rectangle.width;
						rectangle.x = rectangle.x - rectangle.width;
					}
					if(rectangle.height<0){
						rectangle.height = -rectangle.height;
						rectangle.y = rectangle.y - rectangle.height;
					}
					rectangles.add(rectangle);
					System.out.println("Rectangle: " + rectangle.toString());
					rectangle = null;
					frameClick = frame;
				}
			}
		}
		if(dumpButton.isPressed()){
			System.out.println("DUMP");
			for (Rectangle rect : rectangles) {
				float zoom = 100;
				float xscaled = rect.x / zoom;
				float yscaled = rect.y / zoom;
				float widthScaled = rect.width / zoom;
				float heightScaled = rect.height / zoom;
				
				System.out.println(String.format("entityFactory.instantiate(entityTemplates.groundTemplate,new ParametersWrapper().put(\"spatial\", new SpatialImpl(%ff,%ff,%ff,%ff,0)));", xscaled,yscaled,widthScaled,heightScaled));
				
			}
			System.out.println("ENDDUMP");
		}
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		spriteBatch.begin();
		font.draw(spriteBatch, label, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.9f);
		spriteBatch.end();
		if (rectangle != null) {
			ImmediateModeRendererUtils.drawSolidCircle(rectangle.x, rectangle.y, 10, Color.GREEN);
		}
		for (Rectangle rect : rectangles) {
			ImmediateModeRendererUtils.drawRectangle(rect, Color.RED);
		}
	}

	public void setResourceManager(ResourceManager<String> resourceManager) {
		this.resourceManager = resourceManager;
	}
}
