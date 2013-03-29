package com.xyx.gdxaudioshaders;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;

public class GDXAudioShaders implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Texture texture;
	private Sprite sprite;
	
	Mesh surface;
	ShaderProgram waveform;
	AudioData audio;
	Matrix4 rotationMatrix;
	
	@Override
	public void create() {		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(1, h/w);
		batch = new SpriteBatch();
		
		texture = new Texture(Gdx.files.internal("data/libgdx.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		TextureRegion region = new TextureRegion(texture, 0, 0, 512, 275);
		
		sprite = new Sprite(region);
		sprite.setSize(0.9f, 0.9f * sprite.getHeight() / sprite.getWidth());
		sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
		sprite.setPosition(-sprite.getWidth()/2, -sprite.getHeight()/2);
		
		waveform = compileShader(waveform, Gdx.files.internal("data/shaders/waveform.vert"),  Gdx.files.internal("data/shaders/waveform.frag"));
		surface = new Mesh(true, 4, 0, 
                new VertexAttribute(Usage.Position, 3, "vPosition"));
		surface.setVertices(new float[] {
                -1f, -1f, 0,       // bottom left
                1f, -1f, 0,       // bottom right
                1f, 1f, 0,         // top right
                -1f, 1f, 0});     // top left
		
		audio = new AudioData();
	}

	@Override
	public void dispose() {
		batch.dispose();
		texture.dispose();
	}

	@Override
	public void render() {		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		sprite.draw(batch);
		batch.end();
		
		audio.audioToTexture();
		GL20 gl = Gdx.graphics.getGL20();
	    //we don't necessarily need these, but its good practice to enable
	    //the things we need. we enable 2d textures and set the active one
	    //to 0. we could have multiple textures but we don't need it here.
	    gl.glEnable(GL20.GL_TEXTURE_2D);
	    gl.glActiveTexture(GL20.GL_TEXTURE0);
	    rotationMatrix = new Matrix4();
	    
	    waveform.begin();
	    waveform.setAttributef("vRes", Gdx.graphics.getWidth(),Gdx.graphics.getHeight(),0f,0f);
	    waveform.setAttributef("aLevels", audio.bass,audio.mid,audio.treb,0f);
	    waveform.setUniformMatrix("u_worldView", rotationMatrix);
	    waveform.setAttributef("a_angle", 0,0,0,0);
	    surface.render(waveform, Gdx.gl20.GL_TRIANGLE_FAN);
	    waveform.end();
	}
	
	float map(float x, float in_min, float in_max, float out_min, float out_max)
	{
	  return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPILE SHADERS
	public ShaderProgram compileShader(ShaderProgram prog, String vert, String frag) {
	Gdx.app.log("Shader: ", "compiling...");
	prog = new ShaderProgram(vert,frag);
	if (prog.isCompiled() == false) {
	Gdx.app.log("Shader: ", "ERROR!: " + prog.getLog());
	System.exit(0);
	prog = null;
	}else{
	
	}
	return prog;
	}
	public ShaderProgram compileShader(ShaderProgram prog, FileHandle vert, FileHandle frag) {
	Gdx.app.log("Shader: ", "compiling...");
	prog = new ShaderProgram(vert,frag);
	if (prog.isCompiled() == false) {
	Gdx.app.log("Shader: ", "ERROR!: " + prog.getLog());
	System.exit(0);
	prog = null;
	}else{
	
	}
	return prog;
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////
}
