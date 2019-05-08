/****************************************************** 
 *  Copyright 2018 IBM Corporation 
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 *  you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at 
 *  http://www.apache.org/licenses/LICENSE-2.0 
 *  Unless required by applicable law or agreed to in writing, software 
 *  distributed under the License is distributed on an "AS IS" BASIS, 
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 *  See the License for the specific language governing permissions and 
 *  limitations under the License.
 */

import com.app.bean.FBInvokeRes;
import org.apache.commons.io.IOUtils;
import com.app.chaincode.invocation.FbClient;
import com.app.client.FabricClient;
import com.app.config.Config;
import com.app.user.UserContext;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;

import java.io.*;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;


/**
 * 
 * @author Balaji Kadambi
 *
 */

public class InvokeChaincodeTest {

	private static final byte[] EXPECTED_EVENT_DATA = "!".getBytes(UTF_8);
	private static final String EXPECTED_EVENT_NAME = "event";

	public static void main(String args[]) {
		try {
			FbClient fb = new FbClient("src/test/crypto-config/peerOrganizations/org2.example.com/users/Admin@org2.example.com/msp/keystore","src/test/crypto-config/peerOrganizations/org2.example.com/users/Admin@org2.example.com/msp/signcerts/Admin@org2.example.com-cert.pem");
			UserContext userContext = fb.initUserContext("admin","org1","Org1MSP");
			FabricClient fabClient = new FabricClient(userContext);
			Properties prop1 =new Properties();
			prop1.setProperty("hostnameOverride", "orderer.example.com");
			prop1.setProperty("sslProvider", "openSSL");
			prop1.setProperty("negotiationType", "TLS");
			prop1.setProperty("trustServerCertificate", "true");
			File ordererCert1 = Paths.get("D:/workspace/javasdk/src/test/crypto-config/ordererOrganizations/example.com/orderers/orderer.example.com/tls/server.crt").toFile();
			prop1.setProperty("pemFile", ordererCert1.getAbsolutePath());
			Orderer orderer = fabClient.getInstance().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL,prop1);
			Properties prop =new Properties();
			prop.setProperty("hostnameOverride", "peer0.org1.example.com");
			prop.setProperty("sslProvider", "openSSL");
			prop.setProperty("negotiationType", "TLS");
			prop.setProperty("trustServerCertificate", "true");
			File ordererCert = Paths.get("D:/workspace/javasdk/src/test/crypto-config/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/server.crt").toFile();
			prop.setProperty("pemFile", ordererCert.getAbsolutePath());
			Peer peer = fabClient.getInstance().newPeer(Config.ORG1_PEER_0, Config.ORG1_PEER_0_URL,prop);
			Properties prop2 =new Properties();
			prop2.setProperty("hostnameOverride", "peer0.org2.example.com");
			prop2.setProperty("sslProvider", "openSSL");
			prop2.setProperty("negotiationType", "TLS");
			prop2.setProperty("trustServerCertificate", "true");
			File peerCert2 = Paths.get("D:/workspace/javasdk/src/test/crypto-config/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/server.crt").toFile();
			prop2.setProperty("pemFile", peerCert2.getAbsolutePath());
			Peer peer2 = fabClient.getInstance().newPeer(Config.ORG2_PEER_0, Config.ORG2_PEER_0_URL,prop2);
			FBInvokeRes res = fb.invoke(Config.CHANNEL_NAME,"loc","invoke",new String[] { "a", "b", "11" });
			System.out.println("invoke结果"+res.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static UserContext initAdminUser(File privateKeyFile,
												  File certificateFile) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
		try {
			// Try to get the SampleUser state from the cache
			UserContext sampleUser = null;

			// Create the SampleUser and try to restore it's state from the key value store (if found).
			sampleUser = new  UserContext();
			sampleUser.setName("admin");
			sampleUser.setAffiliation("org2");
			sampleUser.setMspId("Org2MSP");
			String certificate = new String(IOUtils.toByteArray(new FileInputStream(certificateFile)), "UTF-8");

			PrivateKey privateKey = getPrivateKeyFromBytes(IOUtils.toByteArray(new FileInputStream(privateKeyFile)));

			sampleUser.setEnrollment(new InvokeChaincodeTest.SampleStoreEnrollement(privateKey, certificate));

			//sampleUser.saveState();

			return sampleUser;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw e;
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			throw e;
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			throw e;
		} catch (ClassCastException e) {
			e.printStackTrace();
			throw e;
		}

	}

	static PrivateKey getPrivateKeyFromBytes(byte[] data) throws IOException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
		final Reader pemReader = new StringReader(new String(data));

		final PrivateKeyInfo pemPair;
		try (PEMParser pemParser = new PEMParser(pemReader)) {
			pemPair = (PrivateKeyInfo) pemParser.readObject();
		}

		//PrivateKey privateKey = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME).getPrivateKey(pemPair);
		PrivateKey privateKey = new JcaPEMKeyConverter().setProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()).getPrivateKey(pemPair);
		return privateKey;
	}

	// Use this to make sure SDK is not dependent on HFCA enrollment for non-Idemix
	static final class SampleStoreEnrollement implements Enrollment, Serializable {

		private static final long serialVersionUID = -2784835212445309006L;
		private final PrivateKey privateKey;
		private final String certificate;

		SampleStoreEnrollement(PrivateKey privateKey, String certificate) {

			this.certificate = certificate;

			this.privateKey = privateKey;
		}

		@Override
		public PrivateKey getKey() {

			return privateKey;
		}

		@Override
		public String getCert() {
			return certificate;
		}

	}

	static File findFileSk(String directorys) {

		File directory = new File(directorys);

		File[] matches = directory.listFiles((dir, name) -> name.endsWith("_sk"));

		if (null == matches) {
			throw new RuntimeException(format("Matches returned null does %s directory exist?", directory.getAbsoluteFile().getName()));
		}

		if (matches.length != 1) {
			throw new RuntimeException(format("Expected in %s only 1 sk file but found %d", directory.getAbsoluteFile().getName(), matches.length));
		}

		return matches[0];

	}
}
