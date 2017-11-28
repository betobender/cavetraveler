package com.cave.traveler.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.cave.traveler.engine.Graphics.ResourceMap;

public class ModelParser {

	public Model parseWavefrontObjectFile(ResourceMap resourceMap, String resourceName, Model model) throws IOException {

		InputStream file = resourceMap.getResourceContext().getResources().openRawResource(resourceMap.getResourceByName(resourceName));
		InputStreamReader isr = new InputStreamReader(file);
		BufferedReader br = new BufferedReader(isr);

		String line = null;
		ModelObject modelObject = null;
		ModelMaterial modelMaterial = null;
		ModelFace modelFace = null;

		int vertexIndex = 1;
		int normalIndex = 1;

		while ((line = br.readLine()) != null) {
			String[] split = line.split(" ");
			String command = split[0].trim();

			if (command.startsWith("#")) {
				continue; // skip comment
			}

			if (command.equals("s")) {
				continue; // skip smooth
			}

			if (command.equals("o")) {
				modelObject = new ModelObject(split[1].trim());
				model.addModelObject(modelObject);
				continue;
			}

			if (command.equals("v")) {
				modelObject.addVertex(vertexIndex++, Float.parseFloat(split[1].trim()), Float.parseFloat(split[2].trim()), Float.parseFloat(split[3].trim()));
				continue;
			}

			if (command.equals("vn")) {
				modelObject.addNormal(normalIndex++, Float.parseFloat(split[1].trim()), Float.parseFloat(split[2].trim()), Float.parseFloat(split[3].trim()));
				continue;
			}

			if (command.equals("vt")) {
				// TODO
				// if(split.length)
				// modelObject.addTextureVertex()
			}

			if (command.equals("f")) {

				String[] aSplit = split[1].split("/");
				String[] bSplit = split[2].split("/");
				String[] cSplit = split[3].split("/");

				modelFace.addTriangle(
						Integer.parseInt(aSplit[0]), Integer.parseInt(aSplit[2]), 
						Integer.parseInt(bSplit[0]), Integer.parseInt(bSplit[2]),
						Integer.parseInt(cSplit[0]), Integer.parseInt(cSplit[2]));

				continue;
			}

			if (command.equals("mtllib")) {
				parseWavefrontMaterialFile(resourceMap, split[1].trim());
				continue;
			}

			if (command.equals("usemtl")) {
				modelMaterial = getMaterialByName(split[1].trim());
				modelFace = new ModelFace(modelMaterial);
				modelObject.addModelFace(modelFace);
				vertexIndex = 1;
				normalIndex = 1;
				continue;
			}
		}

		return model;
	}

	private ModelMaterial getMaterialByName(String name) {
		return modelMaterials.get(name);
	}

	Map<String, ModelMaterial> modelMaterials = new HashMap<String, ModelMaterial>();

	private void parseWavefrontMaterialFile(ResourceMap resourceMap, String resourceName) throws IOException {

		modelMaterials.clear();

		InputStream file = resourceMap.getResourceContext().getResources().openRawResource(resourceMap.getResourceByName(resourceName));
		InputStreamReader isr = new InputStreamReader(file);
		BufferedReader br = new BufferedReader(isr);

		String line = null;
		ModelMaterial modelMaterial = null;

		while ((line = br.readLine()) != null) {
			String[] split = line.split(" ");
			String command = split[0].trim();

			if (command.startsWith("#")) {
				continue; // skip comment
			}

			if (command.equals("newmtl")) {
				modelMaterial = new ModelMaterial(split[1].trim());
				modelMaterials.put(modelMaterial.getName(), modelMaterial);
				continue;
			}

			if (command.equals("Ka")) {
				modelMaterial.setAmbient(Float.parseFloat(split[1].trim()), Float.parseFloat(split[2].trim()), Float.parseFloat(split[3].trim()));
				continue;
			}

			if (command.equals("Kd")) {
				modelMaterial.setDifuse(Float.parseFloat(split[1].trim()), Float.parseFloat(split[2].trim()), Float.parseFloat(split[3].trim()));
				continue;
			}

			if (command.equals("Ks")) {
				modelMaterial.setSpecular(Float.parseFloat(split[1].trim()), Float.parseFloat(split[2].trim()), Float.parseFloat(split[3].trim()));
				continue;
			}

			if (command.equals("d") || command.startsWith("Tr")) {
				modelMaterial.setTransparent(Float.parseFloat(split[1].trim()));
				continue;
			}

			if (command.equals("map_Kd")) {
				modelMaterial.setDifuseTexture(new ModelTexture(split[1].trim()));
			}
		}
	}
}
