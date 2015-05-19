<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:vmf="http://www.altova.com/MapForce/UDF/vmf"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:ns1="urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0"
	xmlns:ns0="urn:ihe:iti:xds-b:2007" xmlns:ns2="http://ns.electronichealth.net.au/Ci/Cda/Extensions/3.0" xmlns:ns6="urn:hl7-org:v3"
	exclude-result-prefixes="vmf xs fn ns0 ns1 ns2 ns6">

	<xsl:output method="xml" encoding="UTF-8" byte-order-mark="no" indent="yes" />

	<xsl:template match="/">
		<Bundle xmlns="http://hl7.org/fhir" xmlns:xhtml="http://www.w3.org/1999/xhtml">
			<id value="100.16685" />
			<meta>
				<lastUpdated value="2014-08-18T01:43:30Z" />
			</meta>
			<type value="document" />
			<xsl:for-each select="/ns6:ClinicalDocument">
				<xsl:variable name="varStopTime" select="ns6:effectiveTime/@value" />
				<xsl:variable name="effectiveTimeConverted"
					select="xs:dateTime(concat(substring($varStopTime,1,4),'-',substring($varStopTime,5,2),'-',substring($varStopTime,7,2),'T',substring($varStopTime,9,2),':',substring($varStopTime,11,2), ':00.000' ))" />

				<entry>
					<resource>
						<Composition>
							<id>
								<xsl:attribute name="value" namespace="" select="ns6:id/@root" />
							</id>
							<meta>
								<lastUpdated>
									<xsl:attribute name="value" namespace="" select="$effectiveTimeConverted" />
								</lastUpdated>
							</meta>
							<date>
								<xsl:attribute name="value" namespace="" select="$effectiveTimeConverted" />
							</date>
							<xsl:if test="ns2:completionCode/@code = 'F'">
								<status value="final" />
							</xsl:if>
							<xsl:if test="ns2:completionCode/@code != 'F'">
								<status value="other" />
							</xsl:if>

							<xsl:if test="ns6:component/ns6:structuredBody/ns6:component/ns6:section[ns6:code/@code = '101.20113']">
								<xsl:variable name="allergy" as="node()" select="ns6:component/ns6:structuredBody/ns6:component/ns6:section[ns6:code/@code = '101.20113']" />
								<section>
									<title>
										<xsl:attribute name="value" namespace="" select="$allergy/ns6:code/@displayName" />
									</title>
									<content>
										<reference>
											<xsl:attribute name="value" namespace="" select="$allergy/ns6:code/@code" />
										</reference>
									</content>
								</section>
							</xsl:if>
							<xsl:if test="ns6:component/ns6:structuredBody/ns6:component/ns6:section[ns6:code/@code = '101.16146']">
								<xsl:variable name="medication" as="node()" select="ns6:component/ns6:structuredBody/ns6:component/ns6:section[ns6:code/@code = '101.16146']" />
								<section>
									<title>
										<xsl:attribute name="value" namespace="" select="$medication/ns6:code/@displayName" />
									</title>
									<content>
										<reference>
											<xsl:attribute name="value" namespace="" select="$medication/ns6:code/@code" />
										</reference>
									</content>
								</section>
							</xsl:if>
						</Composition>
					</resource>
				</entry>
				<xsl:if test="ns6:component/ns6:structuredBody/ns6:component/ns6:section[ns6:code/@code = '101.20113']">
					<xsl:variable name="section" select="ns6:component/ns6:structuredBody/ns6:component/ns6:section[ns6:code/@code = '101.20113']" />
					<entry>
						<resource xmlns="http://hl7.org/fhir">
							<xsl:call-template name="tplListAllergies">
								<xsl:with-param name="code" select="$section/ns6:code/@code" />
								<xsl:with-param name="entries" select="$section/ns6:entry" />
								<xsl:with-param name="recordedDate" select="$effectiveTimeConverted" />
							</xsl:call-template>
						</resource>
					</entry>
					<xsl:for-each select="$section/ns6:entry">
						<xsl:variable name="entry" select="." />
						<entry>
							<resource xmlns="http://hl7.org/fhir">
								<xsl:call-template name="tplAllergy">
									<xsl:with-param name="entry" select="$entry" />
									<xsl:with-param name="recordedDate" select="$effectiveTimeConverted" />
								</xsl:call-template>
							</resource>
						</entry>
					</xsl:for-each>
				</xsl:if>

				<xsl:if test="ns6:component/ns6:structuredBody/ns6:component/ns6:section[ns6:code/@code = '101.16146']">
					<xsl:variable name="section" select="ns6:component/ns6:structuredBody/ns6:component/ns6:section[ns6:code/@code = '101.16146']" />
					<entry>
						<resource xmlns="http://hl7.org/fhir">
							<xsl:call-template name="tplListMedications">
								<xsl:with-param name="code" select="$section/ns6:code/@code" />
								<xsl:with-param name="entries" select="$section/ns6:entry" />
								<xsl:with-param name="recordedDate" select="$effectiveTimeConverted" />
							</xsl:call-template>
						</resource>
					</entry>
					<xsl:for-each select="$section/ns6:entry">
						<xsl:variable name="entry" select="." />
						<entry>
							<resource xmlns="http://hl7.org/fhir">
								<xsl:call-template name="tplMedication">
									<xsl:with-param name="entry" select="$entry" />
									<xsl:with-param name="recordedDate" select="$effectiveTimeConverted" />
								</xsl:call-template>
							</resource>
						</entry>
					</xsl:for-each>
				</xsl:if>
			</xsl:for-each>
		</Bundle>
	</xsl:template>

	<xsl:template name="tplListAllergies">
		<xsl:param name="code" />
		<xsl:param name="entries" />
		<xsl:param name="recordedDate" />

		<List xmlns="http://hl7.org/fhir">
			<id>
				<xsl:attribute name="value" namespace="" select="$code" />
			</id>
			<meta>
				<lastUpdated>
					<xsl:attribute name="value" namespace="" select="$recordedDate" />
				</lastUpdated>
			</meta>
			<code>
				<coding>
					<system value="http://loinc.org" />
					<code value="48765-2" />
					<display value="Allergies and adverse reactions Document" />
				</coding>
			</code>
			<status value="current" />
			<mode value="working" />
			<xsl:for-each select="$entries">
				<xsl:variable name="entry" select="." />
				<entry>
					<item>
						<reference>
							<xsl:attribute name="value" namespace="" select="$entry/ns6:act/ns6:id/@root" />
						</reference>
					</item>
				</entry>
			</xsl:for-each>
		</List>
	</xsl:template>

	<xsl:template name="tplAllergy">
		<xsl:param name="entry" />
		<xsl:param name="recordedDate" />

		<AllergyIntolerance xmlns="http://hl7.org/fhir">
			<id>
				<xsl:attribute name="value" namespace="" select="$entry/ns6:act/ns6:id/@root" />
			</id>
			<recordedDate>
				<xsl:attribute name="value" namespace="" select="$recordedDate" />
			</recordedDate>
			<substance>
				<coding>
					<system value="http://www.nlm.nih.gov/research/umls/rxnorm" />
					<code value="314422" />
					<display>
						<xsl:attribute name="value" namespace=""
							select="$entry/ns6:act/ns6:participant/ns6:participantRole/ns6:playingEntity/ns6:code/ns6:originalText" />
					</display>
				</coding>
			</substance>
			<status value="unconfirmed" />
			<criticality value="high" />
			<category value="medication" />
			<event>
				<manifestation>
					<coding>
						<system value="http://snomed.info/sct" />
						<code value="247472004" />
						<display>
							<xsl:attribute name="value" namespace=""
								select="$entry/ns6:act/ns6:entryRelationship/ns6:observation/ns6:entryRelationship/ns6:observation/ns6:code/ns6:originalText" />
						</display>
					</coding>
				</manifestation>
			</event>
		</AllergyIntolerance>
	</xsl:template>


	<xsl:template name="tplListMedications">
		<xsl:param name="code" />
		<xsl:param name="entries" />
		<xsl:param name="recordedDate" />

		<List xmlns="http://hl7.org/fhir">
			<id>
				<xsl:attribute name="value" namespace="" select="$code" />
			</id>
			<meta>
				<lastUpdated>
					<xsl:attribute name="value" namespace="" select="$recordedDate" />
				</lastUpdated>
			</meta>
			<code>
				<coding>
					<system value="http://loinc.org" />
					<code value="10183-2" />
					<display value="Hospital medications" />
				</coding>
			</code>
			<status value="current" />
			<mode value="working" />
			<xsl:for-each select="$entries">
				<xsl:variable name="entry" select="." />
				<entry>
					<item>
						<reference>
							<xsl:attribute name="value" namespace="" select="$entry/ns6:act/ns6:id/@root" />
						</reference>
					</item>
				</entry>
			</xsl:for-each>
		</List>
	</xsl:template>

	<xsl:template name="tplMedication">
		<xsl:param name="entry" />
		<xsl:param name="recordedDate" />

		<MedicationPrescription xmlns="http://hl7.org/fhir">
			<id>
				<xsl:attribute name="value" namespace="" select="$entry/ns6:act/ns6:id/@root" />
			</id>
			<contained>
				<Medication>
					<id>
						<xsl:attribute name="value" namespace="" select="$entry/ns6:substanceAdministration/ns6:id/@root" />
					</id>
					<name>
						<xsl:attribute name="value" namespace=""
							select="fn:concat($entry/ns6:substanceAdministration/ns6:consumable/ns6:manufacturedProduct/ns6:manufacturedMaterial/ns6:code/ns6:originalText, ' ', $entry/ns6:substanceAdministration/ns6:text)" />
					</name>
					<code>
						<coding>
							<system value="http://snomed.info/sct" />
							<code value="66493003" />
						</coding>
					</code>
				</Medication>
			</contained>
			<dateWritten>
				<xsl:attribute name="value" namespace="" select="$recordedDate" />
			</dateWritten>
			<medication>
				<reference>
					<xsl:attribute name="value" namespace="" select="$entry/ns6:substanceAdministration/ns6:id/@root" />
				</reference>
				<display>
					<xsl:attribute name="value" namespace=""
						select="fn:concat($entry/ns6:substanceAdministration/ns6:consumable/ns6:manufacturedProduct/ns6:manufacturedMaterial/ns6:code/ns6:originalText, ' ', $entry/ns6:substanceAdministration/ns6:text)" />
				</display>
			</medication>
			<xsl:if test="$entry/ns6:substanceAdministration/ns6:entryRelationship/ns6:act[ns6:code/@code = '103.10141']">
				<dosageInstruction>
					<text>
						<xsl:attribute name="value" namespace=""
							select="$entry/ns6:substanceAdministration/ns6:entryRelationship/ns6:act[ns6:code/@code = '103.10141']/ns6:text" />
					</text>
				</dosageInstruction>
			</xsl:if>
		</MedicationPrescription>
	</xsl:template>
</xsl:stylesheet>
