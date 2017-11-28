uniform mat4 u_MVPMatrix;		// A constant representing the combined model/view/projection matrix.      		       
uniform mat4 u_MVMatrix;		// A constant representing the combined model/view matrix.

attribute vec4 a_Position;		// Per-vertex position information we will pass in.   				
attribute vec4 a_Color;			// Per-vertex color information we will pass in. 				
attribute vec3 a_Normal;		// Per-vertex normal information we will pass in.

uniform vec4 u_AmbientColor;
		  
uniform vec3 u_LightPos1;
uniform vec3 u_LightPos2;
uniform vec3 u_LightPos3;

uniform vec4 u_LightColor1;
uniform vec4 u_LightColor2;
uniform vec4 u_LightColor3;

uniform float u_LightFade1;
uniform float u_LightFade2;
uniform float u_LightFade3;

varying vec4 v_Color;
		  			
// The entry point for our vertex shader.  
void main()                                                 	
{                                                         
	  vec3 modelViewVertex = vec3(u_MVMatrix * a_Position);
	  vec3 modelViewNormal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));

      vec3 lightVector1 = normalize(u_LightPos1 - modelViewVertex);
      vec3 lightVector2 = normalize(u_LightPos2 - modelViewVertex);
      
      float diffuse1 = max(dot(modelViewNormal, lightVector1), 0.0);               	  		  													  
      float diffuse2 = max(dot(modelViewNormal, lightVector2), 0.0);

      vec4 lightColor1 = u_LightColor1 * pow(diffuse1, u_LightFade1);
      vec4 lightColor2 = u_LightColor2 * pow(diffuse2, u_LightFade2);

	  float oAlpha = a_Color.a;
	  v_Color = (a_Color * (lightColor1 + lightColor2) * u_AmbientColor);
	  v_Color.a = oAlpha;
	  
	  gl_Position = u_MVPMatrix * a_Position;                    		  
}
