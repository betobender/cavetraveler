package com.cave.traveler.engine.Graphics;


public class NormalShader extends PerPixelShader {

	public NormalShader(String name, ResourceMap resourceMap) {
		super(name, resourceMap, "vertex_shader", "fragment_shader");
	}
}
