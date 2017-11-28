precision mediump float;
uniform float u_Time; 
varying vec4 v_Position;

#define TIMEADJUST 1.0
#define POSITIONADJUSTX 45.0
#define POSITIONADJUSTY 90.0

void main()                    		
{
	float i = 0.5 + (sin(v_Position.y * POSITIONADJUSTY + u_Time*-TIMEADJUST) * 
	                 cos(v_Position.x * POSITIONADJUSTX + u_Time* TIMEADJUST)) / 2.0;
	vec4 color = vec4(1.0, 0.0, 0.0, 0.1);
	gl_FragColor = color * i;
}                                                                     	

