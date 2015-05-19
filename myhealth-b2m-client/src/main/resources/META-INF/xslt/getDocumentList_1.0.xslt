<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:vmf="http://www.altova.com/MapForce/UDF/vmf"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:ns0="urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0"
	xmlns:ns1="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0" exclude-result-prefixes="vmf xs fn ns0 ns1">
	<xsl:template name="vmf:vmf1_inputtoresult">
		<xsl:param name="input" select="()" />
		<xsl:choose>
			<xsl:when test="$input='urn:oasis:names:tc:ebxml-regrep:StatusType:Approved'">
				<xsl:value-of select="'normal'" />
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	<xsl:output method="xml" encoding="UTF-8" byte-order-mark="no" indent="yes" />

	<xsl:template match="/">
		<Bundle xmlns="http://hl7.org/fhir" xmlns:xhtml="http://www.w3.org/1999/xhtml">
			<id value="xds" />
			<meta>
				<lastUpdated value="2014-08-18T01:43:30Z" />
			</meta>
			<type value="getDocumentList" />
			<xsl:for-each select="/ns0:AdhocQueryResponse/ns1:RegistryObjectList/ns1:ExtrinsicObject">
				<entry>
					<resource xmlns="http://hl7.org/fhir">
						<xsl:call-template name="tplExtrinsicObject" />
					</resource>
				</entry>
			</xsl:for-each>
		</Bundle>
	</xsl:template>

	<xsl:template name="tplExtrinsicObject">
		<DocumentReference>
			<masterIdentifier>
				<system>
					<xsl:attribute name="value" namespace="" select="xs:string(xs:anyURI('urn:ietf:rfc:3986'))" />
				</system>
				<value>
					<xsl:for-each
						select="(ns1:ExternalIdentifier)[('urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab' = xs:string(xs:anyURI(fn:string(@identificationScheme))))]">
						<xsl:attribute name="value" namespace="" select="fn:string(@value)" />
					</xsl:for-each>
				</value>
			</masterIdentifier>
			<identifier>
				<use value="secondary" />
				<system>
					<xsl:attribute name="value" namespace="" select="xs:string(xs:anyURI('urn:ietf:rfc:3986'))" />
				</system>
				<value>
					<xsl:attribute name="value" namespace="" select="ns1:Slot['repositoryUniqueId' = @name]/ns1:ValueList/ns1:Value" />
				</value>
			</identifier>
			<subject>
				<reference>
					<xsl:attribute name="value" namespace="" select="ns1:Slot['sourcePatientId' = @name]/ns1:ValueList/ns1:Value" />
				</reference>
			</subject>
			<xsl:for-each select="ns1:Classification[('urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d' = @classificationScheme)]">
				<author>
					<reference>
						<xsl:attribute name="value" namespace="" select="fn:concat('', ./ns1:Slot['authorPerson' = @name]/ns1:ValueList/ns1:Value)" />
					</reference>
				</author>
				<custodian>
					<reference>
						<xsl:attribute name="value" namespace="" select="fn:concat('', ./ns1:Slot['authorInstitution' = @name]/ns1:ValueList/ns1:Value)" />
					</reference>
				</custodian>
			</xsl:for-each>
			<type>
				<coding>
					<system>
						<xsl:for-each select="ns1:Classification">
							<xsl:variable name="var2_current" as="node()" select="." />
							<xsl:for-each
								select="((ns1:Slot/ns1:ValueList/ns1:Value)[fn:exists($var2_current/@classificationScheme)])[('urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a' = xs:string(xs:anyURI(fn:string($var2_current/@classificationScheme))))]">
								<xsl:attribute name="value" namespace="" select="xs:string(xs:anyURI(fn:string(.)))" />
							</xsl:for-each>
						</xsl:for-each>
					</system>
					<code>
						<xsl:for-each
							select="(((ns1:Classification)[fn:exists(@nodeRepresentation)])[fn:exists(@classificationScheme)])[('urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a' = xs:string(xs:anyURI(fn:string(@classificationScheme))))]">
							<xsl:attribute name="value" namespace="" select="fn:string(@nodeRepresentation)" />
						</xsl:for-each>
					</code>
					<display>
						<xsl:for-each select="ns1:Classification">
							<xsl:variable name="var3_current" as="node()" select="." />
							<xsl:for-each
								select="((ns1:Name/ns1:LocalizedString)[fn:exists($var3_current/@classificationScheme)])[('urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a' = xs:string(xs:anyURI(fn:string($var3_current/@classificationScheme))))]">
								<xsl:attribute name="value" namespace="" select="fn:string(@value)" />
							</xsl:for-each>
						</xsl:for-each>
					</display>
				</coding>
			</type>
			<xsl:if test="ns1:Slot['serviceStopTime' = @name]">
				<created>
					<xsl:variable name="varStopTime" select="ns1:Slot['serviceStopTime' = @name]/ns1:ValueList/ns1:Value" />
					<xsl:attribute name="value" namespace=""
						select="xs:dateTime(concat(substring($varStopTime,1,4),'-',substring($varStopTime,5,2),'-',substring($varStopTime,7,2),'T',substring($varStopTime,9,2),':',substring($varStopTime,11,2), ':00.000' ))" />
				</created>
			</xsl:if>
			<xsl:if test="ns1:Slot['creationTime' = @name]">
				<indexed>
					<xsl:variable name="varCreated" select="ns1:Slot['creationTime' = @name]/ns1:ValueList/ns1:Value" />
					<xsl:attribute name="value" namespace=""
						select="xs:dateTime(concat(substring($varCreated,1,4),'-',substring($varCreated,5,2),'-',substring($varCreated,7,2),'T',substring($varCreated,9,2),':',substring($varCreated,11,2), ':00.000' ))" />
				</indexed>
			</xsl:if>
			<status>
				<xsl:attribute name="value" namespace="">
					<xsl:call-template name="vmf:vmf1_inputtoresult">
						<xsl:with-param name="input" select="@status" as="xs:string" />
					</xsl:call-template>
				</xsl:attribute>
			</status>
			<description>
				<xsl:choose>
					<xsl:when test="fn:exists(ns1:Name)">
						<xsl:attribute name="value" namespace="" select="ns1:Name/ns1:LocalizedString/@value" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="value" namespace="" select="ns1:Description/ns1:LocalizedString/@value" />
					</xsl:otherwise>
				</xsl:choose>
			</description>
			<format>
				<xsl:attribute name="value" namespace="" select="@mimeType" />
			</format>
		</DocumentReference>
	</xsl:template>
</xsl:stylesheet>
