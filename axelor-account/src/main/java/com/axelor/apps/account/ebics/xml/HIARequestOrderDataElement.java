/* Copyright (c) 1990-2012 kopiLeft Development SARL, Bizerte, Tunisia
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */

package com.axelor.apps.account.ebics.xml;

import java.math.BigInteger;
import java.util.Calendar;

import com.axelor.apps.account.ebics.schema.h003.AuthenticationPubKeyInfoType;
import com.axelor.apps.account.ebics.schema.h003.EncryptionPubKeyInfoType;
import com.axelor.apps.account.ebics.schema.h003.HIARequestOrderDataType;
import com.axelor.apps.account.ebics.schema.h003.PubKeyValueType;
import com.axelor.apps.account.ebics.schema.xmldsig.RSAKeyValueType;
import com.axelor.apps.account.ebics.schema.xmldsig.X509DataType;

import com.axelor.apps.account.ebics.client.DefaultEbicsRootElement;
import com.axelor.apps.account.ebics.client.EbicsSession;


/**
 * The <code>HIARequestOrderDataElement</code> is the element that contains
 * X002 and E002 keys information needed for a HIA request in order to send
 * the authentication and encryption user keys to the bank server.
 *
 * @author hachani
 *
 */
public class HIARequestOrderDataElement extends DefaultEbicsRootElement {

  /**
   * Constructs a new HIA Request Order Data element
   * @param session the current ebics session
   */
  public HIARequestOrderDataElement(EbicsSession session) {
    super(session);
  }

  @Override
  public void build() {
    HIARequestOrderDataType		request;
    AuthenticationPubKeyInfoType 	authenticationPubKeyInfo;
    EncryptionPubKeyInfoType 		encryptionPubKeyInfo;
    PubKeyValueType		 	encryptionPubKeyValue;
    X509DataType 			encryptionX509Data;
    RSAKeyValueType 			encryptionRsaKeyValue;
    PubKeyValueType		 	authPubKeyValue;
    X509DataType 			authX509Data;
    RSAKeyValueType 			AuthRsaKeyValue;

    encryptionX509Data = EbicsXmlFactory.createX509DataType(session.getUser().getDn(),
	                                                    session.getUser().getE002Certificate());
    encryptionRsaKeyValue = EbicsXmlFactory.createRSAKeyValueType( new BigInteger(session.getUser().getE002PublicKeyExponent()).toByteArray(),
	                                                         new BigInteger( session.getUser().getE002PublicKeyModulus()).toByteArray());
    encryptionPubKeyValue = EbicsXmlFactory.createH003PubKeyValueType(encryptionRsaKeyValue, Calendar.getInstance());
    encryptionPubKeyInfo = EbicsXmlFactory.createEncryptionPubKeyInfoType("E002",
	                                                                  encryptionPubKeyValue,
	                                                                  encryptionX509Data);
    authX509Data = EbicsXmlFactory.createX509DataType(session.getUser().getDn(),
	                                              session.getUser().getX002Certificate());
    AuthRsaKeyValue = EbicsXmlFactory.createRSAKeyValueType( new BigInteger(session.getUser().getX002PublicKeyExponent()).toByteArray(),
							    new BigInteger (session.getUser().getX002PublicKeyModulus()).toByteArray());
    authPubKeyValue = EbicsXmlFactory.createH003PubKeyValueType(AuthRsaKeyValue, Calendar.getInstance());
    authenticationPubKeyInfo = EbicsXmlFactory.createAuthenticationPubKeyInfoType("X002",
	                                                                          authPubKeyValue,
	                                                                          authX509Data);
    request = EbicsXmlFactory.createHIARequestOrderDataType(authenticationPubKeyInfo,
	                                                    encryptionPubKeyInfo,
	                                                    session.getUser().getEbicsPartner().getPartnerId(),
	                                                    session.getUser().getUserId());
    document = EbicsXmlFactory.createHIARequestOrderDataDocument(request);
  }

  @Override
  public String getName() {
    return "HIARequestOrderData.xml";
  }

  @Override
  public byte[] toByteArray() {
    addNamespaceDecl("ds", "http://www.w3.org/2000/09/xmldsig#");
    setSaveSuggestedPrefixes("http://www.ebics.org/S001", "");

    return super.toByteArray();
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

}
