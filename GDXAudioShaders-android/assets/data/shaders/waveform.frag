#ifdef GL_ES
precision mediump float;
#endif
uniform sampler2D texture;		
uniform vec2 res;		
varying vec2 uv; 
varying vec2 uuv; 
varying vec3 audioLevels;
varying mat4 v_rotationMatrix;

void main()	
{
	uv.xy = gl_FragCoord.xy / uuv.xy;
		//uv.xyz = gl_FragCoord.xyz / vec3(uuv.x,uuv.y,1);
	
	//float fft = texture2D(texture, vec2(uv.x, 0.25)).x;
	//float wave = texture2D(texture, vec2(uv.x, 0.75)).x;
	
	float fft = texture2D(texture, vec2(uv.x, 0.25)).x;
	float wave = texture2D(texture, vec2(uv.x, 0.75)).x;
	vec3 col = vec3( fft, 4.0*fft*(1.0-fft), 1.0-fft ) * fft;
	vec3 col2 = col;
	//col += 1.0 -  smoothstep( 0.0, 0.15, abs(wave - uv.y) );
	col += 1.0 -  smoothstep( 0.0, 0.15, abs(wave - uv.y) );
	//col.x -= (col2.x * (5.5*(audioLevels.x*fft)));
	//col.y -= (col2.y * (5.5*(audioLevels.y*fft)));
	//col.z -= (col2.z * (5.5*(audioLevels.z*fft)));
	gl_FragColor = vec4(col, 1.0);
}