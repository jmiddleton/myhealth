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
			<id><xsl:attribute name="value" namespace="" select="ns6:id/@root" /></id>
			<meta>
				<lastUpdated><xsl:attribute name="value" namespace="" select="$varEffectiveTime" /></lastUpdated>
			</meta>
			<type value="document" />
			<xsl:if test="ns6:component/ns6:structuredBody/ns6:component[ns6:section/ns6:code/@code = '101.16491']">
				<xsl:variable name="section" select="ns6:component/ns6:structuredBody/ns6:component[ns6:section/ns6:code/@code = '101.16491']/ns6:section[1]" />

				<xsl:for-each select="$section/ns6:entry[ns6:organizer/ns6:code/@code = '1']">
					<xsl:variable name="entry" select="$section/ns6:entry" />
					<entry>
						<resource>
							<xsl:call-template name="tplMeasurement">
							</xsl:call-template>
						</resource>
					</entry>
				</xsl:for-each>
			</xsl:if>
		</Bundle>
	</xsl:template>

	<xsl:template name="tplMeasurement">
		<xsl:variable name="varEffectiveTimeTmp" select="ns6:organizer/ns6:effectiveTime/@value" />
		<xsl:variable name="varEffectiveTime"
			select="xs:dateTime(concat(substring($varEffectiveTimeTmp,1,4),'-',substring($varEffectiveTimeTmp,5,2),'-',substring($varEffectiveTimeTmp,7,2),'T',substring($varEffectiveTimeTmp,9,2),':',substring($varEffectiveTimeTmp,11,2), ':00.000' ))" />

		<Observation xmlns="http://hl7.org/fhir">
			<id>
				<xsl:attribute name="value" namespace="" select="ns6:organizer/ns6:id/@root" />
			</id>
			<text>
				<status>
					<xsl:attribute name="value" namespace="" select="ns6:organizer/ns6:statusCode/@code" />
				</status>
				<div xmlns="http://www.w3.org/1999/xhtml"><!-- Snipped for brevity -->
				</div>
			</text>
			<issued>
				<xsl:attribute name="value" namespace="" select="$varEffectiveTime" />
			</issued>
			<subject>
				<reference>
					<xsl:attribute name="value" namespace="" select="/ns6:ClinicalDocument/ns6:recordTarget/ns6:patientRole/ns6:patient/ns2:asEntityIdentifier/ns2:id/@root" />
				</reference>
			</subject>
			<contained>
				<xsl:for-each select="ns6:organizer/ns6:component">
					<xsl:call-template name="tplObservation">
					</xsl:call-template>
				</xsl:for-each>
			</contained>
		</Observation>
	</xsl:template>

	<xsl:template name="tplObservation">
		<Observation xmlns="http://hl7.org/fhir">
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
			<status value="final" /><!-- The observation is complete -->
			<reliability value="ok" />
		</Observation>
	</xsl:template>

</xsl:stylesheet>
