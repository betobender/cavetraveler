package com.cave.traveler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class ScoreManager {

	private final String ScoreDeviceIdentity = "ScoreDeviceIdentity";
	private final String ScoreSecretKey = "cav33xplor3rk3y";
	private final String SharedPreferences = "com.cave.traveler.data";
	private Activity activity;

	public ScoreManager(Activity activity) {
		this.activity = activity;
	}

	public Activity getActivity() {
		return activity;
	}

	public String composeHash() {
		MessageDigest digest;

		try {
			digest = MessageDigest.getInstance("MD5");
			String preHashCode = getScoreDeviceIdentity() + ScoreSecretKey;
			digest.update(preHashCode.getBytes(), 0, preHashCode.length());
			String hash = new BigInteger(1, digest.digest()).toString(16);
			return hash;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getScoreDeviceIdentity() {
		SharedPreferences sharedPref = activity.getSharedPreferences(SharedPreferences, Context.MODE_PRIVATE);
		if (sharedPref.getString(ScoreDeviceIdentity, null) == null) {
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString(ScoreDeviceIdentity, UUID.randomUUID().toString());
			editor.commit();
		}
		return sharedPref.getString(ScoreDeviceIdentity, null);
	}

	public void scoreRetrieved(int position, int score, int distance, String playerName, boolean mySelf) {
	}

	public void scoreRetrieved() {

	}

	public void errorRetrievingScore(String errorResponse) {
	}

	public void readScore() {
		class PostRetrieveScore implements Runnable {
			public void run() {
				String url = "http://caveexplorer.mygamesonline.org/readscore.php";

				try {
					url += "?hash=" + URLEncoder.encode(composeHash(), "utf-8");
					url += "&deviceid=" + URLEncoder.encode(getScoreDeviceIdentity(), "utf-8");

					// parse xml
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder db = dbf.newDocumentBuilder();
					Document doc = db.parse(url);
					doc.getDocumentElement().normalize();

					for (int a = 0; a < doc.getChildNodes().getLength(); ++a) {
						if (doc.getChildNodes().item(a).getNodeName().equals("Results")) {
							Node resultsNode = doc.getChildNodes().item(a);
							for (int b = 0; b < resultsNode.getChildNodes().getLength(); ++b) {
								if (resultsNode.getChildNodes().item(b).getNodeName().equals("Result")) {
									Node resultNode = resultsNode.getChildNodes().item(b);

									int position = 0, score = 0, distance = 0;
									String name = "", deviceId = "";

									for (int c = 0; c < resultNode.getChildNodes().getLength(); ++c) {
										Node objectNode = resultNode.getChildNodes().item(c);

										if (objectNode.getNodeName().equals("Position"))
											position = Integer.parseInt(objectNode.getTextContent());
										else if (objectNode.getNodeName().equals("Score"))
											score = Integer.parseInt(objectNode.getTextContent());
										else if (objectNode.getNodeName().equals("Distance"))
											distance = Integer.parseInt(objectNode.getTextContent());
										else if (objectNode.getNodeName().equals("Name"))
											name = objectNode.getTextContent();
										else if (objectNode.getNodeName().equals("DeviceId"))
											deviceId = objectNode.getTextContent();
									}
									
									scoreRetrieved(position, score, distance, name, deviceId.equals(getScoreDeviceIdentity()));
								}
							}
						}
					}

					scoreRetrieved();

				} catch (UnsupportedEncodingException e) {
					errorRetrievingScore("unknown error");
				} catch (ClientProtocolException e) {
					errorRetrievingScore("unknown error");
				} catch (IOException e) {
					errorRetrievingScore("unknown error");
				} catch (ParserConfigurationException e) {
					errorRetrievingScore("unknown error");
				} catch (SAXException e) {
					errorRetrievingScore("unknown error");
				}

			}
		}

		new Thread(new PostRetrieveScore()).start();
	}

	public void scoreSaved() {
	}

	public void errorSavingScore(String errorResponse) {
	}

	public void saveScore(String playerName, int score, int distance, String version) {

		class PostScoreRunnable implements Runnable {

			public PostScoreRunnable(String playerName, int score, int distance, String version) {
				super();
				this.playerName = playerName;
				this.score = score;
				this.distance = distance;
				this.version = version;
			}

			private String playerName;
			private int score;
			private int distance;
			private String version;

			public void run() {
				HttpClient httpclient = new DefaultHttpClient();
				String url = "http://caveexplorer.mygamesonline.org/savescore.php";

				try {
					url += "?hash=" + URLEncoder.encode(composeHash(), "utf-8");
					url += "&deviceid=" + URLEncoder.encode(getScoreDeviceIdentity(), "utf-8");
					url += "&name=" + URLEncoder.encode(playerName, "utf-8");
					url += "&score=" + URLEncoder.encode("" + score, "utf-8");
					url += "&distance=" + URLEncoder.encode("" + distance, "utf-8");
					url += "&version=" + URLEncoder.encode(version, "utf-8");
					HttpPost httppost = new HttpPost(url);
					HttpResponse response = httpclient.execute(httppost);

					if (null != response) {
						String responseString = EntityUtils.toString(response.getEntity());
						if (responseString.startsWith("DATA STORED")) {
							scoreSaved();
						} else {
							errorSavingScore(responseString);
						}

					}
				} catch (UnsupportedEncodingException e) {
					errorSavingScore("unknown error");
				} catch (ClientProtocolException e) {
					errorSavingScore("unknown error");
				} catch (IOException e) {
					errorSavingScore("unknown error");
				}
			}
		}

		new Thread(new PostScoreRunnable(playerName, score, distance, version)).start();
	}
}
