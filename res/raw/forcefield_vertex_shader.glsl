uniform mat4 u_MVPMatrix;      		       
uniform mat4 u_MVMatrix;
uniform float u_Time;       		
		  			
attribute vec4 a_Position;   				
varying vec4 v_Position;    	
		  
void main()                                                 	
{
	v_Position = a_Position;
	gl_Position = u_MVPMatrix * a_Position;                       		  
}                                                          