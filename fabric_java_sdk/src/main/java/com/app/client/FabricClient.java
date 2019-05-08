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
package com.app.client;

import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import java.lang.reflect.InvocationTargetException;

/**
 * Wrapper class for HFClient.
 * 
 * @author Balaji Kadambi
 *
 */

public class FabricClient {

	private HFClient instance;

	/**
	 * Return an instance of HFClient.
	 * 
	 * @return
	 */
	public HFClient getInstance() {
		return instance;
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 * @throws CryptoException
	 * @throws InvalidArgumentException
	 * @throws InvocationTargetException 
	 * @throws NoSuchMethodException 
	 * @throws ClassNotFoundException 
	 * @throws InstantiationException 
	 * @throws IllegalAccessException 
	 */
	public FabricClient(User context) throws CryptoException, InvalidArgumentException, IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
		CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
		// setup the client
		instance = HFClient.createNewInstance();
		instance.setCryptoSuite(cryptoSuite);
		instance.setUserContext(context);
	}

	/**
	 * Create a channel client.
	 * 
	 * @param name
	 * @return
	 * @throws InvalidArgumentException
	 */
	public ChannelClient createChannelClient(String name) throws InvalidArgumentException {
		Channel channel = instance.newChannel(name);
		ChannelClient client = new ChannelClient(name, channel, this);
		return client;
	}


}
