package com.cave.traveler.engine;

import java.math.BigInteger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import android.content.Context;

import com.cave.traveler.engine.Graphics.DrawLayer;
import com.cave.traveler.engine.Graphics.Effect;
import com.cave.traveler.engine.Graphics.Point3D;
import com.cave.traveler.engine.Graphics.Texture;
import com.cave.traveler.engine.Graphics.TextureMap;
import com.cave.traveler.engine.setters.Setter;
import com.cave.traveler.engine.setters.SetterFactory;
import com.cave.traveler.engine.triggers.Trigger;
import com.cave.traveler.objects.GameObjectFactory;

public class Loader {
	private static Loader instance = new Loader();

	private Loader() {
	}

	public static Loader getInstance() {
		return instance;
	}

	public World createWorldFromXml(Context context, int resourceId, String worldName) throws LoaderException {

		try {

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(context.getResources().openRawResource(resourceId));
			doc.getDocumentElement().normalize();

			for (int a = 0; a < doc.getChildNodes().getLength(); ++a) {
				if (doc.getChildNodes().item(a).getNodeName().equals("Engine")) {
					Node engineNode = doc.getChildNodes().item(a);
					for (int b = 0; b < engineNode.getChildNodes().getLength(); ++b) {
						if (engineNode.getChildNodes().item(b).getNodeName().equals("Worlds")) {
							Node worldsNode = engineNode.getChildNodes().item(b);
							for (int i = 0; i < worldsNode.getChildNodes().getLength(); ++i) {
								Node worldNode = worldsNode.getChildNodes().item(i);
								if (worldNode.getNodeName().equals("World") && attributeToString(worldNode, "name", "").equals(worldName)) {
									World world = new World();
									parseWorld(worldNode, world);
									return world;
								}
							}
						}
					}
				}
			}

			return null;

		} catch (Exception e) {
			throw new LoaderException(e.getMessage());
		}
	}

	private String attributeToString(Node node, String name, String def) {
		String res = null;
		try {
			res = node.getAttributes().getNamedItem(name).getNodeValue();
		} catch (Exception e) {
		}
		if (res == null)
			return def;
		return res;
	}

	private int attributeToInteger(Node node, String name, int def) {
		String res = attributeToString(node, name, null);
		if (res == null)
			return def;
		return Integer.parseInt(res);
	}

	private boolean attributeToBoolean(Node node, String name, boolean def) {
		String res = attributeToString(node, name, null);
		if (res == null)
			return def;
		return Boolean.parseBoolean(res);
	}

	private float attributeToFloat(Node node, String name, float def) {
		String res = attributeToString(node, name, null);
		if (res == null)
			return def;
		return Float.parseFloat(res);
	}

	private float[] attributeToColor(Node node, String name, String def) {
		String res = attributeToString(node, name, null);
		if (res == null)
			return ConvertStringToColor(def);
		return ConvertStringToColor(res);
	}

	private float[] ConvertStringToColor(String res) {
		float a = (new BigInteger(res.substring(0, 2), 16).intValue()) / 255f;
		float r = (new BigInteger(res.substring(2, 4), 16).intValue()) / 255f;
		float g = (new BigInteger(res.substring(4, 6), 16).intValue()) / 255f;
		float b = (new BigInteger(res.substring(6, 8), 16).intValue()) / 255f;
		return new float[] { r, g, b, a };
	}

	private Point3D attributeToPoint(Node node, String name, Point3D def) {
		String res = attributeToString(node, name, null);
		if (res == null)
			return def;

		float p[] = new float[3];
		int index = 0;
		for (String s : res.split(","))
			p[index++] = Float.parseFloat(s.trim());

		return new Point3D(p);
	}

	private void parseWorld(Node worldNode, World world) {

		world.setName(attributeToString(worldNode, "name", ""));

		for (int i = 0; i < worldNode.getChildNodes().getLength(); ++i) {

			Node node = worldNode.getChildNodes().item(i);
			if (node.getNodeName().equals("Forces"))
				parseForces(node, world);
			else if (node.getNodeName().equals("GameObjects"))
				parseGameObjects(node, world);
			else if (node.getNodeName().equals("Layers"))
				parseLayers(node, world);
			else if (node.getNodeName().equals("TextureMap"))
				parseTextureMap(node, world);
		}
	}

	private void parseTextureMap(Node textureMapNode, World world) {
		TextureMap textureMap = new TextureMap();

		for (int i = 0; i < textureMapNode.getChildNodes().getLength(); ++i) {
			Node textureNode = textureMapNode.getChildNodes().item(i);
			if (textureNode.getNodeName().equals("Texture")) {
				String name = attributeToString(textureNode, "name", null);
				String bitmap = attributeToString(textureNode, "bitmap", null);
				String text = attributeToString(textureNode, "text", null);
				Texture texture = null;
				
				if(bitmap != null) {
					texture = Texture.createFromBitmap(name, bitmap);
				} else {
					int textSize = attributeToInteger(textureNode, "textSize", 256);
					int textWidth = attributeToInteger(textureNode, "textWidth", 256);
					int textHeight = attributeToInteger(textureNode, "textHeight", 256);
					texture = Texture.createFromText(name, text, textSize, textWidth, textHeight);
				}
				
				
				texture.setWidth(attributeToFloat(textureNode, "width", 1f));
				texture.setHeight(attributeToFloat(textureNode, "height", 1f));
				textureMap.addTexture(texture);
			}
		}

		world.setTextureMap(textureMap);
	}

	private void parseGameObjects(Node gameObjectsNode, World world) {
		for (int i = 0; i < gameObjectsNode.getChildNodes().getLength(); ++i) {
			Node gameObjectNode = gameObjectsNode.getChildNodes().item(i);

			if (gameObjectNode.getNodeName().equals("GameObject")) {

				String type = attributeToString(gameObjectNode, "type", null);
				GameObject obj = GameObjectFactory.getInstance().createFromType(type, world);

				obj.setName(attributeToString(gameObjectNode, "name", obj.getName()));
				obj.setLayerId(attributeToInteger(gameObjectNode, "layerId", obj.getLayerId()));
				obj.setPlayable(attributeToBoolean(gameObjectNode, "playable", obj.isPlayable()));
				obj.setDynamic(attributeToBoolean(gameObjectNode, "dynamic", obj.isDynamic()));
				obj.setCollidable(attributeToBoolean(gameObjectNode, "collidable", obj.isCollidable()));
				obj.setInitialXPosition(attributeToFloat(gameObjectNode, "initialXPosition", obj.getInitialXPosition()));
				obj.setInitialYPosition(attributeToFloat(gameObjectNode, "initialYPosition", obj.getInitialYPosition()));
				obj.setInitialXSpeed(attributeToFloat(gameObjectNode, "initialXSpeed", obj.getInitialXSpeed()));
				obj.setInitialYSpeed(attributeToFloat(gameObjectNode, "initialYSpeed", obj.getInitialYSpeed()));
				obj.setInitialState(attributeToString(gameObjectNode, "initialState", obj.getInitialState()));
				obj.setAttachedToCamera(attributeToBoolean(gameObjectNode, "attachedToCamera", obj.isAttachedToCamera()));

				for (int j = 0; j < gameObjectNode.getChildNodes().getLength(); ++j) {
					Node innerNode = gameObjectNode.getChildNodes().item(j);
					if (innerNode.getNodeName().equals("Body")) {
						Body body = new Body();

						body.setScaleX(attributeToFloat(innerNode, "scaleX", 0));
						body.setScaleY(attributeToFloat(innerNode, "scaleY", 0));
						body.setScaleZ(attributeToFloat(innerNode, "scaleZ", 0));

						obj.setBody(body);
					} else if (innerNode.getNodeName().equals("PhysicBody")) {
						PhysicBody physicBody = new PhysicBody(attributeToString(innerNode, "type", null), attributeToInteger(innerNode, "size", 0));

						int p = 0;
						for (int z = 0; z < innerNode.getChildNodes().getLength(); ++z) {
							Node pointNode = innerNode.getChildNodes().item(z);
							if (pointNode.getNodeName().equals("Point"))
								physicBody.getPoints()[p++] = new float[] { attributeToFloat(pointNode, "x", 0f), attributeToFloat(pointNode, "y", 0f) };
						}

						obj.setPhysicBody(physicBody);
					} else if (innerNode.getNodeName().equals("Triggers")) {
						parseTriggers(innerNode, obj);
					} else if (innerNode.getNodeName().equals("States")) {
						parseStates(innerNode, obj);
					}

				}

				world.addGameObject(obj);
			}
		}
	}

	private void parseStates(Node statesNode, GameObject obj) {
		for (int i = 0; i < statesNode.getChildNodes().getLength(); ++i) {
			Node stateNode = statesNode.getChildNodes().item(i);
			if (stateNode.getNodeName().equals("State")) {
				State state = new State();
				state.setState(attributeToString(stateNode, "state", null));
				state.setNextState(attributeToString(stateNode, "nextState", null));
				state.setDuration(attributeToFloat(stateNode, "duration", 0));
				state.setXPosition(attributeToFloat(stateNode, "xPosition", 0));
				state.setCopyEmitters(attributeToBoolean(stateNode, "copyEmitters", false));
				state.setCopySetters(attributeToBoolean(stateNode, "copySetters", false));
				state.setTextureName(attributeToString(stateNode, "texture", null));

				for (int j = 0; j < stateNode.getChildNodes().getLength(); ++j) {
					Node node = stateNode.getChildNodes().item(j);
					if (node.getNodeName().equals("Setters")) {
						parseSetters(node, state);
					} else if (node.getNodeName().equals("Emitters")) {
						parseEmitters(node, state);
					} else if (node.getNodeName().equals("Effects")) {
						parseEffects(node, state);
					} else if (node.getNodeName().equals("Camera")) {
						parseCamera(node, state);
					}
				}

				obj.addState(state);
			}
		}

	}

	private void parseCamera(Node cameraNode, State state) {
		if (cameraNode.getNodeName().equals("Camera")) {
			Camera camera = new Camera();
			camera.setDuration(attributeToFloat(cameraNode, "duration", 0));
			camera.setFrom(attributeToPoint(cameraNode, "from", null));
			camera.setTo(attributeToPoint(cameraNode, "to", null));
			camera.setLookFrom(attributeToPoint(cameraNode, "lookFrom", null));
			camera.setLookTo(attributeToPoint(cameraNode, "lookTo", null));
			camera.setRelativeTo(attributeToString(cameraNode, "relativeTo", null));
			state.setCamera(camera);
		}
	}

	private void parseEffects(Node effectsNode, State state) {
		for (int i = 0; i < effectsNode.getChildNodes().getLength(); ++i) {
			Node effetctNode = effectsNode.getChildNodes().item(i);
			if (effetctNode.getNodeName().equals("Effect")) {
				Effect e = new Effect();
				//e.setColor(attributeToColor(effetctNode, "color", 0));
				e.setFromSize(attributeToFloat(effetctNode, "fromSize", 0));
				e.setToSize(attributeToFloat(effetctNode, "toSize", 0));
				e.setType(attributeToString(effetctNode, "type", null));

				state.addEffect(e);
			}
		}
	}

	private void parseEmitters(Node emittersNode, State state) {
		for (int i = 0; i < emittersNode.getChildNodes().getLength(); ++i) {
			Node emitterNode = emittersNode.getChildNodes().item(i);
			if (emitterNode.getNodeName().equals("Emitter")) {
				ParticleEmitter particleEmitter = new ParticleEmitter();
				particleEmitter.setParticleType(attributeToString(emitterNode, "particleType", null));
				particleEmitter.setFromDuration(attributeToFloat(emitterNode, "fromDuration", 0));
				particleEmitter.setToDuration(attributeToFloat(emitterNode, "toDuration", 0));
				particleEmitter.setxPosition(attributeToFloat(emitterNode, "xPosition", 0));
				particleEmitter.setyPosition(attributeToFloat(emitterNode, "yPosition", 0));
				particleEmitter.setInterval(attributeToFloat(emitterNode, "interval", 0));
				particleEmitter.setPositionVariation(attributeToFloat(emitterNode, "positionVariation", 0));
				particleEmitter.setTextureName(attributeToString(emitterNode, "textureName", null));
				particleEmitter.setFromSize(attributeToFloat(emitterNode, "fromSize", 1));
				particleEmitter.setToSize(attributeToFloat(emitterNode, "toSize", 1));
				particleEmitter.setParticles(attributeToInteger(emitterNode, "particles", 1));
				particleEmitter.setLimit(attributeToInteger(emitterNode, "limit", 0));
				particleEmitter.setFromAlpha(attributeToFloat(emitterNode, "fromAlpha", 1));
				particleEmitter.setToAlpha(attributeToFloat(emitterNode, "toAlpha", 1));
				particleEmitter.setxSpeed(attributeToFloat(emitterNode, "xSpeed", 0));
				particleEmitter.setySpeed(attributeToFloat(emitterNode, "ySpeed", 0));
				particleEmitter.setxAcceleration(attributeToFloat(emitterNode, "xAcceleration", 0));
				particleEmitter.setyAcceleration(attributeToFloat(emitterNode, "yAcceleration", 0));
				particleEmitter.setSpeedVariation(attributeToFloat(emitterNode, "speedVariation", 0));
				particleEmitter.setAccelerationVariation(attributeToFloat(emitterNode, "accelerationVariation", 0));
				particleEmitter.setRotation(attributeToFloat(emitterNode, "rotation", particleEmitter.getRotation()));
				particleEmitter.setRotationSpeed(attributeToFloat(emitterNode, "rotationSpeed", particleEmitter.getRotationSpeed()));
				particleEmitter.setRotationSpeedVariation(attributeToFloat(emitterNode, "rotationSpeedVariation", particleEmitter.getRotationSpeedVariation()));
				particleEmitter.setColor(attributeToColor(emitterNode, "color", "FFFFFFFF"));

				state.addParticleEmitter(particleEmitter);
			}
		}
	}

	private void parseSetters(Node settersNode, State state) {
		for (int i = 0; i < settersNode.getChildNodes().getLength(); ++i) {
			Node setNode = settersNode.getChildNodes().item(i);
			if (setNode.getNodeName().equals("Setter")) {
				Setter setter = SetterFactory.getInstance().createFromType(attributeToString(setNode, "type", null));
				setter.setX(attributeToFloat(setNode, "x", 0));
				setter.setY(attributeToFloat(setNode, "y", 0));
				setter.setValue(attributeToString(setNode, "value", null));

				state.addSetter(setter);
			}
		}
	}

	private void parseTriggers(Node triggersNod, GameObject obj) {
		for (int i = 0; i < triggersNod.getChildNodes().getLength(); ++i) {
			Node triggerNode = triggersNod.getChildNodes().item(i);
			if (triggerNode.getNodeName().equals("Trigger")) {
				Trigger trigger = TriggerFactory.getInstance().createFromType(attributeToString(triggerNode, "type", null));
				trigger.setCurrentState(attributeToString(triggerNode, "currentState", null));
				trigger.setNextState(attributeToString(triggerNode, "nextState", null));
				obj.addTrigger(trigger);
			}
		}
	}

	private Dynamic parseDynamic(Node node) {
		Dynamic dynamic = new Dynamic();

		dynamic.setFrom(attributeToFloat(node, "from", 0));
		dynamic.setTo(attributeToFloat(node, "to", 0));
		dynamic.setFunction(attributeToString(node, "function", ""));
		dynamic.setName(attributeToString(node, "name", ""));
		dynamic.setX(attributeToFloat(node, "x", 0));
		dynamic.setY(attributeToFloat(node, "y", 0));

		return dynamic;
	}

	private void parseForces(Node forcesNode, World world) {

		for (int i = 0; i < forcesNode.getChildNodes().getLength(); ++i) {
			Node forceNode = forcesNode.getChildNodes().item(i);
			if (forceNode.getNodeName().equals("Force"))
				world.addForce(parseDynamic(forceNode));
		}
	}

	private void parseLayers(Node layersNode, World world) {

		for (int i = 0; i < layersNode.getChildNodes().getLength(); ++i) {
			Node layerNode = layersNode.getChildNodes().item(i);

			if (layerNode.getNodeName().equals("Layer")) {
				DrawLayer layer = new DrawLayer();

				layer.setLayerType(attributeToString(layerNode, "type", null));
				layer.setLayerId(attributeToInteger(layerNode, "layerId", 0));
				layer.setFromCoord(attributeToPoint(layerNode, "fromCoord", new Point3D()));
				layer.setToCoord(attributeToPoint(layerNode, "toCoord", new Point3D()));
				layer.setTextureName(attributeToString(layerNode, "textureName", null));

				world.addLayer(layer);
			}
		}
	}
}
