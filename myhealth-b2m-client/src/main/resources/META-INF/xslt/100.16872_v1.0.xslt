<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:vmf="http://www.altova.com/MapForce/UDF/vmf"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:ns1="urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0"
	xmlns:ns0="urn:ihe:iti:xds-b:2007" xmlns:ns2="http://ns.electronichealth.net.au/Ci/Cda/Extensions/3.0" xmlns:ns6="urn:hl7-org:v3"
	exclude-result-prefixes="vmf xs fn ns0 ns1 ns2 ns6">

	<xsl:output method="xml" encoding="UTF-8" byte-order-mark="no" indent="yes" />

	<xsl:template match="/ns6:ClinicalDocument">
		<xsl:variable name="varEffectiveTimeTmp" select="ns6:effectiveTime/@value" />
		<xsl:variable name="varEffectiveTime"
			select="xs:dateTime(concat(substring($varEffectiveTimeTmp,1,4),'-',substring($varEffectiveTimeTmp,5,2),'-',substring($varEffectiveTimeTmp,7,2),'T',substring($varEffectiveTimeTmp,9,2),':',substring($varEffectiveTimeTmp,11,2), ':00.000' ))" />
		<Bundle xmlns="http://hl7.org/fhir" xmlns:xhtml="http://www.w3.org/1999/xhtml">
			<id>
				<xsl:attribute name="value" namespace="" select="ns6:id/@root" />
			</id>
			<meta>
				<lastUpdated>
					<xsl:attribute name="value" namespace="" select="$varEffectiveTime" />
				</lastUpdated>
			</meta>
			<type value="document" />
			<xsl:for-each select="ns6:component/ns6:structuredBody/ns6:component/ns6:section">
				<xsl:for-each select="ns6:entry">
					<xsl:for-each select="ns6:organizer">
						<xsl:for-each select="ns6:component[fn:exists(ns6:observation)]">
							<entry>
								<resource>
									<xsl:call-template name="tplObservation" />
								</resource>
							</entry>
						</xsl:for-each>
					</xsl:for-each>
				</xsl:for-each>
			</xsl:for-each>
		</Bundle>
	</xsl:template>

	<xsl:template name="tplObservationList">
		<xsl:variable name="varEffectiveTimeTmp" select="ns6:effectiveTime/@value" />
		<xsl:variable name="varEffectiveTime"
			select="xs:dateTime(concat(substring($varEffectiveTimeTmp,1,4),'-',substring($varEffectiveTimeTmp,5,2),'-',substring($varEffectiveTimeTmp,7,2),'T',substring($varEffectiveTimeTmp,9,2),':',substring($varEffectiveTimeTmp,11,2), ':00.000' ))" />

		<Observation xmlns="http://hl7.org/fhir">
			<id>
				<xsl:attribute name="value" namespace="" select="ns6:id/@root" />
			</id>
			<text>
				<status>
					<xsl:attribute name="value" namespace="" select="ns6:statusCode/@code" />
				</status>
				<div xmlns="http://www.w3.org/1999/xhtml">
				</div>
			</text>
			<contained>
				<xsl:for-each select="ns6:component[fn:exists(ns6:observation)]">
					<xsl:call-template name="tplObservation">
					</xsl:call-template>
				</xsl:for-each>
			</contained>
			<issued>
				<xsl:attribute name="value" namespace="" select="$varEffectiveTime" />
			</issued>
			<subject>
				<reference>
					<xsl:attribute name="value" namespace="" select="ns6:code/@code" />
				</reference>
			</subject>
			<status>
				<xsl:attribute name="value" namespace="" select="ns6:statusCode/@code" />
			</status>
		</Observation>
	</xsl:template>

	<xsl:template name="tplObservation">
		<xsl:variable name="varEffectiveTimeTmp" select="../ns6:effectiveTime/@value" />
		<xsl:variable name="varEffectiveTime"
			select="xs:dateTime(concat(substring($varEffectiveTimeTmp,1,4),'-',substring($varEffectiveTimeTmp,5,2),'-',substring($varEffectiveTimeTmp,7,2),'T',substring($varEffectiveTimeTmp,9,2),':',substring($varEffectiveTimeTmp,11,2), ':00.000' ))" />

		<Observation xmlns="http://hl7.org/fhir">
			<id>
				<xsl:attribute name="value" namespace="" select="../ns6:id/@root" />
			</id>
			<text>
				<status>
					<xsl:attribute name="value" namespace="" select="../ns6:statusCode/@code" />
				</status>
				<div xmlns="http://www.w3.org/1999/xhtml">
				</div>
			</text>
			<issued>
				<xsl:attribute name="value" namespace="" select="$varEffectiveTime" />
			</issued>
			<subject>
				<reference>
					<xsl:attribute name="value" namespace="" select="../ns6:code/@code" />
				</reference>
			</subject>
			<code><!-- SNOMED CT Codes -->
				<coding>
					<system value="http://snomed.info/sct" />
					<code>
						<xsl:attribute name="value" namespace="" select="ns6:observation/ns6:code/@code" />
					</code>
					<display>
						<xsl:attribute name="value" namespace="" select="ns6:observation/ns6:code/@displayName" />
					</display>
				</coding>
			</code>
			<valueQuantity>
				<value>
					<xsl:attribute name="value" namespace="" select="ns6:observation/ns6:value/@value" />
				</value>
				<units>
					<xsl:attribute name="value" namespace="" select="ns6:observation/ns6:value/@unit" />
				</units>
				<system value="http://snomed.info/sct" />
				<code>
					<xsl:attribute name="value" namespace="" select="ns6:observation/ns6:code/@code" />
				</code>
			</valueQuantity>
			<status>
				<xsl:attribute name="value" namespace="" select="../ns6:statusCode/@code" />
			</status>
			<reliability value="ok" />
		</Observation>
	</xsl:template>

</xsl:stylesheet>
