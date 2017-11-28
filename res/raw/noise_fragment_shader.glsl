precision mediump float; 
uniform float u_Seed;
uniform vec4 u_NoiseColor;

float rand(vec2 n)
{
	return 0.5 + 0.5 * fract(sin(dot(n.xy + u_Seed, vec2(12.9898, 78.233)))* 43758.5453);
}
  
void main()                    		
{
	gl_FragColor = u_NoiseColor;
}                                                                     	

