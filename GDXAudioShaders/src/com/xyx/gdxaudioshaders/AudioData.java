package com.xyx.gdxaudioshaders;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.analysis.KissFFT;
import com.badlogic.gdx.audio.io.Mpg123Decoder;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class AudioData {
	Pixmap audioPixmap;
	Texture audioTexture;
	Matrix4 rotationMatrix;
	
	String fileToPlay = "data/audio/test.mp3";
	Mpg123Decoder decoder;
	AudioDevice device;
	KissFFT fft;
	
	short[] samples = new short[2048];
	float[] spectrum = new float[2048];
	float[] maxValues = new float[2048];
	float[] topValues = new float[2048];
	float[] samples512 = new float[512];
	float[] maxes = new float[512];
	float[] mins = new float[512];
	
	int sampind = 0;
	int numInGroupd = 4; //2048/512;
	boolean playing = false;
	float wavemax  = 0f;
	public float bass = 0f;
	public float mid = 0f;
	public float treb = 0f;
	Vector3 randomCol;
	
	public AudioData (){
		// fast fourier transform
		fft = new KissFFT(2048);
		for (int i = 0; i < maxValues.length; i++) {
			maxValues[i] = 0;
			topValues[i] = 0;
			if(i < 512) { samples512[i] = 0f; maxes[i] = 0f; mins[i] = 0f;}
		}
		audioPixmap  = new Pixmap( 512,2, Format.RGBA8888 );
		audioTexture = new Texture(audioPixmap,true);
		randomCol = new Vector3((float)Math.random(),(float)Math.random(),(float)Math.random());


		// the audio file has to be on the external storage (not in the assets)
		FileHandle externalFile = Gdx.files.external("tmp/audio-spectrum.mp3");
		Gdx.files.internal(fileToPlay).copyTo(externalFile);

		// create the decoder (you can use a VorbisDecoder if you want to read
		// ogg files)
		decoder = new Mpg123Decoder(externalFile);

		// Create an audio device for playback
		device = Gdx.audio.newAudioDevice(decoder.getRate(),
				decoder.getChannels() == 1 ? true : false);
		
		
		// start a thread for playback</pre>
		Thread playbackThread = new Thread(new Runnable() {
		 @Override
		 public void run() {
			 int readSamples = 0;
			 
			 // read until we reach the end of the file
			 while(playing && (readSamples = decoder.readSamples(samples, 0, samples.length)) > 0) {
			 // get audio spectrum
			 fft.spectrum(samples, spectrum);
			 sampind++;
			 for(int i = 0; i < 512; i++) samples512[i] = 0f;
			 for(int i = 0; i < 512;) {
				 samples512[i] = samples[2*i];
				 samples512[i] += samples[(2*i)+1];
				 samples512[i] += samples[(2*i)+1024];
				 samples512[i] += samples[(2*i)+1025];
				 if(samples512[i] > maxes[i]) maxes[i] = samples512[i];
				 if(samples512[i] < mins[i]) mins[i] = samples512[i];
				 samples512[i] = map(samples512[i],mins[i],maxes[i],0f,1f);
				 i++;
			 }
			 
			 //For comparison with minim values
			 //System.out.println("SAMPLES ARRAY ::::: INDEX: " + sampind);
			 //System.out.println(Arrays.toString(samples512));
			 
			 // write the samples to the AudioDevice
			 device.writeSamples(samples, 0, readSamples);
			 }
			 }
		 });
		 playbackThread.setDaemon(true);
		 playbackThread.start();
		 playing = true;	
	}
	
	public void audioToTexture() {
		if(Gdx.input.justTouched())		randomCol = new Vector3((float)Math.random(),(float)Math.random(),(float)Math.random());

		bass = 0f;
		mid = 0f;
		treb = 0f;
		
		for(int i1 = 0; i1 < 512; i1++) {
			if(samples512[i1]>maxes[i1]) maxes[i1] = samples512[i1];
			for(int i2 = 0; i2 < 2; i2++) {
				if(i2 == 0) {
					int audioind = i1+i2;
					if(audioind < 204) bass += (samples512[i1]);
					if(audioind >= 204 && audioind < 358) mid += (samples512[i1]);
					if(audioind >= 358) treb += (samples512[i1]);
					
					float c = (float)Math.abs((samples512[i1]));
					if(c > maxes[i1]) maxes[i1] = c;
					c = 1f/maxes[i1] * c;
					c = MathUtils.clamp(c, 0f, 1f);
					c = map(samples512[i1],0f,1f,0f,1f);
					//pix.setColor((float)Math.abs(Math.sin(samples512[i1+i2))), (float)Math.abs(Math.cos(samples512[i1+i2))), (float)Math.abs(Math.tan(samples512[i1+i2))), 1f);
					audioPixmap.setColor((float)Math.sin(c*randomCol.x),(float)Math.cos(c*randomCol.y),(float)Math.tan(c*randomCol.z), 1f);
					audioPixmap.setColor(c,0f,1f, c);
					audioPixmap.setColor((float)Math.sin(c*randomCol.x),(float)Math.cos(c*randomCol.y),(float)Math.tan(c*randomCol.z), c);


					audioPixmap.drawPixel(i1,0);
				}else{
					float wv = samples512[i1];//(float)audio.audioPlayer.right.get(i1)+(float)audio.audioPlayer.left.get(i1);
					if(wv > wavemax) wavemax = wv;
					wv = 1f/wavemax * wv;
					//wv = audio.fftLog.getAvg(avgcount-1);// audio.audioPlayer.mix.level();
					//if(i1 > avgrange*avgcount) avgcount++;
					
					//float wave = map(samples512[i1],-1f,1f,0f,1f);
					float wave = map(samples512[i1],-1f,1f,-1f,1f);
					
					//wv = MathUtils.clamp(wv, 0f, 1f);
					audioPixmap.setColor(wave,wave,wave,1f);//(float)audio.audioPlayer.left.get(i1), 1f);
					audioPixmap.drawPixel(i1,1);
				}
				
				textureBind(0);
			}
		}
	}
	
	public void textureBind(int loc) {
		audioTexture.draw(audioPixmap, 0, 0);
		audioTexture.bind(0);
	}
	
	float map(float x, float in_min, float in_max, float out_min, float out_max)
	{
	  return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}
	
	
}
