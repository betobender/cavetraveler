uniform mat4 u_MVPMatrix;		// A constant representing the combined model/view/projection matrix.      		       
uniform mat4 u_MVMatrix;		// A constant representing the combined model/view matrix.       		
		  			
attribute vec4 a_Position;		// Per-vertex position information we will pass in.   				
attribute vec4 a_Color;			// Per-vertex color information we will pass in. 				
attribute vec3 a_Normal;		// Per-vertex normal information we will pass in.
		  
varying vec4 v_Color;			// This will be passed into the fragment shader.          		

uniform vec3 u_LightPos1;
uniform vec3 u_LightPos2;

uniform vec4 u_LightColor1;
uniform vec4 u_LightColor2;

uniform float u_LightFade1;
uniform float u_LightFade2;

		  
// The entry point for our vertex shader.  
void main()                                                 	
{                                                         
	// Pass through the color.
	v_Color = a_Color;
	
	vec3 position = vec3(u_MVMatrix * a_Position); 
	
    vec3 lightVector1 = normalize(u_LightPos1 - vec3(position));              	
    vec3 lightVector2 = normalize(u_LightPos2 - vec3(position));
    
    vec3 normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));
    
    float diffuse1 = max(dot(normal, lightVector1), 0.02);               	  		  													  
    float diffuse2 = max(dot(normal, lightVector2), 0.02);
    
    vec4 lightColor1 = u_LightColor1 * pow(diffuse1, u_LightFade1);
    vec4 lightColor2 = u_LightColor2 * pow(diffuse2, u_LightFade2);    
    
	v_Color = (a_Color * (lightColor1 + lightColor2));
    v_Color.a = a_Color.a;    
          
	// gl_Position is a special variable used to store the final position.
	// Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
	gl_Position = u_MVPMatrix * a_Position;                       		  
}                                                          