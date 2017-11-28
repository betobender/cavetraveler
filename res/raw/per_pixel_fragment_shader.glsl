precision mediump float;       	// Set the default precision to medium. We don't need as high of a 
								// precision in the fragment shader.

uniform sampler2D u_Texture;

  
varying vec4 v_Color;          	// This is the color from the vertex shader interpolated across the 

// The entry point for our fragment shader.
void main()                    		
{
    gl_FragColor = v_Color;
}                                                                     	

