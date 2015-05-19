<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ns0="http://ns.electronichealth.net.au/pcehr/xsd/common/CommonCoreElements/1.0"
	xmlns:ns1="http://ns.electronichealth.net.au/pcehr/xsd/interfaces/GetPCEHRRecordList/1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions"
	exclude-result-prefixes="ns0 ns1 xs fn">
	<xsl:output method="xml" encoding="UTF-8" indent="yes" />

	<xsl:template match="ns1:responseStatus" />

	<xsl:template match="/">
		<Bundle xmlns="http://hl7.org/fhir" xmlns:xhtml="http://www.w3.org/1999/xhtml">
			<id value="getRecordList" />
			<meta>
				<lastUpdated value="2014-08-18T01:43:30Z" />
			</meta>
			<type value="getRecordList" />
			<xsl:for-each select="/ns1:getPCEHRRecordListResponse/ns1:records/ns1:PCEHRRecord">
				<entry>
					<resource xmlns="http://hl7.org/fhir">
						<xsl:call-template name="tplIndividual" />
					</resource>
				</entry>
			</xsl:for-each>
		</Bundle>
	</xsl:template>

	<xsl:template name="tplIndividual">
		<Patient>
			<identifier>
				<value>
					<xsl:attribute name="value" namespace="" select="ns1:individual/ns0:ihiNumber" />
				</value>
			</identifier>
			<name>
				<xsl:choose>
					<xsl:when test="ns1:individual/ns0:ihiNumber = /ns1:getPCEHRRecordListResponse/ns1:individual/ns0:ihiNumber">
						<use value="official" />
					</xsl:when>
					<xsl:otherwise>
						<use value="usual" />
					</xsl:otherwise>
				</xsl:choose>
				<xsl:for-each select="ns1:individual/ns0:name/ns0:familyName">
					<family>
						<xsl:attribute name="value" namespace="" select="." />
					</family>
				</xsl:for-each>
				<xsl:for-each select="ns1:individual/ns0:name/ns0:givenName">
					<given>
						<xsl:attribute name="value" namespace="" select="." />
					</given>
				</xsl:for-each>
				<prefix>
					<xsl:attribute name="value" namespace="" select="ns1:individual/ns0:name/ns0:nameTitle" />
				</prefix>
				<suffix>
					<xsl:attribute name="value" namespace="" select="ns1:individual/ns0:name/ns0:suffix" />
				</suffix>
			</name>
			<gender>
				<xsl:choose>
					<xsl:when test="ns1:individual/ns0:sex = 'M'">
						<xsl:attribute name="value" namespace="" select="'male'" />
					</xsl:when>
					<xsl:when test="ns1:individual/ns0:sex = 'F'">
						<xsl:attribute name="value" namespace="" select="'female'" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="value" namespace="" select="'unknown'" />
					</xsl:otherwise>
				</xsl:choose>
			</gender>
			<birthDate>
				<xsl:attribute name="value" namespace="" select="ns1:individual/ns0:dateOfBirth" />
			</birthDate>
			<photo>
				<xsl:choose>
					<xsl:when test="ns1:individual/ns0:ihiNumber = /ns1:getPCEHRRecordListResponse/ns1:individual/ns0:ihiNumber">
						<url value="images/a7.jpg" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="ns1:individual/ns0:sex = 'M'">
								<url value="images/a1.jpg" />
							</xsl:when>
							<xsl:when test="ns1:individual/ns0:sex = 'F'">
								<url value="images/a5.jpg" />
							</xsl:when>
							<xsl:otherwise>
								<url value="images/p3.jpg" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</photo>
			<active value="true" />
		</Patient>
	</xsl:template>

</xsl:stylesheet>
