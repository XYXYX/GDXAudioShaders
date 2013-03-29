uniform mat4 u_worldView;

attribute vec4 vPosition;
attribute vec2 vRes;
attribute vec3 aLevels;
attribute float a_angle;

varying mat4 v_rotationMatrix;
varying vec3 audioLevels;
varying vec2 uv;
varying vec2 uuv;


void main()
{
	gl_Position = u_worldView * vPosition;
	uv = gl_Position.xy;
	uuv = vRes;
	audioLevels = aLevels;
	
	float cos = cos(a_angle);
    float sin = sin(a_angle);
    mat4 transInMat = mat4(1.0, 0.0, 0.0, 0.0,
                           0.0, 1.0, 0.0, 0.0,
                           0.0, 0.0, 1.0, 0.0,
                           0.5, 0.5, 0.0, 1.0);
    mat4 rotMat = mat4(cos, -sin, 0.0, 0.0,
                       sin, cos, 0.0, 0.0,
                       0.0, 0.0, 1.0, 0.0,
                       0.0, 0.0, 0.0, 1.0);
    mat4 resultMat = transInMat * rotMat;
    resultMat[3][0] = resultMat[3][0] + resultMat[0][0] * -0.5 + resultMat[1][0] * -0.5;
    resultMat[3][1] = resultMat[3][1] + resultMat[0][1] * -0.5 + resultMat[1][1] * -0.5;
    resultMat[3][2] = resultMat[3][2] + resultMat[0][2] * -0.5 + resultMat[1][2] * -0.5;
    v_rotationMatrix = resultMat;
    //gl_Position = gl_Position * v_rotationMatrix;
    
}	