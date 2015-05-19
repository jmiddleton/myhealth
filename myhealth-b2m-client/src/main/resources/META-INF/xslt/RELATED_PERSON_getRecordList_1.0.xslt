<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ns0="http://ns.electronichealth.net.au/pcehr/xsd/common/CommonCoreElements/1.0"
	xmlns:ns1="http://ns.electronichealth.net.au/pcehr/xsd/interfaces/GetPCEHRRecordList/1.0"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions"
	exclude-result-prefixes="ns0 ns1 xs fn">
	<xsl:output method="xml" encoding="UTF-8" indent="yes" />

	<xsl:template match="ns1:responseStatus" />

	<xsl:template match="/">
		<Bundle xmlns="http://hl7.org/fhir" xmlns:xhtml="http://www.w3.org/1999/xhtml">
			<id value="getRecordList" />
			<meta>
				<lastUpdated value="2014-08-18T01:43:30Z" />
			</meta>
			<type value="searchset" />
			<xsl:for-each
				select="/ns1:getPCEHRRecordListResponse/ns1:records/ns1:PCEHRRecord">
				<entry>
					<resource xmlns="http://hl7.org/fhir">
						<xsl:call-template name="tplIndividual" />
					</resource>
				</entry>
			</xsl:for-each>
		</Bundle>
	</xsl:template>

	<xsl:template name="tplIndividual">
		<RelatedPerson>
			<identifier>
				<value>
					<xsl:attribute name="value" namespace=""
						select="ns1:individual/ns0:ihiNumber" />
				</value>
			</identifier>
			<patient>
				<reference>
					<xsl:attribute name="value" namespace=""
						select="ns1:individual/ns0:ihiNumber" />
				</reference>
			</patient>
			<relationship>
				<xsl:if test="ns1:individual/ns0:ihiNumber = /ns1:getPCEHRRecordListResponse/ns1:individual/ns0:ihiNumber">
				<coding>
					<system value="http://hl7.org/fhir/v3/RoleCode" />
					<code value="ONESELF" />
				</coding>
				</xsl:if>
				<xsl:if test="ns1:individual/ns0:ihiNumber != /ns1:getPCEHRRecordListResponse/ns1:individual/ns0:ihiNumber">
					<coding>
					<system value="http://hl7.org/fhir/patient-contact-relationship" />
					<code value="parent" />
				</coding>
				</xsl:if>
			</relationship>
			<name>
				<use>usual</use>
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
					<xsl:attribute name="value" namespace=""
						select="ns1:individual/ns0:name/ns0:nameTitle" />
				</prefix>
				<suffix>
					<xsl:attribute name="value" namespace=""
						select="ns1:individual/ns0:name/ns0:suffix" />
				</suffix>
			</name>
			<gender>
				<coding>
					<system value="http://hl7.org/fhir/v3/AdministrativeGender" />
					<code>
						<xsl:attribute name="value" namespace=""
							select="ns1:individual/ns0:sex" />
					</code>
					<display>
						<xsl:if test="ns1:individual/ns0:sex = 'M'">
							<xsl:attribute name="value" namespace="" select="'Male'" />
						</xsl:if>
						<xsl:if test="ns1:individual/ns0:sex = 'F'">
							<xsl:attribute name="value" namespace="" select="'Female'" />
						</xsl:if>
					</display>
				</coding>
			</gender>
		</RelatedPerson>
	</xsl:template>

</xsl:stylesheet>
