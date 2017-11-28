package com.cave.traveler.model;

import java.io.IOException;

import com.cave.traveler.engine.Graphics.ResourceMap;

public class ModelFactory {
	private static ModelFactory instance = new ModelFactory();

	private ModelFactory() {

	}

	public static ModelFactory getInstance() {
		return instance;
	}

	public Model createModelFromResource(ResourceMap resourceMap, String resourceName) throws IOException {
		ModelParser parser = new ModelParser();
		Model model = new Model();
		parser.parseWavefrontObjectFile(resourceMap, resourceName, model);
		return model;
	}
}
